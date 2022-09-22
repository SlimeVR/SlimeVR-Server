package dev.slimevr.vr.trackers;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.config.TrackerConfig;
import dev.slimevr.vr.Device;


public class ReferenceAdjustedTracker<E extends Tracker> implements Tracker {

	public final E tracker;

	// Two vectors for mounting rotation correction
	private static final Vector3f upVector = new Vector3f(0f, 1f, 0f);
	public final Vector3f rotVector = new Vector3f(upVector);

	public final Quaternion gyroFix = new Quaternion();
	public final Quaternion attachmentFix = new Quaternion();
	public final Quaternion mountRotFix = new Quaternion();
	public final Quaternion yawFix = new Quaternion();

	protected float confidenceMultiplier = 1.0f;

	public ReferenceAdjustedTracker(E tracker) {
		this.tracker = tracker;
	}

	@Override
	public boolean userEditable() {
		return this.tracker.userEditable();
	}

	@Override
	public void readConfig(TrackerConfig config) {
		this.tracker.readConfig(config);
	}

	@Override
	public void writeConfig(TrackerConfig config) {
		this.tracker.writeConfig(config);
	}

	/**
	 * Reset the tracker so that its current rotation is counted as (0, <HMD
	 * Yaw>, 0). This allows the tracker to be strapped to body at any pitch and
	 * roll.
	 * <p>
	 * Performs {@link #resetYaw(Quaternion)} for yaw drift correction.
	 */
	@Override
	public void resetFull(Quaternion reference) {
		tracker.resetFull(reference);

		Quaternion sensorRotation = new Quaternion();
		tracker.getRotation(sensorRotation);

		fixGyroscope(sensorRotation.clone());
		fixAttachment(sensorRotation.clone());

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

	private void fixGyroscope(Quaternion sensorRotation) {
		sensorRotation.fromAngles(0, sensorRotation.getYaw(), 0);
		gyroFix.set(sensorRotation).inverseLocal();
	}

	private void fixAttachment(Quaternion sensorRotation) {
		gyroFix.mult(sensorRotation, sensorRotation);
		attachmentFix.set(sensorRotation).inverseLocal();
	}

	@Override
	public void resetMountingRotation(boolean reverseYaw) {
		tracker.resetMountingRotation(reverseYaw);

		// Get the current calibrated rotation
		Quaternion buffer = new Quaternion();
		tracker.getRotation(buffer);
		gyroFix.mult(buffer, buffer);
		buffer.multLocal(attachmentFix);

		// Reset the vector for the rotation
		rotVector.set(upVector);
		// Rotate the vector by the quat, then flatten and normalize the vector
		buffer.multLocal(rotVector).setY(0f).normalizeLocal();

		// Calculate the yaw angle using tan
		// Just use an angle offset of zero for unsolvable circumstances
		float yawAngle = FastMath.isApproxZero(rotVector.x) && FastMath.isApproxZero(rotVector.z)
			? 0f
			: FastMath.atan2(rotVector.x, rotVector.z);

		// Make an adjustment quaternion from the angle
		buffer.fromAngles(0f, reverseYaw ? yawAngle : yawAngle - FastMath.PI, 0f);

		Quaternion lastRotAdjust = new Quaternion(mountRotFix);
		mountRotFix.set(buffer);

		// Get the difference from the last adjustment
		buffer.multLocal(lastRotAdjust.inverseLocal());
		// Apply the yaw rotation difference to the yaw fix quaternion
		yawFix.multLocal(buffer.inverseLocal());
	}

	private void fixYaw(Quaternion reference) {
		// Use only yaw HMD rotation
		Quaternion targetRotation = new Quaternion(reference);
		targetRotation.fromAngles(0, targetRotation.getYaw(), 0);

		Quaternion sensorRotation = new Quaternion();
		tracker.getRotation(sensorRotation);
		gyroFix.mult(sensorRotation, sensorRotation);
		sensorRotation.multLocal(attachmentFix);
		sensorRotation.multLocal(mountRotFix);

		sensorRotation.fromAngles(0, sensorRotation.getYaw(), 0);

		yawFix.set(sensorRotation).inverseLocal().multLocal(targetRotation);
	}

	protected void adjustInternal(Quaternion store) {
		gyroFix.mult(store, store);
		store.multLocal(attachmentFix);
		store.multLocal(mountRotFix);
		yawFix.mult(store, store);
	}

	@Override
	public boolean getRotation(Quaternion store) {
		tracker.getRotation(store);
		adjustInternal(store);
		return true;
	}

	@Override
	public boolean getAcceleration(Vector3f store) {
		return tracker.getAcceleration(store);
	}

	@Override
	public boolean getPosition(Vector3f store) {
		return tracker.getPosition(store);
	}

	@Override
	public String getName() {
		return tracker.getName();
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
	public Device getDevice() {
		return tracker.getDevice();
	}

	@Override
	public Tracker get() {
		return this.tracker;
	}

	@Override
	public String getDisplayName() {
		return this.tracker.getDisplayName();
	}

	@Override
	public String getCustomName() {
		return this.tracker.getCustomName();
	}
}
