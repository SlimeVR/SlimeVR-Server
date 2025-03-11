package dev.slimevr.filtering

import com.jme3.system.NanoTimer
import dev.slimevr.VRServer
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Quaternion.Companion.IDENTITY
import kotlin.math.abs

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
	private var lastAmt = 0f

	// Polarity tracking
	private var flipped = false
	private var lastDot = 1f

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
				filteredQuaternion = filteredQuaternion.interpQ(quatBuf, amt)
			}
		} else if (type == TrackerFilters.SMOOTHING) {
			// Calculate the slerp factor based off the last amount and smoothFactor
			// limit to 1 to not overshoot
			val amt = (
				lastAmt + (smoothFactor * fpsTimer.timePerFrame)
				).coerceAtMost(1f)
			lastAmt = amt

			// Smooth towards the target rotation by the slerp factor
			filteredQuaternion = smoothingQuaternion.interpQ(latestQuaternion, amt)
		}

		filteringImpact = latestQuaternion.angleToR(filteredQuaternion)
	}

	@Synchronized
	fun addQuaternion(q: Quaternion) {
		// Keep track of rotation polarity (for going over 180 degrees)
		val dot = q.dot(latestQuaternion)
		val dotDiff = lastDot - dot
		lastDot = dot

		// If the rotation is extreme to the opposite polarity
		if (abs(dotDiff) > 1.5f) {
			// Flip polarity
			flipped = !flipped
		}

		val polQ = if (flipped) -q else q

		if (type == TrackerFilters.PREDICTION) {
			if (rotBuffer!!.size == rotBuffer!!.capacity()) {
				rotBuffer?.removeLast()
			}

			// Gets and stores the rotation between the last 2 quaternions
			rotBuffer?.add(latestQuaternion.inv().times(polQ))
		} else if (type == TrackerFilters.SMOOTHING) {
			lastAmt = 0f
			smoothingQuaternion = filteredQuaternion
		}

		latestQuaternion = polQ
	}

	@Synchronized
	fun resetQuats(q: Quaternion) {
		// Clear prediction buffer
		rotBuffer?.clear()
		// Reset tracked quaternions
		latestQuaternion = q
		filteredQuaternion = q
		// Reset polarity tracking
		flipped = false
		lastDot = 1f
		// Set the current quaternion
		addQuaternion(q)
	}
}
