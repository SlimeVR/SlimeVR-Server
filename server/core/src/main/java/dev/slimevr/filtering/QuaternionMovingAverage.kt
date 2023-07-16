package dev.slimevr.filtering

import com.jme3.system.NanoTimer
import dev.slimevr.VRServer
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Quaternion.Companion.IDENTITY

// influences the range of smoothFactor.
private const val SMOOTH_MULTIPLIER = 42f
private const val SMOOTH_MIN = 12f

// influences the range of predictFactor
private const val PREDICT_MULTIPLIER = 14f
private const val PREDICT_MIN = 10f

// how many past rotations are used for prediction.
private const val PREDICT_BUFFER = 6

class QuaternionMovingAverage(
	val type: TrackerFilters,
	var amount: Float,
	initialRotation: Quaternion,
) {
	private var smoothFactor = 0f
	private var predictFactor = 0f
	private lateinit var rotBuffer: CircularArrayList<Quaternion>
	private var latestQuaternion = IDENTITY
	private var smoothingQuaternion = IDENTITY
	var filteredQuaternion = IDENTITY
	private val fpsTimer: NanoTimer = VRServer.instance.fpsTimer
	private var smoothingCounter = 0

	init {
		// amount should range from 0 to 1.
		// GUI should clamp it from 0.01 (1%) or 0.1 (10%)
		// to 1 (100%).
		amount = Math.max(amount, 0f)
		if (type === TrackerFilters.SMOOTHING) {
			// lower smoothFactor = more smoothing
			smoothFactor = SMOOTH_MULTIPLIER * (1 - amount) + SMOOTH_MIN
		}
		if (type === TrackerFilters.PREDICTION) {
			// higher predictFactor = more prediction
			predictFactor = PREDICT_MULTIPLIER * amount + PREDICT_MIN
			rotBuffer = CircularArrayList(PREDICT_BUFFER)
		}
		filteredQuaternion = initialRotation
		latestQuaternion = initialRotation
		smoothingQuaternion = initialRotation
	}

	// Runs at up to 1000hz. We use a timer to make it framerate-independent
	// since it runs between 850hz to 900hz in practice.
	@Synchronized
	fun update() {
		if (type === TrackerFilters.PREDICTION) {
			if (rotBuffer.size > 0) {
				var quatBuf = latestQuaternion

				// Applies the past rotations to the current rotation
				rotBuffer.forEach { quatBuf *= it }

				// Slerps the target rotation to that predicted rotation by
				// a certain factor.
				filteredQuaternion = filteredQuaternion.interpR(quatBuf, predictFactor * fpsTimer.timePerFrame)
			}
		}
		if (type === TrackerFilters.SMOOTHING) {
			// Calculate the slerp factor and limit it to 1 max
			smoothingCounter++
			val amt = (smoothFactor * fpsTimer.timePerFrame * smoothingCounter).coerceAtMost(1f)

			// Smooth towards the target rotation
			filteredQuaternion = smoothingQuaternion.interpR(latestQuaternion, amt)
		}
	}

	@Synchronized
	fun addQuaternion(q: Quaternion) {
		if (type === TrackerFilters.PREDICTION) {
			if (rotBuffer.size == rotBuffer.capacity()) {
				rotBuffer.removeLast()
			}

			// Gets and stores the rotation between the last 2 quaternions
			rotBuffer.add(latestQuaternion.inv().times(q))
		}
		if (type === TrackerFilters.SMOOTHING) {
			smoothingCounter = 0
			smoothingQuaternion = filteredQuaternion
		}
		latestQuaternion = q
	}
}
