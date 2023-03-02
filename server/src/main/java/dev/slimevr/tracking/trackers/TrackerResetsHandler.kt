package dev.slimevr.tracking.trackers

import dev.slimevr.config.DriftCompensationConfig
import dev.slimevr.filtering.CircularArrayList
import io.github.axisangles.ktmath.Quaternion

private val DRIFT_COOLDOWN_MS = 30000L

/**
 * Class taking care of full reset, yaw reset, mounting reset,
 * and drift compensation logic.
 */
class TrackerResetsHandler (val tracker: Tracker) {

	private var compensateDrift = false
	private var driftAmount = 0f
	private val averagedDriftQuat = Quaternion.IDENTITY
	private var rotationSinceReset = Quaternion.IDENTITY
	private var driftQuats = CircularArrayList<Quaternion>(0)
	private var driftTimes = CircularArrayList<Long>(0)
	private var totalDriftTime: Long = 0
	private var driftSince: Long = 0
	private var timeAtLastReset: Long = 0
	var allowDriftCompensation = false

	// Manual mounting orientation
	var mountingOrientation: Quaternion? = null

	// Reference adjustment quats
	private var gyroFix = Quaternion.IDENTITY
	private var attachmentFix = Quaternion.IDENTITY
	private var mountRotFix = Quaternion.IDENTITY
	private var yawFix = Quaternion.IDENTITY

	// Zero-reference adjustment quats for IMU debugging
	private var gyroFixNoMounting = Quaternion.IDENTITY
	private var attachmentFixNoMounting = Quaternion.IDENTITY
	private var yawFixZeroReference = Quaternion.IDENTITY

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
	 * Takes a rotation and adjusts it to resets, mounting,
	 * and drift compensation, with the HMD as the reference.
	 */
	fun getReferenceAdjustedRotationFrom(rotation: Quaternion): Quaternion {
		var rot = rotation
		rot = rot.times(mountingOrientation)
		rot = adjustToReference(rot)
		if (compensateDrift && allowDriftCompensation && totalDriftTime > 0) {
			rot = rot
				.interpR(
					rot.times(averagedDriftQuat), driftAmount
						* ((System.currentTimeMillis() - driftSince).toFloat() / totalDriftTime)
				)
		}
		return rot
	}

	/**
	 * Takes a rotation and adjusts it to resets and mounting,
	 * with the identity Quaternion as the reference.
	 */
	fun getIdentityAdjustedRotationFrom(rotation: Quaternion): Quaternion {
		return adjustToIdentity(rotation)
	}

	fun getAdjustedRawRotation(): Quaternion {
		val rot = tracker.getRotation()
		rot *= mountingOrientation
		adjustToReference(rot)
		return rot
	}

	private fun getMountedAdjustedRotation(): Quaternion {
		val rot = tracker.getRotation()
		rot.multLocal(mountingOrientation)
		return rot
	}

	private fun getMountedAdjustedDriftRotation(): Quaternion {
		val rot = tracker.getRotation()
		rot *= mountingOrientation
		if (compensateDrift && allowDriftCompensation && totalDriftTime > 0) {
			rot
				.slerpLocal(
					rot.mult(averagedDriftQuat), driftAmount
						* ((System.currentTimeMillis() - driftSince).toFloat() / totalDriftTime)
				)
		}
		return rot
	}

	/**
	 * Converts raw or filtered rotation into reference- and
	 * mounting-reset-adjusted by applying quaternions produced after
	 * [.resetFull], [.resetYaw] and
	 * [.resetMounting].
	 *
	 * @param store Raw or filtered rotation to mutate.
	 */
	private fun adjustToReference(rotation: Quaternion): Quaternion {
		var rot = gyroFix * rotation
		rot *= attachmentFix
		rot *= mountRotFix
		rot = yawFix * rot
	}

	/**
	 * Converts raw or filtered rotation into zero-reference-adjusted by
	 * applying quaternions produced after [.resetFull],
	 * [.resetYaw].
	 *
	 * @param store Raw or filtered rotation to mutate.
	 */
	protected fun adjustToIdentity(rotation: Quaternion): Quaternion {
		var rot = gyroFixNoMounting * rotation
		rot *= attachmentFixNoMounting
		rot = yawFixZeroReference * rot
		return rot
	}

	/**
	 * Reset the tracker so that its current rotation is counted as (0, HMD Yaw,
	 * 0). This allows the tracker to be strapped to body at any pitch and roll.
	 */
	fun resetFull(reference: Quaternion) {
		val rot: Quaternion = getAdjustedRawRotation()

		fixGyroscope(getMountedAdjustedRotation())
		fixAttachment(getMountedAdjustedRotation())

		makeIdentityAdjustmentQuatsFull()

		fixYaw(getMountedAdjustedRotation(), reference)

		calculateDrift(rot)
	}

	private fun fixGyroscope(sensorRotation: Quaternion) {
		gyroFix = (sensorRotation.fromAngles(0f, sensorRotation.yaw, 0f)).inv()
	}

	private fun fixAttachment(sensorRotation: Quaternion) {
		attachmentFix = (gyroFix * sensorRotation).inv()
	}

	/**
	 * Reset the tracker so that it's current yaw rotation is counted as <HMD
	 * Yaw>. This allows the tracker to have yaw independent of the HMD. Tracker
	 * should still report yaw as if it was mounted facing HMD, mounting
	 * position should be corrected in the source. Also aligns gyro magnetometer
	 * if it's reliable.
	 */
	fun resetYaw(reference: Quaternion) {
		val rot: Quaternion = getAdjustedRawRotation()

		fixYaw(getMountedAdjustedRotation(), reference)

		makeIdentityAdjustmentQuatsYaw()

		calculateDrift(rot)
	}

	private fun fixYaw(
		sensorRotation: Quaternion,
		reference: Quaternion
	) {
		// Use only yaw HMD rotation
		var rot = sensorRotation
		gyroFix.mult(rot, rot)
		rot.multLocal(attachmentFix)
		rot.multLocal(mountRotFix)
		rot.fromAngles(0f, rot.yaw, 0f)
		yawFix.set(rot.inverseLocal().multLocal(reference.fromAngles(0, reference.yaw, 0)))
	}

	fun resetMounting(reverseYaw: Boolean) {
		// Get the current calibrated rotation
		val buffer: Quaternion = getMountedAdjustedDriftRotation()
		buffer = gyroFix * buffer
		buffer *= attachmentFix

		// Reset the vector for the rotation to point straight up
		rotVector.set(0f, 1f, 0f)

		// Rotate the vector by the quat, then flatten and normalize the vector
		buffer.multLocal(rotVector).setY(0f).normalizeLocal()

		// Calculate the yaw angle using tan
		// Just use an angle offset of zero for unsolvable circumstances
		val yawAngle =
			if (FastMath.isApproxZero(rotVector.x) && FastMath.isApproxZero(rotVector.z)) 0f else FastMath.atan2(
				rotVector.x,
				rotVector.z
			)

		// Make an adjustment quaternion from the angle
		buffer.fromAngles(0f, if (reverseYaw) yawAngle else yawAngle - FastMath.PI, 0f)

		val lastRotAdjust: Quaternion = mountRotFix.clone()
		mountRotFix.set(buffer)

		// Get the difference from the last adjustment
		buffer.multLocal(lastRotAdjust.inverseLocal())

		// Apply the yaw rotation difference to the yaw fix quaternion
		yawFix.multLocal(buffer.inverseLocal())
	}

	/**
	 * Calculates drift since last reset and store the data related to it in
	 * driftQuat, timeAtLastReset and timeForLastReset
	 */
	private fun calculateDrift(beforeQuat: Quaternion) {
		if (compensateDrift && allowDriftCompensation) {
			val rotQuat: Quaternion = getAdjustedRawRotation()
			if (driftSince > 0 &&
				System.currentTimeMillis() - timeAtLastReset > DRIFT_COOLDOWN_MS
			) {
				// Check and remove from lists to keep them under the reset
				// limit
				if (driftQuats.size == driftQuats.capacity()) {
					driftQuats.removeLast()
					driftTimes.removeLast()
				}

				// Add new drift quaternion
				driftQuats
					.add(
						rotQuat
							.fromAngles(0f, rotQuat.yaw, 0f)
							.mult(
								beforeQuat.fromAngles(0f, beforeQuat.yaw, 0f).inverse()
							)
					)

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
					driftWeights[i] =
						driftWeights[i] + driftWeights[i - 1] / driftWeights.size
					// Remove the value that was added to i from i-1
					driftWeights[i - 1] = driftWeights[i - 1] - driftWeights[i - 1] / driftWeights.size
				}

				// Set final averaged drift Quaternion
				averagedDriftQuat.fromAveragedQuaternions(driftQuats, driftWeights)

				// Save tracker rotation and current time
				rotationSinceReset.set(driftQuats.getLatest())
				timeAtLastReset = System.currentTimeMillis()
			} else if (System.currentTimeMillis() - timeAtLastReset < DRIFT_COOLDOWN_MS &&
				driftQuats.size > 0
			) {
				// Replace latest drift quaternion
				rotationSinceReset
					.multLocal(
						rotQuat
							.fromAngles(0f, rotQuat.yaw, 0f)
							.mult(
								beforeQuat.fromAngles(0f, beforeQuat.yaw, 0f).inverse()
							)
					)
				driftQuats
					.set(
						driftQuats.size - 1,
						rotationSinceReset
					)

				// Add drift time to total
				driftTimes[driftTimes.size - 1] =
					driftTimes.latest + System.currentTimeMillis() - driftSince
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
					driftWeights[i] =
						driftWeights[i] + driftWeights[i - 1] / driftWeights.size
					driftWeights[i - 1] = driftWeights[i - 1] - driftWeights[i - 1] / driftWeights.size
				}

				// Set final averaged drift Quaternion
				averagedDriftQuat.fromAveragedQuaternions(driftQuats, driftWeights)
			} else {
				timeAtLastReset = System.currentTimeMillis()
			}
			driftSince = System.currentTimeMillis()
		}
	}

	private fun makeIdentityAdjustmentQuatsFull() {
		val sensorRotation = Quaternion()
		getRawRotation(sensorRotation)
		sensorRotation.fromAngles(0f, sensorRotation.yaw, 0f)
		gyroFixNoMounting.set(sensorRotation).inverseLocal()
		getRawRotation(sensorRotation)
		gyroFixNoMounting.mult(sensorRotation, sensorRotation)
		attachmentFixNoMounting = sensorRotation.inv()
	}

	private fun makeIdentityAdjustmentQuatsYaw() {
		val sensorRotation = Quaternion()
		getRawRotation(sensorRotation)
		gyroFixNoMounting.mult(sensorRotation, sensorRotation)
		sensorRotation.multLocal(attachmentFixNoMounting)
		sensorRotation.fromAngles(0f, sensorRotation.yaw, 0f)
		yawFixZeroReference = sensorRotation.inv()
	}
}
