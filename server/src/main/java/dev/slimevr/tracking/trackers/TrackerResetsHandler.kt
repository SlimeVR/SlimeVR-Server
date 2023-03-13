package dev.slimevr.tracking.trackers

import dev.slimevr.config.DriftCompensationConfig
import dev.slimevr.filtering.CircularArrayList
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.*

private const val DRIFT_COOLDOWN_MS = 30000L

/**
 * Class taking care of full reset, yaw reset, mounting reset,
 * and drift compensation logic.
 */
class TrackerResetsHandler(val tracker: Tracker) {

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
		var rot = adjustToReference(rotation)
		rot = adjustToDrift(rot)
		return rot
	}

	/**
	 * Takes a rotation and adjusts it to resets and mounting,
	 * with the identity Quaternion as the reference.
	 */
	fun getIdentityAdjustedRotationFrom(rotation: Quaternion): Quaternion {
		return adjustToIdentity(rotation)
	}

	private fun getAdjustedRawRotation(): Quaternion {
		return adjustToReference(tracker.getRotation())
	}

	private fun getMountedAdjustedRotation(): Quaternion {
		mountingOrientation?.let { return tracker.getRotation() * mountingOrientation as Quaternion }
		return tracker.getRotation()
	}

	private fun getMountedAdjustedDriftRotation(): Quaternion {
		var rot = tracker.getRotation()
		mountingOrientation?.let { rot *= mountingOrientation as Quaternion }
		rot = adjustToDrift(rot)
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
		var rot = rotation
		mountingOrientation?.let { rot *= mountingOrientation as Quaternion }
		rot = gyroFix * rot
		rot *= attachmentFix
		rot *= mountRotFix
		return yawFix * rot
	}

	/**
	 * Converts raw or filtered rotation into zero-reference-adjusted by
	 * applying quaternions produced after [.resetFull],
	 * [.resetYaw].
	 *
	 * @param store Raw or filtered rotation to mutate.
	 */
	private fun adjustToIdentity(rotation: Quaternion): Quaternion {
		var rot = gyroFixNoMounting * rotation
		rot *= attachmentFixNoMounting
		rot = yawFixZeroReference * rot
		return rot
	}

	private fun adjustToDrift(rotation: Quaternion): Quaternion {
		if (compensateDrift && allowDriftCompensation && totalDriftTime > 0) {
			return rotation
				.interpR(
					rotation * averagedDriftQuat,
					driftAmount * ((System.currentTimeMillis() - driftSince).toFloat() / totalDriftTime)
				)
		}
		return rotation
	}

	/**
	 * Reset the tracker so that its current rotation is counted as (0, HMD Yaw,
	 * 0). This allows the tracker to be strapped to body at any pitch and roll.
	 */
	fun resetFull(reference: Quaternion) {
		val rot: Quaternion = adjustToReference(tracker.getRotation())

		fixGyroscope(getMountedAdjustedRotation())
		fixAttachment(getMountedAdjustedRotation())

		makeIdentityAdjustmentQuatsFull()

		fixYaw(getMountedAdjustedRotation(), reference)

		calculateDrift(rot)
	}

	private fun fixGyroscope(sensorRotation: Quaternion) {
		gyroFix = sensorRotation.project(Vector3.POS_Y).unit().inv()
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
		val rot: Quaternion = adjustToReference(tracker.getRotation())

		fixYaw(getMountedAdjustedRotation(), reference)

		makeIdentityAdjustmentQuatsYaw()

		calculateDrift(rot)
	}

	private fun fixYaw(sensorRotation: Quaternion, reference: Quaternion) {
		// Use only yaw HMD rotation
		var rot = gyroFix * sensorRotation
		rot *= attachmentFix
		rot *= mountRotFix
		rot = rot.project(Vector3.POS_Y).unit()
		yawFix = rot.inv().times(reference.project(Vector3.POS_Y).unit())
	}

	fun resetMounting(reverseYaw: Boolean) {
		// Get the current calibrated rotation
		var buffer: Quaternion = getMountedAdjustedDriftRotation()
		buffer = gyroFix * buffer
		buffer *= attachmentFix

		// Reset the vector for the rotation to point straight up
		var rotVector = Vector3(0f, 1f, 0f)

		// Rotate the vector by the quat, then flatten and normalize the vector
		rotVector = buffer.sandwich(rotVector.unit())

		// Calculate the yaw angle using tan
		// Just use an angle offset of zero for unsolvable circumstances
		val yawAngle = atan2(rotVector.x, rotVector.z)

		// Make an adjustment quaternion from the angle
		buffer = EulerAngles(
			EulerOrder.YZX,
			0f,
			(if (reverseYaw) yawAngle else yawAngle - Math.PI) as Float,
			0f
		).toQuaternion()

		val lastRotAdjust: Quaternion = mountRotFix
		mountRotFix = buffer

		// Get the difference from the last adjustment
		buffer *= lastRotAdjust.inv()

		// Apply the yaw rotation difference to the yaw fix quaternion
		yawFix *= buffer.inv()
	}

	/**
	 * Calculates drift since last reset and store the data related to it in
	 * driftQuat, timeAtLastReset and timeForLastReset
	 */
	private fun calculateDrift(beforeQuat: Quaternion) {
		if (compensateDrift && allowDriftCompensation) {
			if (driftSince > 0 && System.currentTimeMillis() - timeAtLastReset > DRIFT_COOLDOWN_MS) {
				// Check and remove from lists to keep them under the reset limit
				if (driftQuats.size == driftQuats.capacity()) {
					driftQuats.removeLast()
					driftTimes.removeLast()
				}

				// Add new drift quaternion
				driftQuats.add(
					adjustToReference(tracker.getRotation()).project(Vector3.POS_Y).unit() *
						(beforeQuat.project(Vector3.POS_Y).unit().inv())
				)

				// Add drift time to total
				driftTimes.add(System.currentTimeMillis() - driftSince)
				totalDriftTime = 0
				for (time in driftTimes) { totalDriftTime += time }

				// Calculate drift Quaternions' weights
				val driftWeights = ArrayList<Float>(driftTimes.size)
				for (time in driftTimes) { driftWeights.add(time.toFloat() / totalDriftTime.toFloat()) }

				// Make it so recent Quaternions weigh more
				for (i in driftWeights.size - 1 downTo 1) {
					// Add some of i-1's value to i
					driftWeights[i] = driftWeights[i] + driftWeights[i - 1] / driftWeights.size
					// Remove the value that was added to i from i-1
					driftWeights[i - 1] = driftWeights[i - 1] - driftWeights[i - 1] / driftWeights.size
				}

				// Set final averaged drift Quaternion
// 				averagedDriftQuat.fromAveragedQuaternions(driftQuats, driftWeights) TODO

				// Save tracker rotation and current time
				rotationSinceReset = driftQuats.latest
				timeAtLastReset = System.currentTimeMillis()
			} else if (System.currentTimeMillis() - timeAtLastReset < DRIFT_COOLDOWN_MS && driftQuats.size > 0) {
				// Replace latest drift quaternion
				rotationSinceReset *= (
					adjustToReference(tracker.getRotation()).project(Vector3.POS_Y).unit() *
						(beforeQuat.project(Vector3.POS_Y).unit().inv())
					)
				driftQuats[driftQuats.size - 1] = rotationSinceReset

				// Add drift time to total
				driftTimes[driftTimes.size - 1] = driftTimes.latest + System.currentTimeMillis() - driftSince
				totalDriftTime = 0
				for (time in driftTimes) { totalDriftTime += time }

				// Calculate drift Quaternions' weights
				val driftWeights = ArrayList<Float>(driftTimes.size)
				for (time in driftTimes) { driftWeights.add(time.toFloat() / totalDriftTime.toFloat()) }

				// Make it so recent Quaternions weigh more
				for (i in driftWeights.size - 1 downTo 1) {
					// Add some of i-1's value to i
					driftWeights[i] = driftWeights[i] + driftWeights[i - 1] / driftWeights.size
					// Remove the value that was added to i from i-1
					driftWeights[i - 1] = driftWeights[i - 1] - driftWeights[i - 1] / driftWeights.size
				}

				// Set final averaged drift Quaternion
// 				averagedDriftQuat.fromAveragedQuaternions(driftQuats, driftWeights) TODO
			} else {
				timeAtLastReset = System.currentTimeMillis()
			}

			driftSince = System.currentTimeMillis()
		}
	}

	private fun makeIdentityAdjustmentQuatsFull() {
		val sensorRotation = tracker.getRawRotation()
		gyroFixNoMounting = sensorRotation.project(Vector3.POS_Y).unit().inv()
		attachmentFixNoMounting = (gyroFixNoMounting * sensorRotation).inv()
	}

	private fun makeIdentityAdjustmentQuatsYaw() {
		var sensorRotation = tracker.getRawRotation()
		sensorRotation = gyroFixNoMounting * sensorRotation
		sensorRotation *= attachmentFixNoMounting
		yawFixZeroReference = sensorRotation.project(Vector3.POS_Y).unit().inv()
	}
}
