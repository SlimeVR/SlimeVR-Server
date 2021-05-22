package io.eiren.vr.trackers;

import com.jme3.math.Quaternion;

public class AdjustedFullTracker extends AdjustedYawTracker {

	private final float[] angles = new float[3];
	private float yawCorrection = 0;
	private float pitchCorrection = 0;
	private float rollCorrection = 0;

	public AdjustedFullTracker(Tracker tracker) {
		super(tracker);
	}

	@Override
	public void adjust(Quaternion reference) {
		float[] angles = this.angles;
		reference.toAngles(angles);
		// Use only yaw HMD rotation
		angles[0] = 0;
		angles[2] = 0;
		
		Quaternion sensorRotation = new Quaternion();
		tracker.getRotation(sensorRotation);
		float[] angles2 = new float[3];
		sensorRotation.toAngles(angles2);
		yawCorrection = angles[1] - angles2[1];
		pitchCorrection = angles[0] - angles2[0];
		rollCorrection = angles[2] - angles2[2];
	}
	
	@Override
	protected void adjustInternal(Quaternion store) {
		float[] angles = this.angles;
		store.toAngles(angles);
		angles[0] += pitchCorrection;
		angles[1] += yawCorrection;
		angles[2] += rollCorrection;
		store.fromAngles(angles);
	}
}
