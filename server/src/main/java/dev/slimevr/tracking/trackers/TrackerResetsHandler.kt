package dev.slimevr.tracking.trackers

import dev.slimevr.config.DriftCompensationConfig
import dev.slimevr.filtering.CircularArrayList
import io.eiren.util.collections.FastList
import io.github.axisangles.ktmath.Quaternion

/**
 * Class taking care of full reset, yaw reset, mounting reset,
 * and drift compensation logic.
 */
class TrackerResetsHandler(val tracker: Tracker) {
	private val DRIFT_COOLDOWN_MS: Long = 30000
	private var compensateDrift = false
	private var driftAmount = 0f
	private val averagedDriftQuat = Quaternion.IDENTITY
	private var driftWeights = FastList<Float>()
	private var rotationSinceReset = Quaternion.IDENTITY
	private var driftQuats = CircularArrayList<Quaternion>(0)
	private var driftTimes = CircularArrayList<Long>(0)
	private var totalDriftTime: Long = 0
	private var driftSince: Long = 0
	private var timeAtLastReset: Long = 0
	var allowDriftCompensation = false
	var mountingOrientation: Quaternion? = null

	fun readDriftCompensationConfig(config: DriftCompensationConfig) {
		compensateDrift = config.enabled
		driftAmount = config.amount
		val maxResets = config.maxResets
		if (compensateDrift && maxResets != driftQuats.capacity()) {
			driftQuats = CircularArrayList<Quaternion>(maxResets)
			driftTimes = CircularArrayList<Long>(maxResets)
		}
	}

	fun getReferenceAdjustedRotationFrom(rotation: Quaternion): Quaternion {
		return Quaternion.IDENTITY
	}

	fun getIdentityAdjustedRotation(rotation: Quaternion): Quaternion {
		return tracker.getRawRotation()
	}

	fun clearDriftCompensation() {
		driftSince = 0L
		timeAtLastReset = 0L
		totalDriftTime = 0L
		driftQuats.clear()
		driftTimes.clear()
	}

	fun resetFull(reference: Quaternion) {
		val rot: Quaternion = getAdjustedRawRotation()
		rot = fixGyroscope(getMountedAdjustedRotation())
		rot = fixAttachment(getMountedAdjustedRotation())
		makeIdentityAdjustmentQuatsFull()
		rot = fixYaw(getMountedAdjustedRotation(), reference)
		rot = calculateDrift()
	}

	fun resetYaw(reference: Quaternion) {
		val rot: Quaternion = getAdjustedRawRotation()
		fixYaw(getMountedAdjustedRotation(), reference)
		makeIdentityAdjustmentQuatsYaw()
		calibrateMag()
		calculateDrift(rot)
	}

	fun resetMounting(reverseYaw: Boolean) {
	}

	private fun calculateDrift(beforeQuat: com.jme3.math.Quaternion) {
		if (compensateDrift && allowDriftCompensation) {
			val rotQuat: Quaternion = getAdjustedRawRotation()
			if (driftSince > 0 &&
				System.currentTimeMillis() - timeAtLastReset > IMUTracker.DRIFT_COOLDOWN_MS
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
				driftWeights.clear()
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
				IMUTracker.rotationSinceReset.set(driftQuats.getLatest())
				timeAtLastReset = System.currentTimeMillis()
			} else if (System.currentTimeMillis() - timeAtLastReset < IMUTracker.DRIFT_COOLDOWN_MS &&
				driftQuats.size > 0
			) {
				// Replace latest drift quaternion
				IMUTracker.rotationSinceReset
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
						IMUTracker.rotationSinceReset
					)

				// Add drift time to total
				driftTimes[driftTimes.size - 1] =
					driftTimes.latest + System.currentTimeMillis() - driftSince
				totalDriftTime = 0
				for (time in driftTimes) {
					totalDriftTime += time
				}

				// Calculate drift Quaternions' weights
				driftWeights.clear()
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
}
