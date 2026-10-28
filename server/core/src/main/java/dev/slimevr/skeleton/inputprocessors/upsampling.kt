package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.ResettableSkeletonProcessor
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.rpc.ResetType
import kotlin.time.ComparableTimeMark

private data class BoneSample(
	val rotation: Quaternion,
	val acceleration: Vector3,
	val position: Vector3?,
)

private data class BoneSamples(
	val fromSample: BoneSample?,
	val targetSample: BoneSample?,
	val latestPoseTime: ComparableTimeMark,
)

// Allow a little extrapolation to ease into the next pose
private const val MAX_EXTRAPOLATION = 0.25f
private const val EXTRAPOLATION_MULTIPLIER = 0.05f

/**
 * Since the skeleton runs at a different rate than trackers (e.g: 500hz vs 100hz),
 * this processor handles interpolating data received from trackers across skeleton frames up
 * until the next tracker data (e.g: interpolating the 100hz data across 5 frames to reach 500hz).
 *
 * Its goal is to make the data more reliable and easier to work with by avoiding spikes over frames as the skeleton
 * receives new data from trackers. We trade off a little bit of latency (1 tracker tick, or ~10 ms) for this.
 *
 * It will also extrapolate a little if we haven't received new tracker data in the time we were supposed to,
 * to mitigate packet loss and late packets.
 *
 * With this, processors don't have to care that trackers only send at 100hz and can know that the data from one skeleton
 * frame to another will have reliably changed according to the IRL movements.
 */
class UpsamplingInputProcessor :
	SkeletonInputProcessor,
	ResettableSkeletonProcessor {
	private val boneSamples: BodyPartMap<BoneSamples> = bodyPartMap()

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val now = timeSource.markNow()
		for ((bodyPart, bone) in mutableInputSkeleton) {
			if (bone.expectedTps == null || bone.poseTime == null) continue

			val oldSamples = boneSamples[bodyPart]
			val samples = if (bone.poseTime != oldSamples?.latestPoseTime) {
				// New pose data
				val fromSample = oldSamples?.targetSample
				val targetSample = BoneSample(bone.rotation, bone.acceleration, bone.position)
				BoneSamples(
					fromSample,
					targetSample,
					bone.poseTime,
				).also {
					boneSamples[bodyPart] = it
				}
			} else {
				oldSamples
			}

			samples.fromSample?.let { fromSample ->
				samples.targetSample?.let { targetSample ->
					val timeSinceLastPose = now - bone.poseTime
					val rawRatio = (timeSinceLastPose * bone.expectedTps.toInt()).inFloatingSeconds
					val extrapolationRatio = ((rawRatio - 1f).coerceAtLeast(0f) * EXTRAPOLATION_MULTIPLIER).coerceAtMost(MAX_EXTRAPOLATION)
					val interpolationRatio = rawRatio.coerceAtMost(1f) + extrapolationRatio

					// Upsample rotation, acceleration and position.
					val rotation = if (bone.isRotationActive) {
						fromSample.rotation.interpQ(targetSample.rotation, interpolationRatio)
					} else {
						targetSample.rotation
					}
					val acceleration = if (bone.isAccelerationActive) {
						fromSample.acceleration.lerp(targetSample.acceleration, interpolationRatio)
					} else {
						targetSample.acceleration
					}
					val position = if (bone.isPositionActive && fromSample.position != null && targetSample.position != null) {
						fromSample.position.lerp(targetSample.position, interpolationRatio)
					} else {
						targetSample.position
					}

					// Update bone data in skeleton and return the bone
					mutableInputSkeleton[bodyPart] = bone.copy(
						rotation = rotation,
						acceleration = acceleration,
						position = position,
					)
				}
			}
		}
	}

	override fun reset(resetType: ResetType) {
		boneSamples.clear()
	}
}
