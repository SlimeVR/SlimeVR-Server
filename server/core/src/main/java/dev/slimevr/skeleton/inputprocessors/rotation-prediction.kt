package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.ResettableSkeletonProcessor
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.FilteringType
import solarxr_protocol.rpc.ResetType
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration.Companion.milliseconds

/** How far ahead the prediction reaches at amount 1, scaled down by the configured amount */
val PREDICTION_LEAD = 12.milliseconds

/**
 * Tries to predict future rotations of bones.
 */
class RotationPredictionInputProcessor(val settings: Settings) :
	SkeletonInputProcessor,
	ResettableSkeletonProcessor {
	private data class BoneDelta(
		val lastRotation: Quaternion,
		val rotationDelta: Quaternion,
		val lastChange: ComparableTimeMark,
	)

	// Used to minimize latency even more for arm tracking where it is more noticeable in VR.
	private val PART_MULTIPLIER = BodyPartMap(
		mapOf(
			BodyPart.LEFT_SHOULDER to 1.4f,
			BodyPart.RIGHT_SHOULDER to 1.4f,
			BodyPart.LEFT_UPPER_ARM to 1.4f,
			BodyPart.RIGHT_UPPER_ARM to 1.4f,
			BodyPart.LEFT_LOWER_ARM to 1.4f,
			BodyPart.RIGHT_LOWER_ARM to 1.4f,
		),
	)

	private val deltas: BodyPartMap<BoneDelta> = bodyPartMap()

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val config = settings.context.state.value.data.skeletonConfig.filtering
		val filteringAmount = config.amount
		if (config.type != FilteringType.PREDICTION || filteringAmount <= 0f) {
			// Drop stale velocities so re-enabling doesn't diff against a long outdated pose
			if (deltas.isNotEmpty()) deltas.clear()
			return
		}
		val now = timeSource.markNow()

		for ((bodyPart, bone) in mutableInputSkeleton) {
			if (!bone.isRotationActive) continue

			val prev = deltas[bodyPart]
			if (prev == null) {
				deltas[bodyPart] = BoneDelta(bone.rotation, Quaternion.IDENTITY, now)
				continue
			}

			val leadScale = (PREDICTION_LEAD / (now - prev.lastChange)).toFloat()
			val rotationDelta = (bone.rotation / prev.lastRotation).pow(leadScale)
			deltas[bodyPart] = BoneDelta(bone.rotation, rotationDelta, now)

			val bonePredictionAmount = filteringAmount * (PART_MULTIPLIER[bodyPart] ?: 1f)
			val scaledDelta = rotationDelta.pow(bonePredictionAmount)
			val predicted = scaledDelta * bone.rotation
			if (predicted != bone.rotation) mutableInputSkeleton[bodyPart] = bone.copy(rotation = predicted)
		}
	}

	override fun reset(resetType: ResetType) {
		deltas.clear()
	}
}
