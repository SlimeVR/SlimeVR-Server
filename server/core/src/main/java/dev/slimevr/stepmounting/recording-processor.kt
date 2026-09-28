package dev.slimevr.stepmounting

import dev.slimevr.tracker.HeadingAlignment
import dev.slimevr.util.inFloatingSeconds
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.atan2
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration

fun accumSample(
	accum: AccelerationIntegrator,
	sample: RecordingSample,
	lastSampleTime: ComparableTimeMark? = null,
	accelBias: Vector3 = Vector3.ZERO,
): Duration {
	val delta = lastSampleTime?.let { sample.time - it } ?: Duration.ZERO
	accum.integrate(sample.accel - accelBias, delta.inFloatingSeconds)

	return delta
}

fun processTimeline(
	accum: AccelerationIntegrator,
	recording: Iterable<RecordingSample>,
	lastSampleTime: ComparableTimeMark? = null,
	accelBias: Vector3 = Vector3.ZERO,
): ComparableTimeMark? {
	var lastTime = lastSampleTime

	for (sample in recording) {
		accumSample(accum, sample, lastTime, accelBias)
		lastTime = sample.time
	}

	return lastTime
}

fun angle(vector: Vector3): Quaternion {
	val yaw = atan2(vector.x, vector.z)
	return Quaternion.rotationAroundYAxis(yaw)
}

data class StepMountingResult(
	val headingAlignment: HeadingAlignment,
	val errorMeters: Float,
	val trackerOffset: Vector3,
	val accelerationBias: Vector3,
)

fun estimateHeadingAlign(
	recording: Iterable<RecordingSample>,
	headOffset: Vector3,
): StepMountingResult {
	// Compute the unbiased final velocity
	val calibAccum = AccelerationIntegrator()
	processTimeline(calibAccum, recording)

	// Assume the final velocity is zero (at rest), we can divide our unbiased final
	//  velocity (m/s) by the duration and get a static acceleration offset (m/s^2)
	val duration = recording.last().time - recording.first().time
	val bias = calibAccum.velocity / duration.inFloatingSeconds

	// Compute the biased final offset
	val finalAccum = AccelerationIntegrator()
	processTimeline(finalAccum, recording, accelBias = bias)

	// Compute the final offsets
	val trackerOffset = finalAccum.offset
	val trackerXZ = Vector3(trackerOffset.x, 0f, trackerOffset.z)
	val hmdXZ = Vector3(headOffset.x, 0f, headOffset.z)

	// Compute mounting to fix the yaw offset from tracker to HMD
	// OLD: angle(trackerXZ.unit()) * angle(hmdXZ.unit()).inv()
	return StepMountingResult(
		Quaternion.fromTo(trackerXZ.unit(), hmdXZ.unit()),
		trackerXZ.len() - hmdXZ.len(),
		trackerOffset,
		bias,
	)
}
