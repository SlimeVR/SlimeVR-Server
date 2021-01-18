package io.eiren.vr.trackers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class AdjustedYawTracker extends AdjustedTracker {

	public AdjustedYawTracker(Tracker tracker) {
		super(tracker);
	}
	
	@Override
	public void adjust(Quaternion reference) {
		Quaternion targetTrackerRotation = new Quaternion(reference);
		
		// Use only yaw rotation
		Vector3f hmdFront = new Vector3f(0, 0, 1);
		targetTrackerRotation.multLocal(hmdFront);
		hmdFront.multLocal(1, 0, 1).normalizeLocal();
		targetTrackerRotation.lookAt(hmdFront, Vector3f.UNIT_Y);
		
		Quaternion sensorRotation = new Quaternion();
		tracker.getRotation(sensorRotation);
		
		// Adjust only yaw rotation
		Vector3f sensorFront = new Vector3f(0, 0, 1);
		sensorRotation.multLocal(sensorFront);
		sensorFront.multLocal(1, 0, 1).normalizeLocal();
		sensorRotation.lookAt(sensorFront, Vector3f.UNIT_Y);
		
		adjustment.set(sensorRotation).inverseLocal().multLocal(targetTrackerRotation);
	}
}
