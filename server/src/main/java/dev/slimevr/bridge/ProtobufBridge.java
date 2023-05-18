package dev.slimevr.bridge;

import dev.slimevr.Main;
import dev.slimevr.bridge.ProtobufMessages.*;
import dev.slimevr.tracking.trackers.Tracker;
import dev.slimevr.tracking.trackers.TrackerPosition;
import dev.slimevr.tracking.trackers.TrackerRole;
import dev.slimevr.tracking.trackers.TrackerUtils;
import dev.slimevr.util.ann.VRServerThread;
import io.eiren.util.ann.Synchronize;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import io.github.axisangles.ktmath.Quaternion;
import io.github.axisangles.ktmath.Vector3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


public abstract class ProtobufBridge implements Bridge {
	private static final String resetSourceNamePrefix = "ProtobufBridge";

	@VRServerThread
	protected final List<Tracker> sharedTrackers = new FastList<>();
	protected final String bridgeName;
	@ThreadSafe
	private final Queue<ProtobufMessage> inputQueue = new LinkedBlockingQueue<>();
	@ThreadSafe
	private final Queue<ProtobufMessage> outputQueue = new LinkedBlockingQueue<>();
	@Synchronize("self")
	private final Map<String, Tracker> remoteTrackersBySerial = new HashMap<>();
	@Synchronize("self")
	private final Map<Integer, Tracker> remoteTrackersByTrackerId = new HashMap<>();
	private final Tracker hmd;
	private boolean hadNewData = false;
	private Tracker hmdTracker;

	public ProtobufBridge(String bridgeName, Tracker hmd) {
		this.bridgeName = bridgeName;
		this.hmd = hmd;
	}

	/**
	 * Wakes the bridge thread, implementation is platform-specific.
	 */
	@ThreadSafe
	protected abstract void signalSend();

	@BridgeThread
	protected abstract boolean sendMessageReal(ProtobufMessage message);

	@BridgeThread
	protected void messageReceived(ProtobufMessage message) {
		inputQueue.add(message);
	}

	@ThreadSafe
	protected void sendMessage(ProtobufMessage message) {
		outputQueue.add(message);
		signalSend();
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
	protected void trackerOverrideUpdate(Tracker source, Tracker target) {
		target.setPosition(source.getPosition());
		target.setRotation(source.getRotation());
		target.setStatus(source.getStatus());
		target.setBatteryLevel(source.getBatteryLevel());
		target.setBatteryVoltage(source.getBatteryVoltage());
		target.dataTick();
	}

	@VRServerThread
	@Override
	public void dataWrite() {
		if (!hadNewData) // Don't write anything if no message were received, we
							// always process at the
			// speed of the other side
			return;
		for (Tracker tracker : sharedTrackers) {
			writeTrackerUpdate(tracker);
			writeBatteryUpdate(tracker);
		}
	}

	@VRServerThread
	protected void writeTrackerUpdate(Tracker localTracker) {
		Position.Builder builder = Position.newBuilder().setTrackerId(localTracker.getId());
		if (localTracker.getHasPosition()) {
			Vector3 pos = localTracker.getPosition();
			builder.setX(pos.getX());
			builder.setY(pos.getY());
			builder.setZ(pos.getZ());
		}
		if (localTracker.getHasRotation()) {
			Quaternion rot = localTracker.getRotation();
			builder.setQx(rot.getX());
			builder.setQy(rot.getY());
			builder.setQz(rot.getZ());
			builder.setQw(rot.getW());
		}
		sendMessage(ProtobufMessage.newBuilder().setPosition(builder).build());
	}

	// Battery Status
	@VRServerThread
	protected void writeBatteryUpdate(Tracker localTracker) {
		float lowestLevel = 200; // Arbitrarily higher than expected battery
		// percentage
		float trackerLevel = 0; // Tracker battery percentage on a scale from 0
		// to 100. SteamVR expects a value from 0 to 1.
		float trackerVoltage = 0; // Tracker voltage. This is used to determine
		// if the tracker is being charged. owoTrack
		// devices do not have a tracker voltage.
		boolean isCharging = false;

		List<Tracker> allTrackers = Main.getVrServer().getAllTrackers();
		TrackerRole role = localTracker.getTrackerPosition().getTrackerRole();

		Tracker primaryTracker = null;
		Tracker secondaryTracker = null;
		Tracker tertiaryTracker = null;

		// Given what the role is of localTracker, the tracker positions that
		// make up that role are set to primaryTracker, secondaryTracker, and
		// tertiaryTracker respectively.
		primaryTracker = TrackerUtils
			.getNonInternalTrackerForBodyPosition(
				allTrackers,
				TrackerPosition.getByTrackerRole(role)
			);
		switch (role) {
			case WAIST:
				secondaryTracker = TrackerUtils
					.getNonInternalTrackerForBodyPosition(allTrackers, TrackerPosition.WAIST);
				break;
			case LEFT_FOOT:
				secondaryTracker = TrackerUtils
					.getNonInternalTrackerForBodyPosition(
						allTrackers,
						TrackerPosition.LEFT_LOWER_LEG
					);
				break;
			case RIGHT_FOOT:
				secondaryTracker = TrackerUtils
					.getNonInternalTrackerForBodyPosition(
						allTrackers,
						TrackerPosition.RIGHT_LOWER_LEG
					);
				break;
			case LEFT_ELBOW:
				secondaryTracker = TrackerUtils
					.getNonInternalTrackerForBodyPosition(
						allTrackers,
						TrackerPosition.LEFT_LOWER_ARM
					);
				tertiaryTracker = TrackerUtils
					.getNonInternalTrackerForBodyPosition(
						allTrackers,
						TrackerPosition.LEFT_SHOULDER
					);
				break;
			case RIGHT_ELBOW:
				secondaryTracker = TrackerUtils
					.getNonInternalTrackerForBodyPosition(
						allTrackers,
						TrackerPosition.RIGHT_LOWER_ARM
					);
				tertiaryTracker = TrackerUtils
					.getNonInternalTrackerForBodyPosition(
						allTrackers,
						TrackerPosition.RIGHT_SHOULDER
					);
				break;
		}

		// If the battery level of the tracker is lower than lowestLevel, then
		// the battery level of the tracker position becomes lowestLevel.
		// Tracker voltage is set if the tracker position has a battery level
		// lower than lowest level and has a battery voltage (owoTrack devices
		// do not).
		if ((primaryTracker != null) && (primaryTracker.getBatteryLevel() != null)) {
			lowestLevel = primaryTracker.getBatteryLevel();

			if (primaryTracker.getBatteryVoltage() != null) {
				trackerVoltage = primaryTracker.getBatteryVoltage();
			}
		}
		if (
			(secondaryTracker != null)
				&& (secondaryTracker.getBatteryLevel() != null)
				&& (secondaryTracker.getBatteryLevel() < lowestLevel)
		) {
			lowestLevel = secondaryTracker.getBatteryLevel();

			if (secondaryTracker.getBatteryVoltage() != null) {
				trackerVoltage = secondaryTracker.getBatteryVoltage();
			}
		}
		if (
			(tertiaryTracker != null)
				&& (tertiaryTracker.getBatteryLevel() != null)
				&& (tertiaryTracker.getBatteryLevel() < lowestLevel)
		) {
			lowestLevel = tertiaryTracker.getBatteryLevel();

			if (tertiaryTracker.getBatteryVoltage() != null) {
				trackerVoltage = tertiaryTracker.getBatteryVoltage();
			}
		}

		if (lowestLevel == 200) {
			return;
		} else {
			trackerLevel = lowestLevel / 100;
			if (trackerVoltage >= 4.3) {
				// TO DO: Add sending whether the tracker is charging from the
				// tracker itself rather than checking voltage.
				isCharging = true;
			}
		}

		Battery.Builder builder = Battery.newBuilder().setTrackerId(localTracker.getId());

		builder.setBatteryLevel(trackerLevel);
		builder.setIsCharging(isCharging);

		sendMessage(ProtobufMessage.newBuilder().setBattery(builder).build());
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
		} else if (message.hasBattery()) {
			batteryReceived(message.getBattery());
		}
	}

	@VRServerThread
	protected void positionReceived(Position positionMessage) {
		Tracker tracker = getInternalRemoteTrackerById(positionMessage.getTrackerId());
		if (tracker != null) {
			if (positionMessage.hasX())
				tracker
					.setPosition(
						new Vector3(
							positionMessage.getX(),
							positionMessage.getY(),
							positionMessage.getZ()
						)
					);
			tracker
				.setRotation(
					new Quaternion(
						positionMessage.getQw(),
						positionMessage.getQx(),
						positionMessage.getQy(),
						positionMessage.getQz()
					)

				);
			tracker.dataTick();
		}
	}

	@VRServerThread
	protected void batteryReceived(Battery batteryMessage) {
		Tracker tracker = getInternalRemoteTrackerById(batteryMessage.getTrackerId());

		if (tracker != null) {
			tracker.setBatteryLevel(batteryMessage.getBatteryLevel());

			// Purely for cosmetic purposes, SteamVR does not report device
			// voltage.
			if (batteryMessage.getIsCharging()) {
				tracker.setBatteryVoltage(4.3f);
				// TO DO: Add "tracker.setIsCharging"
			} else {
				tracker.setBatteryVoltage(3.7f);
			}
		}
	}

	@VRServerThread
	protected abstract Tracker createNewTracker(TrackerAdded trackerAdded);

	@VRServerThread
	protected void trackerAddedReceived(TrackerAdded trackerAdded) {
		Tracker tracker = getInternalRemoteTrackerById(trackerAdded.getTrackerId());
		if (tracker != null) {
			// TODO reinit?
			return;
		}
		tracker = createNewTracker(trackerAdded);
		synchronized (remoteTrackersBySerial) {
			remoteTrackersBySerial.put(tracker.getName(), tracker);
		}
		synchronized (remoteTrackersByTrackerId) {
			remoteTrackersByTrackerId.put(tracker.getId(), tracker);
		}
		if (trackerAdded.getTrackerRole() == TrackerRole.HMD.getId()) {
			hmdTracker = tracker;
		} else {
			Main.getVrServer().registerTracker(tracker);
		}
	}

	@VRServerThread
	protected void userActionReceived(UserAction userAction) {
		String resetSourceName = "%s: %s".formatted(resetSourceNamePrefix, bridgeName);
		switch (userAction.getName()) {
			case "calibrate":
				LogManager
					.warning("[" + bridgeName + "] Received deprecated user action 'calibrate'!");
			case "reset":
				// TODO : Check pose field
				Main.getVrServer().resetTrackersFull(resetSourceName);
				break;
			case "fast_reset":
				Main.getVrServer().resetTrackersYaw(resetSourceName);
				break;
		}
	}

	@VRServerThread
	protected void trackerStatusReceived(TrackerStatus trackerStatus) {
		Tracker tracker = getInternalRemoteTrackerById(trackerStatus.getTrackerId());
		if (tracker != null) {
			tracker
				.setStatus(
					dev.slimevr.tracking.trackers.TrackerStatus
						.getById(trackerStatus.getStatusValue())
				);
		}
	}

	@ThreadSafe
	protected Tracker getInternalRemoteTrackerById(int trackerId) {
		synchronized (remoteTrackersByTrackerId) {
			return remoteTrackersByTrackerId.get(trackerId);
		}
	}

	@VRServerThread
	protected void reconnected() {
		for (Tracker tracker : sharedTrackers) {
			TrackerAdded.Builder builder = TrackerAdded
				.newBuilder()
				.setTrackerId(tracker.getId())
				.setTrackerName(tracker.getName())
				.setTrackerSerial(tracker.getName())
				.setTrackerRole(tracker.getTrackerPosition().getTrackerRole().getId());
			sendMessage(ProtobufMessage.newBuilder().setTrackerAdded(builder).build());
		}
	}

	@VRServerThread
	protected void disconnected() {
		synchronized (remoteTrackersByTrackerId) {
			for (Entry<Integer, Tracker> integerTEntry : remoteTrackersByTrackerId.entrySet()) {
				integerTEntry
					.getValue()
					.setStatus(dev.slimevr.tracking.trackers.TrackerStatus.DISCONNECTED);
			}
		}
		if (hmdTracker != null) {
			hmd.setStatus(dev.slimevr.tracking.trackers.TrackerStatus.DISCONNECTED);
		}
	}

	@VRServerThread
	@Override
	public void addSharedTracker(Tracker tracker) {
		if (sharedTrackers.contains(tracker))
			return;
		sharedTrackers.add(tracker);
		TrackerAdded.Builder builder = TrackerAdded
			.newBuilder()
			.setTrackerId(tracker.getId())
			.setTrackerName(tracker.getName())
			.setTrackerSerial(tracker.getName())
			.setTrackerRole(tracker.getTrackerPosition().getTrackerRole().getId());
		sendMessage(ProtobufMessage.newBuilder().setTrackerAdded(builder).build());
	}

	@VRServerThread
	@Override
	public void removeSharedTracker(Tracker tracker) {
		// Remove shared tracker
		sharedTrackers.remove(tracker);

		// Set the tracker's status as disconnected
		TrackerStatus.Builder statusBuilder = TrackerStatus
			.newBuilder()
			.setTrackerId(tracker.getId());
		statusBuilder.setStatus(TrackerStatus.Status.DISCONNECTED);
		sendMessage(ProtobufMessage.newBuilder().setTrackerStatus(statusBuilder).build());
	}
}
