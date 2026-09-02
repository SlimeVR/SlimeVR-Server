package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.forEachBone
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.FilteringType
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

/** How far ahead the prediction reaches at amount 1, scaled down by the configured amount */
val PREDICTION_LEAD = 10.milliseconds

/**
 * Tries to predict future rotations of bones.
 */
class BonePredictionInputProcessor(val settings: Settings) : SkeletonInputProcessor {
	private data class BoneVelocity(
		val lastRotation: Quaternion,
		val rotationDelta: Quaternion,
		val lastChange: TimeSource.Monotonic.ValueTimeMark,
	)

	private var velocities: BodyPartMap<BoneVelocity> = bodyPartMap()

	private fun getMultiplier(bodyPart: BodyPart) = when (bodyPart) {
		BodyPart.LEFT_SHOULDER,
		BodyPart.RIGHT_SHOULDER,
		BodyPart.LEFT_UPPER_ARM,
		BodyPart.RIGHT_UPPER_ARM,
		BodyPart.LEFT_LOWER_ARM,
		BodyPart.RIGHT_LOWER_ARM,
		-> 1.5f

		else -> 1f
	}

	override fun process(inputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val config = settings.context.state.value.data.skeletonConfig.filtering
		if (config.type != FilteringType.PREDICTION) {
			// Drop stale velocities so re-enabling doesn't diff against a long outdated pose
			if (velocities.isNotEmpty()) velocities.clear()
			return
		}
		val predictionAmount = config.amount
		val now = timeSource.markNow()

		val newVelocities = bodyPartMap<BoneVelocity>()
		inputSkeleton.forEachBone { bodyPart, bone ->
			val prev = velocities[bodyPart]
			if (prev == null) {
				newVelocities[bodyPart] = BoneVelocity(bone.rotation, Quaternion.IDENTITY, now)
				return@forEachBone
			}

			val bonePredictionAmount = predictionAmount * getMultiplier(bodyPart)

			val changed = bone.rotation !== prev.lastRotation
			val rotationDelta = if (changed) {
				val leadScale = (PREDICTION_LEAD / (now - prev.lastChange)).toFloat()
				Quaternion.IDENTITY.lerpR(bone.rotation * prev.lastRotation.inv(), leadScale).unit()
			} else {
				prev.rotationDelta
			}

			newVelocities[bodyPart] = BoneVelocity(bone.rotation, rotationDelta, if (changed) now else prev.lastChange)
			val scaledDelta = Quaternion.IDENTITY.lerpR(rotationDelta, bonePredictionAmount).unit()
			val predicted = (scaledDelta * bone.rotation).unit()
			if (predicted != bone.rotation) inputSkeleton[bodyPart] = bone.copy(rotation = predicted)
		}
		velocities = newVelocities
	}
}
