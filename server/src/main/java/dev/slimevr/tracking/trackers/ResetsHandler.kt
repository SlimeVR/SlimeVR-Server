package dev.slimevr.tracking.trackers

import dev.slimevr.filtering.CircularArrayList
import io.eiren.util.collections.FastList
import io.github.axisangles.ktmath.Quaternion

/**
 * Class taking care of full reset, yaw reset, mounting reset,
 * and drift compensation logic.
 */
class ResetsHandler(val tracker: Tracker) {
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

	fun setDriftCompensationSettings(enabled: Boolean, amount: Float, maxResets: Int) {
		compensateDrift = enabled
		driftAmount = amount
		if (enabled && maxResets != driftQuats.capacity()) {
			driftQuats = CircularArrayList<Quaternion>(maxResets)
			driftTimes = CircularArrayList<Long>(maxResets)
		}
	}


	fun resetFull(reference: Quaternion) {
	}

	fun resetYaw(reference: Quaternion) {
	}

	fun resetMounting(reverseYaw: Boolean) {
	}
}
