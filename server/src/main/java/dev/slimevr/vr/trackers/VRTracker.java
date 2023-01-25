package dev.slimevr.vr.trackers;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import dev.slimevr.vr.Device;
import io.eiren.util.BufferedTimer;


public class VRTracker extends ComputedTracker {

	private static final Quaternion LEFT_TPOSE_OFFSET = new Quaternion()
		.fromAngles(0, 0, FastMath.HALF_PI);
	private static final Quaternion RIGHT_TPOSE_OFFSET = new Quaternion()
		.fromAngles(0, 0, -FastMath.HALF_PI);
	protected BufferedTimer timer = new BufferedTimer(1f);
	public final Quaternion mountFix = new Quaternion();
	public final Quaternion attachmentFix = new Quaternion();
	public final Quaternion yawFix = new Quaternion();

	public VRTracker(
		int id,
		String serial,
		String name,
		boolean hasRotation,
		boolean hasPosition,
		Device device
	) {
		super(id, serial, name, hasRotation, hasPosition, device);
	}

	public VRTracker(int id, String name, boolean hasRotation, boolean hasPosition) {
		super(id, name, name, hasRotation, hasPosition, null);
	}

	@Override
	public boolean getRotation(Quaternion store) {
		store.set(rotation);
		// Don't adjust if tracker is HMD/Head (use raw rotation)
		if (super.getBodyPosition() != TrackerPosition.HMD)
			adjustInternal(store);
		return true;
	}

	// TODO Reduce code duplication from IMUTracker.java
	// Refactor all tracker classes into one?
	protected void adjustInternal(Quaternion store) {
		store.multLocal(attachmentFix);
		store.multLocal(mountFix);
		yawFix.mult(store, store);
	}

	@Override
	public void resetFull(Quaternion reference, boolean tPose) {
		fixAttachment(rotation, reference, tPose);
		fixYaw(rotation, reference);
	}

	@Override
	public void resetYaw(Quaternion reference) {
		fixYaw(rotation, reference);
	}

	private void fixAttachment(Quaternion sensorRotation, Quaternion reference, boolean tPose) {
		mountFix.fromAngles(0, reference.getYaw(), 0);
		if (tPose)
			fixForTPose(sensorRotation);
		attachmentFix.set(sensorRotation.inverse());
	}

	private void fixYaw(Quaternion sensorRotation, Quaternion reference) {
		// Use only yaw HMD rotation
		reference = reference.clone();
		reference.fromAngles(0, reference.getYaw(), 0);

		sensorRotation = sensorRotation.clone();
		sensorRotation.multLocal(attachmentFix);
		sensorRotation.multLocal(mountFix);

		sensorRotation.fromAngles(0, sensorRotation.getYaw(), 0);

		yawFix.set(sensorRotation).inverseLocal().multLocal(reference);
	}

	private void fixForTPose(Quaternion store) {
		if (isOnLeftArm()) {
			store.set(LEFT_TPOSE_OFFSET.mult(store));
		} else if (isOnRightArm()) {
			store.set(RIGHT_TPOSE_OFFSET.mult(store));
		}
	}

	private boolean isOnLeftArm() {
		return bodyPosition == TrackerPosition.LEFT_UPPER_ARM
			|| bodyPosition == TrackerPosition.LEFT_LOWER_ARM
			|| bodyPosition == TrackerPosition.LEFT_HAND;
	}

	private boolean isOnRightArm() {
		return bodyPosition == TrackerPosition.RIGHT_UPPER_ARM
			|| bodyPosition == TrackerPosition.RIGHT_LOWER_ARM
			|| bodyPosition == TrackerPosition.RIGHT_HAND;
	}

	@Override
	public float getTPS() {
		return timer.getAverageFPS();
	}

	@Override
	public void dataTick() {
		timer.update();
	}

	@Override
	public boolean userEditable() {
		return true;
	}

	@Override
	public boolean isComputed() {
		return false;
	}

	@Override
	public String getDisplayName() {
		return getName();
	}
}
