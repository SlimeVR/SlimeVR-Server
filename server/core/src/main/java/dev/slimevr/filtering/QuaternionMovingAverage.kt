package dev.slimevr.filtering

import com.jme3.system.NanoTimer
import dev.slimevr.VRServer
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Quaternion.Companion.IDENTITY

// influences the range of smoothFactor.
private const val SMOOTH_MULTIPLIER = 42f
private const val SMOOTH_MIN = 11f

// influences the range of predictFactor
private const val PREDICT_MULTIPLIER = 15f
private const val PREDICT_MIN = 10f

// how many past rotations are used for prediction.
private const val PREDICT_BUFFER = 6

class QuaternionMovingAverage(
	val type: TrackerFilters,
	var amount: Float = 0f,
	initialRotation: Quaternion = IDENTITY,
) {
	var filteredQuaternion = IDENTITY
	var filteringImpact = 0f
	private var smoothFactor = 0f
	private var predictFactor = 0f
	private var rotBuffer: CircularArrayList<Quaternion>? = null
	private var latestQuaternion = IDENTITY
	private var smoothingQuaternion = IDENTITY
	private val fpsTimer = if (VRServer.instanceInitialized) VRServer.instance.fpsTimer else NanoTimer()
	private var timeSinceUpdate = 0f

	init {
		// amount should range from 0 to 1.
		// GUI should clamp it from 0.01 (1%) or 0.1 (10%)
		// to 1 (100%).
		amount = amount.coerceAtLeast(0f)
		if (type == TrackerFilters.SMOOTHING) {
			// lower smoothFactor = more smoothing
			smoothFactor = SMOOTH_MULTIPLIER * (1 - amount.coerceAtMost(1f)) + SMOOTH_MIN
			// Totally a hack
			if (amount > 1) {
				smoothFactor /= amount
			}
		}
		if (type == TrackerFilters.PREDICTION) {
			// higher predictFactor = more prediction
			predictFactor = PREDICT_MULTIPLIER * amount + PREDICT_MIN
			rotBuffer = CircularArrayList(PREDICT_BUFFER)
		}
		resetQuats(initialRotation)
	}

	// Runs at up to 1000hz. We use a timer to make it framerate-independent
	// since it runs a bit below 1000hz in practice.
	@Synchronized
	fun update() {
		if (type == TrackerFilters.PREDICTION) {
			if (rotBuffer!!.size > 0) {
				var quatBuf = latestQuaternion

				// Applies the past rotations to the current rotation
				rotBuffer?.forEach { quatBuf *= it }

				// Calculate how much to slerp
				val amt = predictFactor * fpsTimer.timePerFrame

				// Slerps the target rotation to that predicted rotation by amt
				filteredQuaternion = filteredQuaternion.interpR(quatBuf, amt)
			}
		} else if (type == TrackerFilters.SMOOTHING) {
			// Make it framerate-independent
			timeSinceUpdate += fpsTimer.timePerFrame

			// Calculate the slerp factor based off the smoothFactor and smoothingCounter
			// limit to 1 to not overshoot
			val amt = (smoothFactor * timeSinceUpdate).coerceAtMost(1f)

			// Smooth towards the target rotation by the slerp factor
			filteredQuaternion = smoothingQuaternion.interpR(latestQuaternion, amt)
		}

		filteringImpact = latestQuaternion.angleToR(filteredQuaternion)
	}

	@Synchronized
	fun addQuaternion(q: Quaternion) {
		if (type == TrackerFilters.PREDICTION) {
			if (rotBuffer!!.size == rotBuffer!!.capacity()) {
				rotBuffer?.removeLast()
			}

			// Gets and stores the rotation between the last 2 quaternions
			rotBuffer?.add(latestQuaternion.inv().times(q))
		} else if (type == TrackerFilters.SMOOTHING) {
			timeSinceUpdate = 0f
			smoothingQuaternion = filteredQuaternion
		} else {
			// No filtering; just keep track of rotations (for going over 180 degrees)
			filteredQuaternion = q.twinNearest(filteredQuaternion)
		}

		latestQuaternion = q
	}

	@Synchronized
	fun resetQuats(q: Quaternion) {
		rotBuffer?.clear()
		latestQuaternion = q
		filteredQuaternion = q
		addQuaternion(q)
	}
}
