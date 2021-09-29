package dev.slimevr.bridge;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import dev.slimevr.bridge.ProtobufMessages.Position;
import dev.slimevr.bridge.ProtobufMessages.ProtobufMessage;
import dev.slimevr.bridge.ProtobufMessages.TrackerAdded;
import dev.slimevr.bridge.ProtobufMessages.TrackerStatus;
import dev.slimevr.bridge.ProtobufMessages.UserAction;
import io.eiren.util.ann.VRServerThread;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.VRTracker;

public abstract class ProtobufBridge<T extends VRTracker> extends AbstractTrackerBrdige<T, VRTracker> {

	private final Vector3f vBuffer = new Vector3f();
	private final Quaternion qBuffer = new Quaternion();
	
	public ProtobufBridge() {
	}

	@BridgeThread
	protected abstract void sendMessage(ProtobufMessage message);

	@BridgeThread
	protected void messageRecieved(ProtobufMessage message) {
		if(message.hasPosition()) {
			positionRecieved(message.getPosition());
		} else if(message.hasUserAction()) {
			userActionRecieved(message.getUserAction());
		} else if(message.hasTrackerStatus()) {
			trackerStatusRecieved(message.getTrackerStatus());
		} else if(message.hasTrackerAdded()) {
			trackerAddedRecieved(message.getTrackerAdded());
		}
	}

	@BridgeThread
	protected void positionRecieved(Position positionMessage) {
		T tracker = getRemoteTracker(positionMessage.getTrackerId());
		if(tracker != null) {
			tracker.position.set(positionMessage.getX(), positionMessage.getY(), positionMessage.getZ());
			tracker.rotation.set(positionMessage.getQx(), positionMessage.getQy(), positionMessage.getQz(), positionMessage.getQw());
			tracker.dataTick();
		}
	}

	@BridgeThread
	protected void trackerAddedRecieved(TrackerAdded trackerAdded) {
	}

	@BridgeThread
	protected void userActionRecieved(UserAction userAction) {
	}

	@BridgeThread
	protected void trackerStatusRecieved(TrackerStatus trackerStatus) {
		T tracker = getRemoteTracker(trackerStatus.getTrackerId());
		if(tracker != null) {
			tracker.setStatus(io.eiren.vr.trackers.TrackerStatus.getById(trackerStatus.getStatusValue()));
		}
	}

	@Override
	@VRServerThread
	protected void transferToInternalTracker(Tracker source, VRTracker target) {
		if(source.getPosition(vBuffer))
			target.position.set(vBuffer);
		if(source.getRotation(qBuffer))
			target.rotation.set(qBuffer);
		target.setStatus(source.getStatus());
		target.dataTick();
	}

	@Override
	@VRServerThread
	protected void transferFromInternalTracker(T source, T target) {
		if(source.getPosition(vBuffer))
			target.position.set(vBuffer);
		if(source.getRotation(qBuffer))
			target.rotation.set(qBuffer);
		target.setStatus(source.getStatus());
		target.dataTick();
	}
    
	protected Tracker getLocalTracker(int trackerId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected T getRemoteTracker(int trackerId) {
		// TODO Auto-generated method stub
		return null;
	}
}
