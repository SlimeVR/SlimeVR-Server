package dev.slimevr.vr.trackers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


public class ReferenceAdjustedTracker<E extends Tracker> implements Tracker {

	public final E tracker;
	public final Quaternion yawFix = new Quaternion();
	public final Quaternion gyroFix = new Quaternion();
	public final Quaternion attachmentFix = new Quaternion();
	protected float confidenceMultiplier = 1.0f;

	public ReferenceAdjustedTracker(E tracker) {
		this.tracker = tracker;
	}

	public E getTracker() {
		return this.tracker;
	}

	@Override
	public boolean userEditable() {
		return this.tracker.userEditable();
	}

	@Override
	public void loadConfig(TrackerConfig config) {
		this.tracker.loadConfig(config);
	}

	@Override
	public void saveConfig(TrackerConfig config) {
		this.tracker.saveConfig(config);
	}

	/**
	 * Reset the tracker so that it's current rotation is counted as (0, <HMD
	 * Yaw>, 0). This allows tracker to be strapped to body at any pitch and
	 * roll.
	 * <p>
	 * Performs {@link #resetYaw(Quaternion)} for yaw drift correction.
	 */
	@Override
	public void resetFull(Quaternion reference) {
		tracker.resetFull(reference);
		fixGyroscope();

		Quaternion sensorRotation = new Quaternion();
		tracker.getRotation(sensorRotation);
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
		tracker.resetYaw(reference);
		fixYaw(reference);
	}

	private void fixYaw(Quaternion reference) {
		// Use only yaw HMD rotation
		Quaternion targetTrackerRotation = new Quaternion(reference);
		float[] angles = new float[3];
		targetTrackerRotation.toAngles(angles);
		targetTrackerRotation.fromAngles(0, angles[1], 0);

		Quaternion sensorRotation = new Quaternion();
		tracker.getRotation(sensorRotation);
		gyroFix.mult(sensorRotation, sensorRotation);
		sensorRotation.multLocal(attachmentFix);

		sensorRotation.toAngles(angles);
		sensorRotation.fromAngles(0, angles[1], 0);

		yawFix.set(sensorRotation).inverseLocal().multLocal(targetTrackerRotation);
	}

	private void fixGyroscope() {
		float[] angles = new float[3];

		Quaternion sensorRotation = new Quaternion();
		tracker.getRotation(sensorRotation);

		sensorRotation.toAngles(angles);
		sensorRotation.fromAngles(0, angles[1], 0);

		gyroFix.set(sensorRotation).inverseLocal();
	}

	protected void adjustInternal(Quaternion store) {
		gyroFix.mult(store, store);
		store.multLocal(attachmentFix);
		yawFix.mult(store, store);
	}

	@Override
	public boolean getRotation(Quaternion store) {
		tracker.getRotation(store);
		adjustInternal(store);
		return true;
	}

	@Override
	public boolean getPosition(Vector3f store) {
		return tracker.getPosition(store);
	}

	@Override
	public String getName() {
		return tracker.getName() + "/adj";
	}

	@Override
	public TrackerStatus getStatus() {
		return tracker.getStatus();
	}

	@Override
	public float getConfidenceLevel() {
		return tracker.getConfidenceLevel() * confidenceMultiplier;
	}

	@Override
	public TrackerPosition getBodyPosition() {
		return tracker.getBodyPosition();
	}

	@Override
	public void setBodyPosition(TrackerPosition position) {
		tracker.setBodyPosition(position);
	}

	@Override
	public void tick() {
		tracker.tick();
	}

	@Override
	public boolean hasRotation() {
		return tracker.hasRotation();
	}

	@Override
	public boolean hasPosition() {
		return tracker.hasPosition();
	}

	@Override
	public boolean isComputed() {
		return tracker.isComputed();
	}

	@Override
	public int getTrackerId() {
		return tracker.getTrackerId();
	}

	@Override
	public int getTrackerNum() {
		return tracker.getTrackerNum();
	}

	@Override
	public String getDescriptiveName() {
		return tracker.getDescriptiveName();
	}

	@Override
	public Device getDevice() {
		return tracker.getDevice();
	}
}
