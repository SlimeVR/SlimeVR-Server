package dev.slimevr.skeleton.inputprocessors

import com.jme3.math.FastMath
import dev.slimevr.config.Settings
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
	val predictionRotation: Quaternion?,
	val timeLeftForLastPose: Duration = Duration.ZERO,
)

// TODO fine tune these values
// Smoothing
private const val TIME_CARRYOVER_DECAY_MIN = 0.8f
private const val TIME_CARRYOVER_DECAY_MAX = 0.1f

// Smaller = more smoother
private const val ROTATION_SMOOTHING_MIN = 0.3f
private const val ROTATION_SMOOTHING_MAX = 0.04f

// Prediction
private const val EXTRAPOLATION_MAX_SECONDS = 4.5

// Higher = less extrapolation
private const val EXTRAPOLATION_LOG_BASE_MIN = 8.0f
private const val EXTRAPOLATION_LOG_BASE_MAX = 4.0f

// Higher = more prediction
private const val ROTATION_PREDICTION_MIN = 0f
private const val ROTATION_PREDICTION_MAX = 0.95f

// Lower = more biased towards recent rotations
private const val PREVIOUS_PREDICTION_DECAY = 0.2f

// TODO this could use 'some' (A LOT OF) refactoring
//  pls don't judge the code quality, I was just trying to make it work for now!

/**
 * This processor handles upsampling, smoothing and prediction, all in one processor for optimization's sake.
 *
 * ----- Upsampling -----
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
 *
 * ----- Smoothing -----
 * Smoothing decides how fast to get to the target rotation by just scaling down the interpolation ratio.
 *
 * ----- Prediction -----
 * Prediction decides how far away the target rotation is using an average of past rotation deltas.
 */
class FilteringInputProcessor(val settings: Settings) :
	SkeletonInputProcessor,
	ResettableSkeletonProcessor {
	private val boneSamples: BodyPartMap<BoneSamples> = bodyPartMap()

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val now = timeSource.markNow()
		val skeletonRatios = settings.context.state.value.data.skeletonConfig.ratios
		val smoothingStrength = skeletonRatios.smoothingStrength
		val predictionStrength = skeletonRatios.predictionStrength
		val timeCarryoverDecay = FastMath.remapClamped(smoothingStrength, 0f, 1f, TIME_CARRYOVER_DECAY_MIN, TIME_CARRYOVER_DECAY_MAX).toDouble()
		val rotationSmoothingMultiplier = FastMath.remapClamped(smoothingStrength, 0f, 1f, ROTATION_SMOOTHING_MIN, ROTATION_SMOOTHING_MAX)
		val extrapolationLogBase = FastMath.remapClamped(predictionStrength, 0f, 1f, EXTRAPOLATION_LOG_BASE_MIN, EXTRAPOLATION_LOG_BASE_MAX).toDouble()
		val rotationPredictionMultiplier = FastMath.remapClamped(predictionStrength, 0f, 1f, ROTATION_PREDICTION_MIN, ROTATION_PREDICTION_MAX)

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
						val lastPoseCarryoverTime = oldSamples.timeLeftForLastPose * timeCarryoverDecay
						val timeSinceRunningPose = now - runningPoseTime
						(timeBetweenLastPoses + lastPoseCarryoverTime - timeSinceRunningPose).coerceAtLeast(Duration.ZERO)
					} ?: timeBetweenLastPoses
				}
				val predictionRotation = oldSamples?.targetSample?.rotation?.let { previousRotation ->
					if (predictionStrength > 0f) {
						val previousPrediction = oldSamples.predictionRotation?.pow(PREVIOUS_PREDICTION_DECAY) ?: Quaternion.IDENTITY
						val currentDelta = (previousRotation.inv() * targetSample.rotation).pow(rotationPredictionMultiplier)
						previousPrediction * currentDelta
					} else {
						null
					}
				}

				BoneSamples(fromSample, null, targetSample, timeBetweenPoses, predictionRotation)
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
				//  0 = we just got started, 1 = we should have the next one, >1 = we didn't get it yet; packet lost or late
				val rawRatio = timeSinceLastPose / timeBetweenPoses
				// In case the rawRatio is >1, we extrapolate using log to keep some smooth perceived movement.
				val extrapolationAmount = log((rawRatio).coerceIn(1.0, EXTRAPOLATION_MAX_SECONDS), extrapolationLogBase)
				// Resulting ratio to use for interpolation, with extrapolation if any.
				val interpolationRatio = (rawRatio.coerceAtMost(1.0) + extrapolationAmount).toFloat()

				// Upsample rotation, acceleration and position.
				val rotation = if (bone.isRotationActive) {
					val rotationInterpolationRatio = if (smoothingStrength > 0f) {
						val extrapolationAmount = log((rawRatio * rotationSmoothingMultiplier).coerceIn(1.0, EXTRAPOLATION_MAX_SECONDS), extrapolationLogBase)
						((rawRatio * rotationSmoothingMultiplier).coerceAtMost(1.0) + extrapolationAmount).toFloat()
					} else {
						interpolationRatio
					}
					val targetRotation = samples.predictionRotation?.let { targetSample.rotation * it } ?: targetSample.rotation
					fromSample.rotation.interpQ(targetRotation, rotationInterpolationRatio)
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
