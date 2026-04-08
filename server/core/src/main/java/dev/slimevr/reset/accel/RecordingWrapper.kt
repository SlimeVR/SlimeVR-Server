package dev.slimevr.reset.accel

import dev.slimevr.autobone.StatsCalculator
import dev.slimevr.tracking.trackers.Tracker
import io.github.axisangles.ktmath.Vector3
import org.apache.commons.collections4.queue.CircularFifoQueue
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration.Companion.milliseconds

data class RecordingWrapper(val tracker: Tracker, var moving: Boolean = false) {
	// Buffer for performing rest detection
	val restDetect = CircularFifoQueue<RecordingSample>(8)

	// List capacity assuming ~10 seconds at 100 TPS
	val recording: MutableList<RecordingSample> = ArrayList(1024)

	// Whether to dump our rest detection into the recording on the next sample
	var dumpRest = false

	fun makeSample(time: ComparableTimeMark, hmdPos: Vector3): RecordingSample = RecordingSample(
		time,
		tracker.getAcceleration(),
		tracker.getRotation(),
		hmdPos,
	)

	fun addRestSample(sample: RecordingSample): Boolean {
		// Collect samples for rest detection at a constant-ish rate if possible
		return if (moving && restDetect.isNotEmpty()) {
			val lastSampleTime = restDetect.last().time
			// Try to have TPS at a lower rate
			if (sample.time - lastSampleTime > REST_INTERVAL) {
				restDetect.add(sample)
			} else {
				false
			}
		} else {
			restDetect.add(sample)
		}
	}

	fun updateRestState(new: RecordingSample): Boolean {
		if (restDetect.size < 4) return moving

		val stats = StatsCalculator()
		for (sample in restDetect) {
			stats.addValue(sample.accel.len())
		}

		// Conditions to start or remain moving
		// TODO: Add rotation as a rest metric
		moving = if (moving) {
			stats.mean >= 0.1f || stats.standardDeviation >= 0.2f
		} else {
			stats.mean >= 0.3f || new.accel.len() - stats.mean >= 0.6f
		}
		return moving
	}

	companion object {
		val REST_INTERVAL = 100.milliseconds
	}
}
