package dev.slimevr.reset.accel

import dev.slimevr.util.AccelAccumulator
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.atan2
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration
import kotlin.time.DurationUnit

object RecordingProcessor {
	fun accumSample(
		accum: AccelAccumulator,
		sample: RecordingSample,
		lastSampleTime: ComparableTimeMark? = null,
		accelBias: Vector3 = Vector3.NULL,
	): Duration {
		val delta = lastSampleTime?.let { sample.time - it } ?: Duration.ZERO
		accum.dataTick(sample.accel - accelBias, delta.toDouble(DurationUnit.SECONDS).toFloat())

		return delta
	}

	fun processTimeline(
		accum: AccelAccumulator,
		wrapper: RecordingWrapper,
		lastSampleTime: ComparableTimeMark? = null,
		accelBias: Vector3 = Vector3.NULL,
	): ComparableTimeMark? {
		var lastTime = lastSampleTime

		for (sample in wrapper.recording) {
			accumSample(accum, sample, lastTime, accelBias)
			lastTime = sample.time
		}

		return lastTime
	}

	fun angle(vector: Vector3): Quaternion {
		val yaw = atan2(vector.x, vector.z)
		return Quaternion.rotationAroundYAxis(yaw)
	}
}
