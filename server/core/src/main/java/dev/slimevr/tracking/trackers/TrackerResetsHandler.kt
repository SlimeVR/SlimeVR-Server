package dev.slimevr.tracking.trackers

import com.jme3.math.FastMath
import com.jme3.system.NanoTimer
import dev.slimevr.VRServer
import dev.slimevr.config.ArmsResetModes
import dev.slimevr.config.DriftCompensationConfig
import dev.slimevr.config.ResetsConfig
import dev.slimevr.filtering.CircularArrayList
import io.eiren.math.FloatMath.animateEase
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.*

private const val DRIFT_COOLDOWN_MS = 50000L

/**
 * Class taking care of full reset, yaw reset, mounting reset,
 * and drift compensation logic.
 */
class TrackerResetsHandler(val tracker: Tracker) {

	private var driftAmount = 0f
	private var averagedDriftQuat = Quaternion.IDENTITY
	private var rotationSinceReset = Quaternion.IDENTITY
	private var driftQuats = CircularArrayList<Quaternion>(0)
	private var driftTimes = CircularArrayList<Long>(0)
	private var totalDriftTime: Long = 0
	private var driftSince: Long = 0
	private var timeAtLastReset: Long = 0
	private var compensateDrift = false
	private var driftCompensationEnabled = false
	private var resetMountingFeet = false
	private var armsResetMode = ArmsResetModes.BACK
	private var yawResetSmoothTime = 0.0f
	private lateinit var fpsTimer: NanoTimer
	var allowDriftCompensation = false
	var lastResetQuaternion: Quaternion? = null

	// Manual mounting orientation
	var mountingOrientation: Quaternion = EulerAngles(
		EulerOrder.YZX,
		0f,
		Math.PI.toFloat(),
		0f,
	).toQuaternion()
		set(value) {
			field = value
			// Clear the mounting reset now that it's been set manually
			clearMounting()
		}

	// Reference adjustment quats
	private var gyroFix = Quaternion.IDENTITY
	private var attachmentFix = Quaternion.IDENTITY
	var mountRotFix = Quaternion.IDENTITY
		private set
	private var yawFix = Quaternion.IDENTITY

	// Yaw reset smoothing vars
	private var yawFixOld = Quaternion.IDENTITY
	private var yawFixSmoothIncremental = Quaternion.IDENTITY
	private var yawResetSmoothTimeRemain = 0.0f

	// Zero-reference/identity adjustment quats for IMU debugging
	private var gyroFixNoMounting = Quaternion.IDENTITY
	private var attachmentFixNoMounting = Quaternion.IDENTITY
	private var yawFixZeroReference = Quaternion.IDENTITY
	private var tposeFix = Quaternion.IDENTITY

	/**
	 * Reads/loads drift compensation settings from given config
	 */
	fun readDriftCompensationConfig(config: DriftCompensationConfig) {
		compensateDrift = config.enabled
		driftAmount = config.amount
		val maxResets = config.maxResets

		if (compensateDrift && maxResets != driftQuats.capacity()) {
			driftQuats = CircularArrayList<Quaternion>(maxResets)
			driftTimes = CircularArrayList<Long>(maxResets)
		}

		refreshDriftCompensationEnabled()
	}

	/**
	 * Clears drift compensation data
	 */
	fun clearDriftCompensation() {
		driftSince = 0L
		timeAtLastReset = 0L
		totalDriftTime = 0L
		driftQuats.clear()
		driftTimes.clear()
	}

	/**
	 * Checks for compensateDrift, allowDriftCompensation, and if
	 * a computed head tracker exists.
	 */
	fun refreshDriftCompensationEnabled() {
		driftCompensationEnabled = compensateDrift &&
			allowDriftCompensation &&
			TrackerUtils.getNonInternalNonImuTrackerForBodyPosition(
				VRServer.instance.allTrackers,
				TrackerPosition.HEAD,
			) != null
	}

	/**
	 * Reads/loads arms reset mode settings from given config
	 */
	fun readResetConfig(config: ResetsConfig) {
		resetMountingFeet = config.resetMountingFeet
		armsResetMode = config.mode
		yawResetSmoothTime = config.yawResetSmoothTime
		if (!::fpsTimer.isInitialized) {
			fpsTimer = VRServer.instance.fpsTimer
		}
	}

	/**
	 * Takes a rotation and adjusts it to resets, mounting,
	 * and drift compensation, with the HMD as the reference.
	 */
	fun getReferenceAdjustedDriftRotationFrom(rotation: Quaternion): Quaternion = adjustToDrift(adjustToYawResetSmoothing(adjustToReference(rotation)))

	/**
	 * Takes a rotation and adjusts it to resets and mounting,
	 * with the identity Quaternion as the reference.
	 */
	fun getIdentityAdjustedDriftRotationFrom(rotation: Quaternion): Quaternion = adjustToDrift(adjustToIdentity(rotation))

	/**
	 * Get the adjusted accel from yawFixZeroReference
	 */
	fun getReferenceAdjustedAccel(rawRot: Quaternion, accel: Vector3): Vector3 = (adjustToReference(rawRot) / yawFix).sandwich(accel)

	/**
	 * Converts raw or filtered rotation into reference- and
	 * mounting-reset-adjusted by applying quaternions produced after
	 * full reset, yaw rest and mounting reset
	 */
	private fun adjustToReference(rotation: Quaternion): Quaternion {
		var rot = rotation
		rot *= mountingOrientation
		rot = gyroFix * rot
		rot *= attachmentFix
		rot *= mountRotFix
		rot = yawFix * rot
		return rot * tposeFix
	}

	/**
	 * Converts raw or filtered rotation into zero-reference-adjusted by
	 * applying quaternions produced after full reset and yaw reset only
	 */
	private fun adjustToIdentity(rotation: Quaternion): Quaternion {
		var rot = gyroFixNoMounting * rotation
		rot *= attachmentFixNoMounting
		rot = yawFixZeroReference * rot
		return rot
	}

	/**
	 * Adjust the given rotation for drift compensation if enabled,
	 * and returns it
	 */
	private fun adjustToDrift(rotation: Quaternion): Quaternion {
		if (driftCompensationEnabled && totalDriftTime > 0) {
			return rotation
				.interpR(
					averagedDriftQuat * rotation,
					driftAmount * ((System.currentTimeMillis() - driftSince).toFloat() / totalDriftTime),
				)
		}
		return rotation
	}

	/**
	 * Apply yaw reset smoothing to quaternion rotated to new yaw
	 * fix and returns smoothed quaternion
	 */
	private fun adjustToYawResetSmoothing(rotation: Quaternion): Quaternion {
		if (yawResetSmoothTime > 0.0f) {
			return rotation * yawFixSmoothIncremental
		}
		return rotation
	}

	/**
	 * Reset the tracker so that its current rotation is counted as (0, HMD Yaw,
	 * 0). This allows the tracker to be strapped to body at any pitch and roll.
	 */
	fun resetFull(reference: Quaternion) {
		// Adjust for T-Pose
		if ((isLeftArmTracker() && armsResetMode == ArmsResetModes.TPOSE_DOWN)) {
			tposeFix = EulerAngles(
				EulerOrder.YZX,
				0f,
				0f,
				-FastMath.HALF_PI,
			).toQuaternion()
		} else if ((isRightArmTracker() && armsResetMode == ArmsResetModes.TPOSE_DOWN)) {
			tposeFix = EulerAngles(EulerOrder.YZX, 0f, 0f, FastMath.HALF_PI).toQuaternion()
		} else {
			tposeFix = Quaternion.IDENTITY
		}

		lastResetQuaternion = adjustToReference(tracker.getRawRotation())

		val oldRot = adjustToReference(tracker.getRawRotation())

		if (tracker.needsMounting) {
			gyroFix = fixGyroscope(tposeFix * tracker.getRawRotation() * mountingOrientation)
		} else {
			// Set mounting to the HMD's yaw so that the non-mounting-adjusted
			// tracker goes forward.
			mountRotFix = getYawQuaternion(reference)
		}
		attachmentFix = fixAttachment(tracker.getRawRotation() * mountingOrientation)

		makeIdentityAdjustmentQuatsFull()

		yawFix = fixYaw(tracker.getRawRotation() * mountingOrientation, reference)
		yawResetSmoothTimeRemain = 0.0f

		calculateDrift(oldRot)

		if (this.tracker.lastResetStatus != 0u) {
			VRServer.instance.statusSystem.removeStatus(this.tracker.lastResetStatus)
			this.tracker.lastResetStatus = 0u
		}
	}

	/**
	 * Reset the tracker so that its current yaw rotation is aligned with the HMD's
	 * Yaw. This allows the tracker to have yaw independent of the HMD. Tracker
	 * should still report yaw as if it was mounted facing HMD, mounting
	 * position should be corrected in the source.
	 */
	fun resetYaw(reference: Quaternion) {
		lastResetQuaternion = adjustToReference(tracker.getRawRotation())

		val rot: Quaternion = adjustToReference(tracker.getRawRotation())

		yawFixOld = yawFix
		yawFix = fixYaw(tracker.getRawRotation() * mountingOrientation, reference)
		yawResetSmoothTimeRemain = 0.0f

		makeIdentityAdjustmentQuatsYaw()

		calculateDrift(rot)

		// Start at yaw before reset if smoothing enabled
		if (yawResetSmoothTime > 0.0f) {
			yawResetSmoothTimeRemain = yawResetSmoothTime
			yawFixSmoothIncremental = yawFixOld / yawFix
		}

		// Remove the status if yaw reset was performed after the tracker
		// was disconnected and connected.
		if (this.tracker.lastResetStatus != 0u && this.tracker.statusResetRecently) {
			VRServer.instance.statusSystem.removeStatus(this.tracker.lastResetStatus)
			this.tracker.statusResetRecently = false
			this.tracker.lastResetStatus = 0u
		}
	}

	/**
	 * Perform the math to align the tracker to go forward
	 * and stores it in mountRotFix, and adjusts yawFix
	 */
	fun resetMounting(reference: Quaternion) {
		if (!resetMountingFeet && isFootTracker()) {
			return
		}

		// Get the current calibrated rotation
		var buffer = adjustToDrift(tracker.getRawRotation() * mountingOrientation)
		buffer *= tposeFix
		buffer = gyroFix * buffer
		buffer *= attachmentFix

		// TODO adjust buffer to reference

		// Rotate a vector pointing up by the quat
		val rotVector = buffer.sandwich(Vector3.POS_Y)

		// Calculate the yaw angle using tan
		var yawAngle = atan2(rotVector.x, rotVector.z)

		// Adjust for T-Pose
		if ((isLeftArmTracker() && armsResetMode == ArmsResetModes.TPOSE_DOWN) ||
			(isRightArmTracker() && armsResetMode == ArmsResetModes.TPOSE_UP)
		) {
			// Tracker goes right
			yawAngle -= FastMath.HALF_PI
		}
		if ((isLeftArmTracker() && armsResetMode == ArmsResetModes.TPOSE_UP) ||
			(isRightArmTracker() && armsResetMode == ArmsResetModes.TPOSE_DOWN)
		) {
			// Tracker goes left
			yawAngle += FastMath.HALF_PI
		}

		// Adjust for forward/back arms and thighs
		val isLowerArmBack =
			armsResetMode == ArmsResetModes.BACK && (isLeftLowerArmTracker() || isRightLowerArmTracker())
		val isArmForward =
			armsResetMode == ArmsResetModes.FORWARD && (isLeftArmTracker() || isRightArmTracker())
		if (!isThighTracker() && !isArmForward && !isLowerArmBack) {
			// Tracker goes back
			yawAngle -= FastMath.PI
		}

		// Make an adjustment quaternion from the angle
		buffer = EulerAngles(EulerOrder.YZX, 0f, yawAngle, 0f).toQuaternion()

		// Get the difference from the last mounting to the current mounting and apply
		// the difference to the yaw fix quaternion to correct for the rotation change
		yawFix /= (buffer / mountRotFix)
		yawResetSmoothTimeRemain = 0.0f
		mountRotFix = buffer
	}

	fun clearMounting() {
		// If there is no mounting reset quaternion, skip clearing
		if (mountRotFix == Quaternion.IDENTITY) return

		// Undo the effect on yaw fix
		yawFix *= mountRotFix.inv()
		yawResetSmoothTimeRemain = 0.0f
		// Clear the mounting reset
		mountRotFix = Quaternion.IDENTITY
	}

	private fun fixGyroscope(sensorRotation: Quaternion): Quaternion = getYawQuaternion(sensorRotation).inv()

	private fun fixAttachment(sensorRotation: Quaternion): Quaternion = (gyroFix * sensorRotation).inv()

	private fun fixYaw(sensorRotation: Quaternion, reference: Quaternion): Quaternion {
		var rot = gyroFix * sensorRotation
		rot *= attachmentFix
		rot *= mountRotFix
		rot = getYawQuaternion(rot)
		return rot.inv() * reference.project(Vector3.POS_Y).unit()
	}

	// TODO : isolating yaw for yaw reset bad.
	// The way we isolate the tracker's yaw for yaw reset is
	// incorrect. Projection around the Y-axis is worse.
	// In both cases, the isolated yaw value changes
	// with the tracker's roll when pointing forward.
	private fun getYawQuaternion(rot: Quaternion): Quaternion = EulerAngles(EulerOrder.YZX, 0f, rot.toEulerAngles(EulerOrder.YZX).y, 0f).toQuaternion()

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

	/**
	 * Calculates drift since last reset and store the data related to it in
	 * driftQuat and timeAtLastReset
	 */
	private fun calculateDrift(beforeQuat: Quaternion) {
		if (driftCompensationEnabled) {
			val rotQuat = adjustToReference(tracker.getRawRotation())

			if (driftSince > 0 && System.currentTimeMillis() - timeAtLastReset > DRIFT_COOLDOWN_MS) {
				// Check and remove from lists to keep them under the reset limit
				if (driftQuats.size == driftQuats.capacity()) {
					driftQuats.removeLast()
					driftTimes.removeLast()
				}

				// Add new drift quaternion
				driftQuats.add(getYawQuaternion(rotQuat) / getYawQuaternion(beforeQuat))

				// Add drift time to total
				driftTimes.add(System.currentTimeMillis() - driftSince)
				totalDriftTime = 0
				for (time in driftTimes) {
					totalDriftTime += time
				}

				// Calculate drift Quaternions' weights
				val driftWeights = ArrayList<Float>(driftTimes.size)
				for (time in driftTimes) {
					driftWeights.add(time.toFloat() / totalDriftTime.toFloat())
				}

				// Make it so recent Quaternions weigh more
				for (i in driftWeights.size - 1 downTo 1) {
					// Add some of i-1's value to i
					driftWeights[i] = driftWeights[i] + driftWeights[i - 1] / driftWeights.size
					// Remove the value that was added to i from i-1
					driftWeights[i - 1] = driftWeights[i - 1] - driftWeights[i - 1] / driftWeights.size
				}

				// Set final averaged drift Quaternion
				averagedDriftQuat = fromAveragedQuaternions(driftQuats, driftWeights)

				// Save tracker rotation and current time
				rotationSinceReset = driftQuats.latest
				timeAtLastReset = System.currentTimeMillis()
			} else if (System.currentTimeMillis() - timeAtLastReset < DRIFT_COOLDOWN_MS && driftQuats.size > 0) {
				// Replace latest drift quaternion
				rotationSinceReset *= (getYawQuaternion(rotQuat) / getYawQuaternion(beforeQuat))
				driftQuats[driftQuats.size - 1] = rotationSinceReset

				// Add drift time to total
				driftTimes[driftTimes.size - 1] = driftTimes.latest + System.currentTimeMillis() - driftSince
				totalDriftTime = 0
				for (time in driftTimes) {
					totalDriftTime += time
				}

				// Calculate drift Quaternions' weights
				val driftWeights = ArrayList<Float>(driftTimes.size)
				for (time in driftTimes) {
					driftWeights.add(time.toFloat() / totalDriftTime.toFloat())
				}

				// Make it so recent Quaternions weigh more
				for (i in driftWeights.size - 1 downTo 1) {
					// Add some of i-1's value to i
					driftWeights[i] = driftWeights[i] + driftWeights[i - 1] / driftWeights.size
					// Remove the value that was added to i from i-1
					driftWeights[i - 1] = driftWeights[i - 1] - driftWeights[i - 1] / driftWeights.size
				}

				// Set final averaged drift Quaternion
				averagedDriftQuat = fromAveragedQuaternions(driftQuats, driftWeights)
			} else {
				timeAtLastReset = System.currentTimeMillis()
			}

			driftSince = System.currentTimeMillis()
		}
	}

	/**
	 * Calculates and returns the averaged Quaternion
	 * from the given Quaternions and weights.
	 */
	private fun fromAveragedQuaternions(
		qn: CircularArrayList<Quaternion>,
		tn: ArrayList<Float>,
	): Quaternion {
		var totalMatrix = qn[0].toMatrix() * tn[0]
		for (i in 1 until qn.size) {
			totalMatrix += (qn[i].toMatrix() * tn[i])
		}
		return totalMatrix.toQuaternion()
	}

	/**
	 * Update yaw reset smoothing time
	 */
	@Synchronized
	fun update() {
		if (yawResetSmoothTimeRemain > 0.0f) {
			var deltaTime = 0.001f
			if (::fpsTimer.isInitialized) {
				deltaTime = fpsTimer.timePerFrame
			}
			yawResetSmoothTimeRemain = yawResetSmoothTimeRemain - deltaTime
			if (yawResetSmoothTimeRemain > 0.0f) {
				// Remaining time decreases to 0, so the interpolation is reversed
				yawFixSmoothIncremental = yawFix
					.interpR(
						yawFixOld,
						animateEase(yawResetSmoothTimeRemain / yawResetSmoothTime),
					)
				yawFixSmoothIncremental /= yawFix
			}
		}
	}

	private fun isThighTracker(): Boolean {
		tracker.trackerPosition?.let {
			return it == TrackerPosition.LEFT_UPPER_LEG ||
				it == TrackerPosition.RIGHT_UPPER_LEG
		}
		return false
	}

	private fun isLeftArmTracker(): Boolean {
		tracker.trackerPosition?.let {
			return it == TrackerPosition.LEFT_SHOULDER ||
				it == TrackerPosition.LEFT_UPPER_ARM ||
				it == TrackerPosition.LEFT_LOWER_ARM ||
				it == TrackerPosition.LEFT_HAND
		}
		return false
	}

	private fun isRightArmTracker(): Boolean {
		tracker.trackerPosition?.let {
			return it == TrackerPosition.RIGHT_SHOULDER ||
				it == TrackerPosition.RIGHT_UPPER_ARM ||
				it == TrackerPosition.RIGHT_LOWER_ARM ||
				it == TrackerPosition.RIGHT_HAND
		}
		return false
	}

	private fun isLeftLowerArmTracker(): Boolean {
		tracker.trackerPosition?.let {
			return it == TrackerPosition.LEFT_LOWER_ARM ||
				it == TrackerPosition.LEFT_HAND
		}
		return false
	}

	private fun isRightLowerArmTracker(): Boolean {
		tracker.trackerPosition?.let {
			return it == TrackerPosition.RIGHT_LOWER_ARM ||
				it == TrackerPosition.RIGHT_HAND
		}
		return false
	}

	private fun isFootTracker(): Boolean {
		tracker.trackerPosition?.let {
			return it == TrackerPosition.LEFT_FOOT ||
				it == TrackerPosition.RIGHT_FOOT
		}
		return false
	}
}
