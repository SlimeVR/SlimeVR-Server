package dev.slimevr.skeleton.inputprocessors

import com.jme3.math.FastMath
import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.BoneInput
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
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

// Upsampling
private val UPSAMPLING_EXTRAPOLATION_MAX_DURATION = 0.8.seconds

// Upsampling (technically Prediction but always active)
private const val UPSAMPLING_EXTRAPOLATION_LOG_BASE_MIN = 7f
private const val UPSAMPLING_EXTRAPOLATION_LOG_BASE_MAX = 3f

// Upsampling (technically Smoothing but always active)
private const val UPSAMPLING_TIME_CARRYOVER_MIN = 0.6f
private const val UPSAMPLING_TIME_CARRYOVER_MAX = 0.9f

// Smoothing
private const val SMOOTHING_MS_MAX = 75.0

// Prediction
private const val PREDICTION_MS_MAX = 32.0
private const val PREVIOUS_PREDICTION_DECAY = 0.25f

private data class BoneSample(
	val rotation: Quaternion,
	val acceleration: Vector3,
	val position: Vector3?,
) {
	companion object {
		fun fromBone(bone: BoneInput) = BoneSample(bone.rotation, bone.acceleration, bone.position)
	}
}

private data class BoneSamples(
	// These get assigned everytime we get a new pose for a bone
	val fromSample: BoneSample?,
	val targetSample: BoneSample,
	val poseTime: ComparableTimeMark,
	val sampleTime: ComparableTimeMark,
	val timeBetweenPoses: Duration,
	val predictedRotation: Quaternion,
	// Mutable since these get changed every skeleton frame even if no new pose is received
	var runningSample: BoneSample?,
	var timeLeftForLastPose: Duration, // can be negative
)

private fun computePoseTimeDelta(
	previousPoseTime: ComparableTimeMark?,
	currentPoseTime: ComparableTimeMark,
) = previousPoseTime?.let {
	currentPoseTime - previousPoseTime
} ?: Duration.ZERO

private fun computeTimeBetweenPoses(
	poseTimeDelta: Duration,
	timeLeftForLastPose: Duration?,
	timeCarryover: Double,
): Duration {
	// Use some of last pose's remaining time
	val lastPoseCarryoverTime = timeLeftForLastPose?.let { it * timeCarryover } ?: Duration.ZERO
	return (lastPoseCarryoverTime + poseTimeDelta).coerceAtLeast(Duration.ZERO)
}

private fun computePredictedRotation(
	previousRotation: Quaternion?,
	previousPredictedRotation: Quaternion?,
	targetRotation: Quaternion,
	predictionTime: Duration,
	poseTimeDelta: Duration,
): Quaternion {
	if (previousRotation == null) return targetRotation

	// Use some of the previous prediction
	val previousPredictionDelta = previousPredictedRotation?.localAbout(previousRotation)?.pow(PREVIOUS_PREDICTION_DECAY)
		?: Quaternion.IDENTITY

	val currentDelta = if (poseTimeDelta > Duration.ZERO) {
		val t = (predictionTime / poseTimeDelta).toFloat()
		targetRotation.localAbout(previousRotation).pow(t)
	} else {
		Quaternion.IDENTITY
	}

	return targetRotation * previousPredictionDelta * currentDelta
}

private fun computeInterpolationRatio(
	timeSinceLastPose: Duration,
	timeBetweenPoses: Duration,
	extrapolationLogBase: Double,
): Float {
	// Time between now and tracker data.
	val timeSinceLastPoseClamped = timeSinceLastPose.coerceAtMost(timeBetweenPoses + UPSAMPLING_EXTRAPOLATION_MAX_DURATION)
	// Ratio of how close we should be to the next pose.
	//  0 = we just got started, 1 = we should have the next one, >1 = we didn't get it yet; packet lost or late
	val rawRatio = if (timeBetweenPoses > Duration.ZERO) timeSinceLastPoseClamped / timeBetweenPoses else 1.0
	// In case the rawRatio is >1, we extrapolate using log to keep some smooth perceived movement.
	val extrapolationAmount = log((rawRatio).coerceAtLeast(1.0), extrapolationLogBase)
	// Resulting ratio to use for interpolation, including extrapolation if any.
	return (rawRatio.coerceAtMost(1.0) + extrapolationAmount).toFloat()
}

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
	private var lastProcessEndTime = timeSource.markNow()

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val now = timeSource.markNow()

		val skeletonRatios = settings.context.state.value.data.skeletonConfig.ratios
		val timeCarryover = FastMath.remapClamped(skeletonRatios.smoothingStrength, 0f, 1f, UPSAMPLING_TIME_CARRYOVER_MIN, UPSAMPLING_TIME_CARRYOVER_MAX).toDouble()
		val extrapolationLogBase = FastMath.remapClamped(skeletonRatios.predictionStrength, 0f, 1f, UPSAMPLING_EXTRAPOLATION_LOG_BASE_MIN, UPSAMPLING_EXTRAPOLATION_LOG_BASE_MAX).toDouble()
		val smoothingTime = (skeletonRatios.smoothingStrength * SMOOTHING_MS_MAX).milliseconds
		val predictionTime = (skeletonRatios.predictionStrength * PREDICTION_MS_MAX).milliseconds

		for ((bodyPart, bone) in mutableInputSkeleton) {
			if (bone.poseTime == null) {
				boneSamples.remove(bodyPart)
				continue
			}

			val previousSamples = boneSamples[bodyPart]

			// New pose data updates the samples
			val samples = if (bone.poseTime != previousSamples?.poseTime) {
				// Use the runningSample as starting point if any to resume where we were at last frame
				val fromSample = previousSamples?.runningSample ?: previousSamples?.targetSample
				// Need to store this data in time because we mutate the skeleton bones
				val targetSample = BoneSample.fromBone(bone)

				// How much time has passed between the last 2 bone poses
				val poseTimeDelta = computePoseTimeDelta(previousSamples?.poseTime, bone.poseTime)

				// The time we'll take to interpolate to the new pose
				val timeBetweenPoses = computeTimeBetweenPoses(poseTimeDelta, previousSamples?.timeLeftForLastPose, timeCarryover)

				// The predicted target rotation
				val predictedRotation = if (predictionTime > Duration.ZERO) {
					// Prediction enabled
					computePredictedRotation(
						previousSamples?.targetSample?.rotation,
						previousSamples?.predictedRotation,
						targetSample.rotation,
						predictionTime,
						poseTimeDelta,
					)
				} else {
					targetSample.rotation
				}

				// New samples
				BoneSamples(fromSample, targetSample, bone.poseTime, lastProcessEndTime, timeBetweenPoses, predictedRotation, null, Duration.ZERO)
			} else {
				previousSamples
			}

			// Filtering
			if (samples.fromSample != null) {
				val timeSinceLastPose = now - samples.sampleTime
				val upsamplingRatio = computeInterpolationRatio(timeSinceLastPose, samples.timeBetweenPoses, extrapolationLogBase)

				// Rotation
				val rotation = if (bone.isRotationActive) {
					val smoothingRatio = if (smoothingTime > Duration.ZERO) {
						// Smoothing enabled
						computeInterpolationRatio(timeSinceLastPose, samples.timeBetweenPoses + smoothingTime, extrapolationLogBase)
					} else {
						upsamplingRatio
					}
					// Use the predicted rotation as target
					samples.fromSample.rotation.interpQ(samples.predictedRotation, smoothingRatio)
				} else {
					samples.targetSample.rotation
				}

				// Acceleration
				val acceleration = if (bone.isAccelerationActive) {
					samples.fromSample.acceleration.lerp(samples.targetSample.acceleration, upsamplingRatio)
				} else {
					samples.targetSample.acceleration
				}

				// Position
				val position = if (bone.isPositionActive && samples.fromSample.position != null && samples.targetSample.position != null) {
					samples.fromSample.position.lerp(samples.targetSample.position, upsamplingRatio)
				} else {
					samples.targetSample.position
				}

				// Update bone data in skeleton
				val newBone = bone.copy(rotation = rotation, acceleration = acceleration, position = position)
				mutableInputSkeleton[bodyPart] = newBone

				// Update running sample so that we resume from where we left off if we get a new pose next frame
				samples.runningSample = BoneSample.fromBone(newBone)
				// Time left for this pose, using the ratio to account for non-linear extrapolation
				samples.timeLeftForLastPose = samples.timeBetweenPoses - (samples.timeBetweenPoses * upsamplingRatio.toDouble())
			}

			// Save updated samples
			boneSamples[bodyPart] = samples
		}

		lastProcessEndTime = now
	}

	override fun reset(resetType: ResetType) {
		boneSamples.clear()
	}
}
