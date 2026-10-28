package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.ResettableSkeletonProcessor
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.rpc.ResetType
import kotlin.math.log
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration

private data class BoneSample(
	val poseTime: ComparableTimeMark,
	val rotation: Quaternion,
	val acceleration: Vector3,
	val position: Vector3?,
)

private data class BoneSamples(
	val fromSample: BoneSample?,
	val runningSample: BoneSample?,
	val targetSample: BoneSample?,
	val timeBetweenPoses: Duration?,
	val timeLeftForLastPose: Duration = Duration.ZERO,
)

// Smoothing
private const val TIME_CARRYOVER_DECAY = 0.9 // Carries over 90% of the last pose's remaining time

// Prediction
private const val EXTRAPOLATION_LOG_BASE = 5.0 // Higher = less extrapolation
private const val EXTRAPOLATION_MAX = 1.0 // Allows 5 seconds

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
			val receivedNewPose = bone.poseTime != oldSamples?.targetSample?.poseTime
			val samples = if (receivedNewPose) {
				// Use the runningSample as starting point if any to resume where we were at last frame
				val fromSample = oldSamples?.runningSample ?: oldSamples?.targetSample
				// Need to store this data in time because we mutate the skeleton bones
				val targetSample = BoneSample(bone.poseTime, bone.rotation, bone.acceleration, bone.position)
				// The time we'll take to interpolate to the new pose
				val timeBetweenPoses = oldSamples?.targetSample?.poseTime?.let { targetPoseTime ->
					val timeBetweenLastPoses = bone.poseTime - targetPoseTime
					oldSamples.runningSample?.poseTime?.let { runningPoseTime ->
						// Add some of last pose's remaining time
						val lastPoseCarryoverTime = oldSamples.timeLeftForLastPose * TIME_CARRYOVER_DECAY
						val timeSinceRunningPose = now - runningPoseTime
						(timeBetweenLastPoses + lastPoseCarryoverTime - timeSinceRunningPose).coerceAtLeast(Duration.ZERO)
					} ?: timeBetweenLastPoses
				}

				BoneSamples(fromSample, null, targetSample, timeBetweenPoses)
			} else {
				oldSamples
			}

			val fromSample = samples.fromSample
			val targetSample = samples.targetSample
			val timeBetweenPoses = samples.timeBetweenPoses
			if (fromSample != null && targetSample != null && timeBetweenPoses != null) {
				// Time between now and tracker data.
				val timeSinceLastPose = now - bone.poseTime
				// Ratio of how close we should be to the next pose.
				//  0 = we just got started, 1 = we should have the next one, >1 = we didn't get it yet, packet loss?
				val rawRatio = timeSinceLastPose / timeBetweenPoses
				// In case the rawRatio is >1, we extrapolate using log to keep some smooth perceived movement.
				val extrapolationAmount = log((rawRatio).coerceAtLeast(1.0), EXTRAPOLATION_LOG_BASE).coerceAtMost(EXTRAPOLATION_MAX)
				// Resulting ratio to use for interpolation, with extrapolation if any.
				val interpolationRatio = (rawRatio.coerceAtMost(1.0) + extrapolationAmount).toFloat()

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

				// Update bone data in skeleton
				val newBone = bone.copy(rotation = rotation, acceleration = acceleration, position = position)
				mutableInputSkeleton[bodyPart] = newBone

				// Update running sample so that we resume from where we left off if we get a new pose next frame
				boneSamples[bodyPart] = samples.copy(
					runningSample = BoneSample(now, newBone.rotation, newBone.acceleration, newBone.position),
					// Time left for this pose, using interpolationRatio because extrapolation isn't linear
					timeLeftForLastPose = timeBetweenPoses - (timeBetweenPoses * interpolationRatio.toDouble()),
				)
			} else if (receivedNewPose) {
				boneSamples[bodyPart] = samples
			}
		}
	}

	override fun reset(resetType: ResetType) {
		boneSamples.clear()
	}
}
