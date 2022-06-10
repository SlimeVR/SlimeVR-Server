package dev.slimevr.vr.trackers;

import com.jme3.math.Quaternion;

public abstract class AdjustedTracker implements Tracker {

	public final Quaternion yawFix = new Quaternion();
	public final Quaternion gyroFix = new Quaternion();
	public final Quaternion attachmentFix = new Quaternion();

	/**
	 * Reset the tracker so that it's current rotation is counted as (0, <HMD
	 * Yaw>, 0). This allows tracker to be strapped to body at any pitch and
	 * roll.
	 * <p>
	 * Performs {@link #resetYaw(Quaternion)} for yaw drift correction.
	 */
	@Override
	public void resetFull(Quaternion reference) {
		fixGyroscope();

		Quaternion sensorRotation = new Quaternion();
		getRotation(sensorRotation);
		gyroFix.mult(sensorRotation, sensorRotation);
		attachmentFix.set(sensorRotation).inverseLocal();

		fixYaw(reference);
	}

	/**
	 * Reset the tracker so that it's current yaw rotation is counted as <HMD
	 * Yaw>. This allows the tracker to have yaw independent of the HMD. Tracker
	 * should still report yaw as if it was mounted facing HMD, mounting
	 * position should be corrected in the source.
	 */
	@Override
	public void resetYaw(Quaternion reference) {
		fixYaw(reference);
	}

	private void fixYaw(Quaternion reference) {
		// Use only yaw HMD rotation
		Quaternion targetTrackerRotation = new Quaternion(reference);
		float[] angles = new float[3];
		targetTrackerRotation.toAngles(angles);
		targetTrackerRotation.fromAngles(0, angles[1], 0);

		Quaternion sensorRotation = new Quaternion();
		getRotation(sensorRotation);
		gyroFix.mult(sensorRotation, sensorRotation);
		sensorRotation.multLocal(attachmentFix);

		sensorRotation.toAngles(angles);
		sensorRotation.fromAngles(0, angles[1], 0);

		yawFix.set(sensorRotation).inverseLocal().multLocal(targetTrackerRotation);
	}

	private void fixGyroscope() {
		float[] angles = new float[3];

		Quaternion sensorRotation = new Quaternion();
		getRotation(sensorRotation);

		sensorRotation.toAngles(angles);
		sensorRotation.fromAngles(0, angles[1], 0);

		gyroFix.set(sensorRotation).inverseLocal();
	}

	protected void adjustInternal(Quaternion store) {
		gyroFix.mult(store, store);
		store.multLocal(attachmentFix);
		yawFix.mult(store, store);
	}
}
