package dev.slimevr.bridge;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.Main;
import dev.slimevr.bridge.ProtobufMessages.TrackerStatus;
import dev.slimevr.bridge.ProtobufMessages.*;
import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.vr.trackers.*;
import io.eiren.util.ann.Synchronize;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.collections.FastList;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;


public abstract class ProtobufBridge<T extends VRTracker> implements Bridge {

	@VRServerThread
	protected final List<ShareableTracker> sharedTrackers = new FastList<>();
	protected final String bridgeName;
	private final Vector3f vec1 = new Vector3f();
	private final Quaternion quat1 = new Quaternion();
	@ThreadSafe
	private final Queue<ProtobufMessage> inputQueue = new LinkedBlockingQueue<>();
	@ThreadSafe
	private final Queue<ProtobufMessage> outputQueue = new LinkedBlockingQueue<>();
	@Synchronize("self")
	private final Map<String, T> remoteTrackersBySerial = new HashMap<>();
	@Synchronize("self")
	private final Map<Integer, T> remoteTrackersByTrackerId = new HashMap<>();
	private final HMDTracker hmd;
	private boolean hadNewData = false;
	private T hmdTracker;

	public ProtobufBridge(String bridgeName, HMDTracker hmd) {
		this.bridgeName = bridgeName;
		this.hmd = hmd;
	}

	@BridgeThread
	protected abstract boolean sendMessageReal(ProtobufMessage message);

	@BridgeThread
	protected void messageReceived(ProtobufMessage message) {
		inputQueue.add(message);
	}

	@ThreadSafe
	protected void sendMessage(ProtobufMessage message) {
		outputQueue.add(message);
	}

	@BridgeThread
	protected void updateMessageQueue() {
		ProtobufMessage message = null;
		while ((message = outputQueue.poll()) != null) {
			if (!sendMessageReal(message))
				return;
		}
	}

	@VRServerThread
	@Override
	public void dataRead() {
		hadNewData = false;
		ProtobufMessage message = null;
		while ((message = inputQueue.poll()) != null) {
			processMessageReceived(message);
			hadNewData = true;
		}
		if (hadNewData && hmdTracker != null) {
			trackerOverrideUpdate(hmdTracker, hmd);
		}
	}

	@VRServerThread
	protected void trackerOverrideUpdate(T source, ComputedTracker target) {
		target.position.set(source.position);
		target.rotation.set(source.rotation);
		target.setStatus(source.getStatus());
		target.dataTick();
	}

	@VRServerThread
	@Override
	public void dataWrite() {
		if (!hadNewData) // Don't write anything if no message were received, we
							// always process at the
			// speed of the other side
			return;
		for (ShareableTracker tracker : sharedTrackers) {
			writeTrackerUpdate(tracker);
		}
	}

	@VRServerThread
	protected void writeTrackerUpdate(ShareableTracker localTracker) {
		Position.Builder builder = Position.newBuilder().setTrackerId(localTracker.getTrackerId());
		if (localTracker.getPosition(vec1)) {
			builder.setX(vec1.x);
			builder.setY(vec1.y);
			builder.setZ(vec1.z);
		}
		if (localTracker.getRotation(quat1)) {
			builder.setQx(quat1.getX());
			builder.setQy(quat1.getY());
			builder.setQz(quat1.getZ());
			builder.setQw(quat1.getW());
		}
		sendMessage(ProtobufMessage.newBuilder().setPosition(builder).build());
	}

	@VRServerThread
	protected void processMessageReceived(ProtobufMessage message) {
		// if(!message.hasPosition())
		// LogManager.log.info("[" + bridgeName + "] MSG: " + message);
		if (message.hasPosition()) {
			positionReceived(message.getPosition());
		} else if (message.hasUserAction()) {
			userActionReceived(message.getUserAction());
		} else if (message.hasTrackerStatus()) {
			trackerStatusReceived(message.getTrackerStatus());
		} else if (message.hasTrackerAdded()) {
			trackerAddedReceived(message.getTrackerAdded());
		}
	}

	@VRServerThread
	protected void positionReceived(Position positionMessage) {
		T tracker = getInternalRemoteTrackerById(positionMessage.getTrackerId());
		if (tracker != null) {
			if (positionMessage.hasX())
				tracker.position
					.set(positionMessage.getX(), positionMessage.getY(), positionMessage.getZ());
			tracker.rotation
				.set(
					positionMessage.getQx(),
					positionMessage.getQy(),
					positionMessage.getQz(),
					positionMessage.getQw()
				);
			tracker.dataTick();
		}
	}

	@VRServerThread
	protected abstract T createNewTracker(TrackerAdded trackerAdded);

	@VRServerThread
	protected void trackerAddedReceived(TrackerAdded trackerAdded) {
		T tracker = getInternalRemoteTrackerById(trackerAdded.getTrackerId());
		if (tracker != null) {
			// TODO reinit?
			return;
		}
		tracker = createNewTracker(trackerAdded);
		synchronized (remoteTrackersBySerial) {
			remoteTrackersBySerial.put(tracker.getName(), tracker);
		}
		synchronized (remoteTrackersByTrackerId) {
			remoteTrackersByTrackerId.put(tracker.getTrackerId(), tracker);
		}
		if (trackerAdded.getTrackerRole() == TrackerRole.HMD.id) {
			hmdTracker = tracker;
		} else {
			Main.vrServer.registerTracker(tracker);
		}
	}

	@VRServerThread
	protected void userActionReceived(UserAction userAction) {
		switch (userAction.getName()) {
			case "calibrate":
				// TODO : Check pose field
				Main.vrServer.resetTrackers();
				break;
		}
	}

	@VRServerThread
	protected void trackerStatusReceived(TrackerStatus trackerStatus) {
		T tracker = getInternalRemoteTrackerById(trackerStatus.getTrackerId());
		if (tracker != null) {
			tracker
				.setStatus(
					dev.slimevr.vr.trackers.TrackerStatus.getById(trackerStatus.getStatusValue())
				);
		}
	}

	@ThreadSafe
	protected T getInternalRemoteTrackerById(int trackerId) {
		synchronized (remoteTrackersByTrackerId) {
			return remoteTrackersByTrackerId.get(trackerId);
		}
	}

	@VRServerThread
	protected void reconnected() {
		for (ShareableTracker tracker : sharedTrackers) {
			TrackerAdded.Builder builder = TrackerAdded
				.newBuilder()
				.setTrackerId(tracker.getTrackerId())
				.setTrackerName(tracker.getDescriptiveName())
				.setTrackerSerial(tracker.getName())
				.setTrackerRole(tracker.getTrackerRole().id);
			sendMessage(ProtobufMessage.newBuilder().setTrackerAdded(builder).build());
		}
	}

	@VRServerThread
	protected void disconnected() {
		synchronized (remoteTrackersByTrackerId) {
			Iterator<Entry<Integer, T>> iterator = remoteTrackersByTrackerId.entrySet().iterator();
			while (iterator.hasNext()) {
				iterator
					.next()
					.getValue()
					.setStatus(dev.slimevr.vr.trackers.TrackerStatus.DISCONNECTED);
			}
		}
		if (hmdTracker != null) {
			hmd.setStatus(dev.slimevr.vr.trackers.TrackerStatus.DISCONNECTED);
		}
	}

	@VRServerThread
	@Override
	public void addSharedTracker(ShareableTracker tracker) {
		if (sharedTrackers.contains(tracker))
			return;
		sharedTrackers.add(tracker);
		TrackerAdded.Builder builder = TrackerAdded
			.newBuilder()
			.setTrackerId(tracker.getTrackerId())
			.setTrackerName(tracker.getDescriptiveName())
			.setTrackerSerial(tracker.getName())
			.setTrackerRole(tracker.getTrackerRole().id);
		sendMessage(ProtobufMessage.newBuilder().setTrackerAdded(builder).build());
	}

	@VRServerThread
	@Override
	public void removeSharedTracker(ShareableTracker tracker) {
		sharedTrackers.remove(tracker);
		// No message can be sent to the remote side, protocol doesn't support
		// tracker
		// removal (yet)
	}
}
