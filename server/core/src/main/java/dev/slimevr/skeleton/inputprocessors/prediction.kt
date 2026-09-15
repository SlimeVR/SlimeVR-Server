package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.bones.BoneId
import dev.slimevr.bones.boneId
import dev.slimevr.config.Settings
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.ResettableSkeletonProcessor
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.util.MonotonicValueTimeMark
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.FilteringType
import solarxr_protocol.rpc.ResetType
import kotlin.time.Duration.Companion.milliseconds

/** How far ahead the prediction reaches at amount 1, scaled down by the configured amount */
val PREDICTION_LEAD = 10.milliseconds

/**
 * Tries to predict future rotations of bones.
 */
class PredictionInputProcessor(val settings: Settings) :
	SkeletonInputProcessor,
	ResettableSkeletonProcessor {
	private data class BoneDelta(
		val lastRotation: Quaternion,
		val rotationDelta: Quaternion,
		val lastChange: MonotonicValueTimeMark,
	)

	private var deltas: MutableMap<BoneId, BoneDelta> = mutableMapOf()

	// Used to minimize latency even more for elbow tracking in VR where it is more noticeable.
	private fun getMultiplier(boneId: BoneId) = when (boneId) {
		BodyPart.LEFT_SHOULDER.boneId,
		BodyPart.RIGHT_SHOULDER.boneId,
		BodyPart.LEFT_UPPER_ARM.boneId,
		BodyPart.RIGHT_UPPER_ARM.boneId,
		BodyPart.LEFT_LOWER_ARM.boneId,
		BodyPart.RIGHT_LOWER_ARM.boneId,
		-> 1.4f

		else -> 1f
	}

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val config = settings.context.state.value.data.skeletonConfig.filtering
		val filteringAmount = config.amount
		if (config.type != FilteringType.PREDICTION || filteringAmount <= 0f) {
			// Drop stale velocities so re-enabling doesn't diff against a long outdated pose
			if (deltas.isNotEmpty()) deltas.clear()
			return
		}
		val now = timeSource.markNow()

		val newVelocities = mutableMapOf<BoneId, BoneDelta>()
		for ((boneId, bone) in mutableInputSkeleton) {
			if (!bone.isRotationActive) continue

			val prev = deltas[boneId]
			if (prev == null) {
				newVelocities[boneId] = BoneDelta(bone.rotation, Quaternion.IDENTITY, now)
				continue
			}

			val bonePredictionAmount = filteringAmount * getMultiplier(boneId)

			val changed = bone.rotation !== prev.lastRotation
			val rotationDelta = if (changed) {
				val leadScale = (PREDICTION_LEAD / (now - prev.lastChange)).toFloat()
				Quaternion.IDENTITY.lerpR(bone.rotation * prev.lastRotation.inv(), leadScale).unit()
			} else {
				prev.rotationDelta
			}

			newVelocities[boneId] = BoneDelta(bone.rotation, rotationDelta, if (changed) now else prev.lastChange)
			val scaledDelta = Quaternion.IDENTITY.lerpR(rotationDelta, bonePredictionAmount).unit()
			val predicted = (scaledDelta * bone.rotation).unit()
			if (predicted != bone.rotation) mutableInputSkeleton[boneId] = bone.copy(rotation = predicted)
		}
		deltas = newVelocities
	}

	override fun reset(resetType: ResetType) {
		deltas.clear()
	}
}
