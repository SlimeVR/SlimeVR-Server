package io.eiren.vr.bridge;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import dev.slimevr.bridge.ProtobufMessages.Position;
import dev.slimevr.bridge.ProtobufMessages.ProtobufMessage;
import dev.slimevr.bridge.ProtobufMessages.TrackerAdded;
import dev.slimevr.bridge.ProtobufMessages.TrackerStatus;
import dev.slimevr.bridge.ProtobufMessages.UserAction;
import io.eiren.vr.trackers.ComputedTracker;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.VRTracker;

public abstract class ProtobufVRBridge<T extends VRTracker> extends AbstractTrackerBrdige<T, ComputedTracker> {

	private final Vector3f vBuffer = new Vector3f();
	private final Quaternion qBuffer = new Quaternion();
	
	public ProtobufVRBridge() {
	}
	
	protected abstract T newTracker(TrackerAdded trackerAddedMessage);
	
	protected abstract void sendMessage(ProtobufMessage message);
	
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
	
	protected void positionRecieved(Position positionMessage) {
		
	}
	
	protected void trackerAddedRecieved(TrackerAdded trackerAdded) {
		
	}
	
	protected void userActionRecieved(UserAction userAction) {
	}
	
	protected void trackerStatusRecieved(TrackerStatus trackerStatus) {
		
	}

	@Override
	protected void transferToInternalTracker(Tracker source, ComputedTracker target) {
		if(source.getPosition(vBuffer))
			target.position.set(vBuffer);
		if(source.getRotation(qBuffer))
			target.rotation.set(qBuffer);
		target.setStatus(source.getStatus());
		target.dataTick();
	}

	@Override
	protected void transferFromInternalTracker(T source, T target) {
		if(source.getPosition(vBuffer))
			target.position.set(vBuffer);
		if(source.getRotation(qBuffer))
			target.rotation.set(qBuffer);
		target.setStatus(source.getStatus());
		target.dataTick();
	}

	@Override
	protected ComputedTracker createInternalSharedTracker(Tracker source) {
		return new ComputedTracker("internal://" + source.getName(), source.hasRotation(), source.hasPosition());
	}
}
