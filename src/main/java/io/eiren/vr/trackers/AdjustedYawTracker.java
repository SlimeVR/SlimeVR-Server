package io.eiren.vr.trackers;

import com.jme3.math.Quaternion;

public class AdjustedYawTracker extends AdjustedTracker {
	
	public final Quaternion adjustment = new Quaternion();

	public AdjustedYawTracker(Tracker tracker) {
		super(tracker);
	}
	
	@Override
	public void adjust(Quaternion reference) {
		Quaternion targetTrackerRotation = new Quaternion(reference);

		// Use only yaw HMD rotation
		float[] angles = new float[3];
		targetTrackerRotation.toAngles(angles);
		targetTrackerRotation.fromAngles(0, angles[1], 0);
		
		Quaternion sensorRotation = new Quaternion();
		tracker.getRotation(sensorRotation);
		
		sensorRotation.toAngles(angles);
		sensorRotation.fromAngles(0, angles[1], 0);
		
		adjustment.set(sensorRotation).inverseLocal().multLocal(targetTrackerRotation);
		
		confidenceMultiplier = 1.0f / tracker.getConfidenceLevel();
		lastAngles[0] = 1000;
	}
	
	@Override
	protected void adjustInternal(Quaternion store) {
		adjustment.mult(store, store);
	}
}
