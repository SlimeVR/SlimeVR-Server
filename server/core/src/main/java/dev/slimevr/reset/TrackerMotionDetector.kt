package dev.slimevr.reset

import dev.slimevr.tracking.trackers.Tracker
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.scheduleAtFixedRate

class TrackerMotionDetector(private val trackersProvider: () -> List<Tracker>) {

	private class MotionState {
		var rot: Quaternion = Quaternion.IDENTITY
		var acc: Vector3 = Vector3(0f, 0f, 0f)
		val deltas = ArrayDeque<Float>()
	}

	@Volatile
	private var maxVelocity = 0f

	private var timer: Timer? = null
	private var task: TimerTask? = null

	@Volatile
	private var lastPolled = 0L
	private val states = HashMap<String, MotionState>()

	@Synchronized
	fun isMoving(): Boolean {
		lastPolled = System.currentTimeMillis()
		if (task == null) {
			start()
		}
		return maxVelocity >= MOVEMENT_THRESHOLD
	}

	@Synchronized
	private fun start() {
		states.clear()
		timer = Timer("TrackerMotionDetector", true)
		task = timer!!.scheduleAtFixedRate(SAMPLE_MS, SAMPLE_MS) {
			if (System.currentTimeMillis() - lastPolled > IDLE_STOP_MS) {
				stop()
			} else {
				try {
					sample()
				} catch (e: Exception) {
					stop()
				}
			}
		}
	}

	@Synchronized
	private fun stop() {
		task?.cancel()
		timer?.cancel()
		task = null
		timer = null
		states.clear()
		maxVelocity = 0f
	}

	private fun sample() {
		var max = 0f
		for (tracker in trackersProvider()) {
			if (!tracker.hasRotation) continue
			var state = states[tracker.name]
			if (state == null) {
				state = MotionState()
				state.rot = tracker.getRawRotation()
				state.acc = tracker.getAcceleration()
				states[tracker.name] = state
				continue
			}
			val rot = tracker.getRawRotation() * state.rot.inv()
			val acc = tracker.getAcceleration() - state.acc
			val dif = (
				(rot.x * rot.x + rot.y * rot.y + rot.z * rot.z) *
					50f +
					(acc.x * acc.x + acc.y * acc.y + acc.z * acc.z) /
					1000f
				).coerceAtMost(1f)
			if (state.deltas.size >= WINDOW_SAMPLES) state.deltas.removeFirst()
			state.deltas.addLast(dif)
			var sum = 0f
			for (delta in state.deltas) sum += delta
			max = maxOf(max, sum.coerceIn(0f, 1f))
			state.rot = tracker.getRawRotation()
			state.acc = tracker.getAcceleration()
		}
		maxVelocity = max
	}

	companion object {
		// Values matched with GUI tracker.ts
		private const val SAMPLE_MS = 100L
		private const val WINDOW_SAMPLES = 5
		private const val IDLE_STOP_MS = 5000L
		private const val MOVEMENT_THRESHOLD = 4 * 0.125f
	}
}
