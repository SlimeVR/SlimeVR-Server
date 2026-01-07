package dev.slimevr.tracking.trackers

import com.jme3.math.FastMath
import dev.slimevr.config.ArmsResetModes
import dev.slimevr.config.ResetsConfig
import dev.slimevr.tracking.trackers.udp.TrackerDataType
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.*

/** Class taking care of full reset, yaw reset, and mounting reset logic. */
class TrackerResetsHandler(val tracker: Tracker) {

	private val HalfHorizontal = EulerAngles(
		EulerOrder.YZX,
		0f,
		Math.PI.toFloat(),
		0f,
	).toQuaternion()
	private val QuarterPitch = Quaternion.rotationAroundXAxis(FastMath.HALF_PI)

	private var armsResetMode = ArmsResetModes.BACK
	private var yawResetSmoothTime = 0.0f
	var saveMountingReset = false
	var resetHmdPitch = false
	var lastResetQuaternion: Quaternion? = null

	// Manual mounting orientation
	var mountingOrientation = HalfHorizontal
		set(value) {
			field = value
			// Clear the mounting reset now that it's been set manually
			clearMounting()
		}

	// Reference adjustment quats

	/**
	 * Attachment fix is set by full reset. This sets the current x and z rotations to
	 * 0, correcting for initial pitch and roll rotation. This is a global offset in
	 * rotation and affects the axes of rotation.
	 *
	 * This effectively sets the rotation at the moment of a full reset to be
	 * zero-reference in the x and z axes.
	 */
	private var attachmentFix = Quaternion.IDENTITY

	/**
	 * Mounting rotation fix is set by mounting reset. This corrects for the mounting
	 * orientation, then the inverse is used to correct for the rotation incurred. This
	 * value is computed after [yawFix], but takes effect before [yawFix]. This affects
	 * the axes of rotation, but does not incur an offset in rotation.
	 *
	 * This rotation is done in addition to [mountingOrientation] as to not interfere
	 * with the functionality of manual mounting orientation. This effectively sets the
	 * rotation at the moment of a mounting reset to be zero-reference in the y-axis. If
	 * no mounting reset is done, then this rotation will not be used and only
	 * [mountingOrientation] will apply.
	 */
	var mountRotFix = Quaternion.IDENTITY
		private set

	/**
	 * Yaw fix is set by yaw reset. This sets the current y rotation to match the
	 * provided reference, correlating the tracker to the provided frame of reference.
	 * This is a local offset in rotation and does not affect the axes of rotation.
	 *
	 * This effectively aligns the current yaw rotation to the head tracker's yaw
	 * rotation.
	 */
	private var yawFix = Quaternion.IDENTITY

	/**
	 * Constraint fix is set by skeleton constraints. This corrects for any yaw rotation
	 * that violates the skeleton constraints. This is a local offset in rotation and
	 * does not affect the axes of rotation.
	 */
	private var constraintFix = Quaternion.IDENTITY

	// Zero-reference/identity adjustment quats for IMU debugging
	private var gyroFixNoMounting = Quaternion.IDENTITY
	private var attachmentFixNoMounting = Quaternion.IDENTITY
	private var yawFixZeroReference = Quaternion.IDENTITY

	/**
	 * T-Pose down fix is set by full reset. This corrects for the pitch of the rotation
	 * assuming a t-pose reference, adjusting to match our expected i-pose reference.
	 * This is a global offset in rotation and affects the axes of rotation.
	 */
	private var tposeDownFix = Quaternion.IDENTITY

	/**
	 * Reads/loads reset settings from the given config
	 */
	fun readResetConfig(config: ResetsConfig) {
		armsResetMode = config.mode
		yawResetSmoothTime = config.yawResetSmoothTime
		saveMountingReset = config.saveMountingReset
		resetHmdPitch = config.resetHmdPitch
	}

	fun trySetMountingReset(quat: Quaternion) {
		if (saveMountingReset) {
			mountRotFix = quat
		}
	}

	/**
	 * Takes a rotation and adjusts it to resets and mounting with the HMD as the
	 * reference.
	 */
	fun getReferenceAdjustedRotationFrom(rotation: Quaternion): Quaternion = adjustToReference(rotation)

	/**
	 * Takes a rotation and adjusts it to resets and mounting,
	 * with the identity Quaternion as the reference.
	 */
	fun getIdentityAdjustedRotationFrom(rotation: Quaternion): Quaternion = adjustToIdentity(rotation)

	/**
	 * Get the reference adjusted accel.
	 */
	// All IMU axis corrections are inverse to undo `adjustToReference` after local yaw offsets are added
	// Order is VERY important here! Please be extremely careful! >~>
	fun getReferenceAdjustedAccel(rawRot: Quaternion, accel: Vector3): Vector3 = (adjustToReference(rawRot) * (attachmentFix * mountingOrientation * mountRotFix * tposeDownFix).inv()).sandwich(accel)

	/**
	 * Converts raw or filtered rotation into reference- and
	 * mounting-reset-adjusted by applying quaternions produced after
	 * full reset, yaw rest and mounting reset
	 */
	private fun adjustToReference(rotation: Quaternion): Quaternion {
		var rot = rotation
		// Correct for global pitch/roll offset
		rot *= attachmentFix
		// Correct for global yaw offset without affecting local yaw so we can change this
		// later without invalidating local yaw offset corrections
		if (!tracker.isHmd || tracker.trackerPosition != TrackerPosition.HEAD) {
			rot = mountingOrientation.inv() * rot * mountingOrientation
		}
		rot = mountRotFix.inv() * rot * mountRotFix
		// T-pose global correction
		rot *= tposeDownFix
		// Align local yaw with reference
		rot = yawFix * rot
		rot = constraintFix * rot
		return rot
	}

	/**
	 * Converts raw or filtered rotation into zero-reference-adjusted by
	 * applying quaternions produced after full reset and yaw reset only
	 */
	private fun adjustToIdentity(rotation: Quaternion): Quaternion {
		var rot = rotation
		rot = gyroFixNoMounting * rot
		rot *= attachmentFixNoMounting
		rot = yawFixZeroReference * rot
		rot = constraintFix * rot
		return rot
	}

	/**
	 * Reset the tracker so that its current rotation is counted as (0, HMD Yaw,
	 * 0). This allows the tracker to be strapped to body at any pitch and roll.
	 */
	fun resetFull(reference: Quaternion) {
		constraintFix = Quaternion.IDENTITY

		if (tracker.trackerDataType == TrackerDataType.FLEX_RESISTANCE) {
			tracker.trackerFlexHandler.resetMin()
			postProcessResetFull(reference)
			return
		} else if (tracker.trackerDataType == TrackerDataType.FLEX_ANGLE) {
			postProcessResetFull(reference)
			return
		}

		// Adjust for T-Pose (down)
		tposeDownFix = if (((tracker.trackerPosition.isLeftArm() || tracker.trackerPosition.isLeftFinger()) && armsResetMode == ArmsResetModes.TPOSE_DOWN)) {
			EulerAngles(EulerOrder.YZX, 0f, 0f, -FastMath.HALF_PI).toQuaternion()
		} else if (((tracker.trackerPosition.isRightArm() || tracker.trackerPosition.isRightFinger()) && armsResetMode == ArmsResetModes.TPOSE_DOWN)) {
			EulerAngles(EulerOrder.YZX, 0f, 0f, FastMath.HALF_PI).toQuaternion()
		} else {
			Quaternion.IDENTITY
		}

		// Old rot for drift logging
		lastResetQuaternion = adjustToReference(tracker.getRawRotation())

		// Adjust raw rotation to mountingOrientation
		val rotation = tracker.getRawRotation()

		// Gyrofix
		val gyroFix = if (tracker.allowMounting || (tracker.trackerPosition == TrackerPosition.HEAD && !tracker.isHmd)) {
			if (tracker.isComputed) {
				fixGyroscope(rotation)
			} else {
				if (tracker.trackerPosition.isFoot()) {
					// Feet are rotated by 90 deg pitch, this means we're relying on IMU rotation
					//  to be set correctly here.
					fixGyroscope(rotation * tposeDownFix * QuarterPitch)
				} else {
					fixGyroscope(rotation * tposeDownFix)
				}
			}
		} else {
			Quaternion.IDENTITY
		}

		// Mounting for computed trackers
		if (tracker.isComputed && tracker.trackerPosition != TrackerPosition.HEAD) {
			// Set mounting to the reference's yaw so that a computed
			// tracker goes forward according to the head tracker.
			mountRotFix = getYawQuaternion(reference)
		}

		// Attachment fix
		attachmentFix = if (tracker.trackerPosition == TrackerPosition.HEAD && tracker.isHmd) {
			if (resetHmdPitch) {
				// Reset the HMD's pitch if it's assigned to head and resetHmdPitch is true
				// Get rotation without yaw (make sure to use the raw rotation directly!)
				val rotBuf = getYawQuaternion(rotation).inv() * rotation
				// Isolate pitch
				Quaternion(rotBuf.w, -rotBuf.x, 0f, 0f).unit()
			} else {
				// Don't reset the HMD at all
				Quaternion.IDENTITY
			}
		} else {
			(gyroFix * rotation).inv()
		}

		// Rotate attachmentFix by 180 degrees as a workaround for t-pose (down)
		if (tposeDownFix != Quaternion.IDENTITY && tracker.allowMounting) {
			attachmentFix *= HalfHorizontal
		}

		// Don't adjust yaw if head and computed
		if (tracker.trackerPosition != TrackerPosition.HEAD || !tracker.isComputed) {
			yawFix = gyroFix * reference.project(Vector3.POS_Y).unit()
			tracker.yawResetSmoothing.reset()
		}

		makeIdentityAdjustmentQuatsFull()

		// Reset Stay Aligned (before resetting filtering, which depends on the
		// tracker's rotation)
		tracker.stayAligned.reset()

		postProcessResetFull(reference)
	}

	private fun postProcessResetFull(reference: Quaternion) {
		if (this.tracker.needReset) {
			this.tracker.needReset = false
		}

		tracker.resetFilteringQuats(reference)
	}

	/**
	 * Reset the tracker so that its current yaw rotation is aligned with the HMD's
	 * Yaw. This allows the tracker to have yaw independent of the HMD. Tracker
	 * should still report yaw as if it was mounted facing HMD, mounting
	 * position should be corrected in the source.
	 */
	fun resetYaw(reference: Quaternion) {
		// TODO HMD doesn't get yaw reset, which makes it so tracker.resetFilteringQuats() doesn't get called

		constraintFix = Quaternion.IDENTITY

		if (tracker.trackerDataType == TrackerDataType.FLEX_RESISTANCE ||
			tracker.trackerDataType == TrackerDataType.FLEX_ANGLE
		) {
			// Don't do anything as these don't have yaw anyways
			return
		}

		// Old rot for drift logging
		lastResetQuaternion = adjustToReference(tracker.getRawRotation())

		val yawFixOld = yawFix
		yawFix = fixYaw(tracker.getRawRotation(), reference)
		tracker.yawResetSmoothing.reset()

		makeIdentityAdjustmentQuatsYaw()

		// Start at yaw before reset if smoothing enabled
		if (yawResetSmoothTime > 0.0f) {
			tracker.yawResetSmoothing.interpolate(
				yawFixOld / yawFix,
				Quaternion.IDENTITY,
				yawResetSmoothTime,
			)
		}

		// Reset Stay Aligned (before resetting filtering, which depends on the
		// tracker's rotation)
		tracker.stayAligned.reset()

		tracker.resetFilteringQuats(reference)
	}

	/**
	 * Perform the math to align the tracker to go forward
	 * and stores it in mountRotFix, and adjusts yawFix
	 */
	fun resetMounting(reference: Quaternion) {
		if (tracker.trackerDataType == TrackerDataType.FLEX_RESISTANCE) {
			tracker.trackerFlexHandler.resetMax()
			tracker.resetFilteringQuats(reference)
			return
		} else if (tracker.trackerDataType == TrackerDataType.FLEX_ANGLE) {
			return
		}

		constraintFix = Quaternion.IDENTITY

		// Get the current calibrated rotation
		var rotBuf = tracker.getRawRotation()
		rotBuf *= attachmentFix
		rotBuf = mountingOrientation.inv() * rotBuf * mountingOrientation
		rotBuf = yawFix * rotBuf

		// Adjust buffer to reference
		rotBuf = reference.project(Vector3.POS_Y).inv().unit() * rotBuf

		// Rotate a vector pointing up by the quat
		val rotVector = rotBuf.sandwich(Vector3.POS_Y)

		// Calculate the yaw angle using tan
		var yawAngle = atan2(rotVector.x, rotVector.z)

		// Adjust for T-Pose and fingers
		if ((tracker.trackerPosition.isLeftArm() && armsResetMode == ArmsResetModes.TPOSE_DOWN) ||
			(tracker.trackerPosition.isRightArm() && armsResetMode == ArmsResetModes.TPOSE_UP) ||
			tracker.trackerPosition.isLeftFinger()
		) {
			// Tracker goes right
			yawAngle -= FastMath.HALF_PI
		}
		if ((tracker.trackerPosition.isLeftArm() && armsResetMode == ArmsResetModes.TPOSE_UP) ||
			(tracker.trackerPosition.isRightArm() && armsResetMode == ArmsResetModes.TPOSE_DOWN) ||
			tracker.trackerPosition.isRightFinger()
		) {
			// Tracker goes left
			yawAngle += FastMath.HALF_PI
		}

		// Adjust for forward/back arms and thighs
		val isLowerArmBack = armsResetMode == ArmsResetModes.BACK && (tracker.trackerPosition.isLeftLowerArm() || tracker.trackerPosition.isRightLowerArm())
		val isArmForward = armsResetMode == ArmsResetModes.FORWARD && (tracker.trackerPosition.isLeftArm() || tracker.trackerPosition.isRightArm())
		if (!tracker.trackerPosition.isThigh() && !isArmForward && !isLowerArmBack) {
			// Tracker goes back
			yawAngle -= FastMath.PI
		}

		// Make an adjustment quaternion from the angle
		mountRotFix = EulerAngles(EulerOrder.YZX, 0f, yawAngle, 0f).toQuaternion()

		// save mounting reset
		if (saveMountingReset) tracker.saveMountingResetOrientation(mountRotFix)

		tracker.resetFilteringQuats(reference)
	}

	/**
	 * Apply a corrective rotation to the gyroFix
	 */
	fun updateConstraintFix(correctedRotation: Quaternion) {
		constraintFix *= correctedRotation
	}

	fun clearMounting() {
		mountRotFix = Quaternion.IDENTITY
	}

	// EulerOrder.YXZ is actually better for gyroscope fix, as it can get yaw at any roll.
	//  Consequentially, instead of the roll being limited, the pitch is limited to
	//  90 degrees from the yaw plane. This means trackers may be mounted upside down
	//  or with incorrectly configured IMU rotation, but we will need to compensate for
	//  the pitch.
	private fun fixGyroscope(sensorRotation: Quaternion): Quaternion = getYawQuaternion(sensorRotation, EulerOrder.YXZ).inv()

	private fun fixYaw(sensorRotation: Quaternion, reference: Quaternion): Quaternion {
		var rot = sensorRotation * attachmentFix
		// We need to fix the global yaw offset for the euler yaw calculation
		if (!tracker.isHmd || tracker.trackerPosition != TrackerPosition.HEAD) {
			rot = mountingOrientation.inv() * rot * mountingOrientation
		}
		rot = mountRotFix.inv() * rot * mountRotFix
		// TODO: Get diff from ref to rot, use euler angle (YZX) yaw as output.
		//  This prevents pitch and roll from affecting the alignment.
		rot = getYawQuaternion(rot)
		return rot.inv() * reference.project(Vector3.POS_Y).unit()
	}

	// TODO : isolating yaw for yaw reset bad.
	// The way we isolate the tracker's yaw for yaw reset is
	// incorrect. Projection around the Y-axis is worse.
	// In both cases, the isolated yaw value changes
	// with the tracker's roll when pointing forward.
	// calling twinNearest() makes sure this rotation has the wanted polarity (+-).
	private fun getYawQuaternion(rot: Quaternion, order: EulerOrder = EulerOrder.YZX): Quaternion = EulerAngles(order, 0f, rot.toEulerAngles(order).y, 0f).toQuaternion().twinNearest(rot)

	private fun makeIdentityAdjustmentQuatsFull() {
		val sensorRotation = tracker.getRawRotation()
		gyroFixNoMounting = fixGyroscope(sensorRotation)
		attachmentFixNoMounting = (gyroFixNoMounting * sensorRotation).inv()
		yawFixZeroReference = Quaternion.IDENTITY
	}

	private fun makeIdentityAdjustmentQuatsYaw() {
		var sensorRotation = tracker.getRawRotation()
		sensorRotation = gyroFixNoMounting * sensorRotation
		sensorRotation *= attachmentFixNoMounting
		yawFixZeroReference = fixGyroscope(sensorRotation)
	}
}
