package dev.slimevr.skeleton.processors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.mapValues
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.FilteringType

/**
 * Tries to predict future rotations of bones.
 */
class BonePredictionProcessor(val settings: Settings) : SkeletonProcessor {
	private data class BoneVelocity(
		val lastRotation: Quaternion,
		val rotationDelta: Quaternion,
		val lastOffset: Vector3,
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

	override fun process(inputSkeleton: InputSkeleton): InputSkeleton {
		val config = settings.context.state.value.data.skeletonConfig.filtering
		if (config.type != FilteringType.PREDICTION) {
			// Drop stale velocities so re-enabling doesn't diff against a long outdated pose
			if (velocities.isNotEmpty()) velocities.clear()
			return inputSkeleton
		}
		val predictionAmount = config.amount

		val newVelocities = bodyPartMap<BoneVelocity>()
		val newBones = inputSkeleton.mapValues { bodyPart, bone ->
			val prev = velocities[bodyPart]
			if (prev == null) {
				newVelocities[bodyPart] = BoneVelocity(bone.rawRotation, Quaternion.IDENTITY, bone.offset)
				return@mapValues bone
			}

			val bonePredictionAmount = predictionAmount * getMultiplier(bodyPart)

			val rotationDelta = if (bone.rawRotation !== prev.lastRotation) {
				bone.rawRotation * prev.lastRotation.inv()
			} else {
				prev.rotationDelta
			}
			// Offsets only move on a proportion change, so the delta must not carry across frames
			val lengthDelta = if (bone.offset != prev.lastOffset) {
				bone.offset - prev.lastOffset
			} else {
				Vector3.NULL
			}
			newVelocities[bodyPart] = BoneVelocity(bone.rawRotation, rotationDelta, bone.offset)
			val scaledDelta = Quaternion.IDENTITY.lerpR(rotationDelta, bonePredictionAmount).unit()
			bone.copy(
				rawRotation = (scaledDelta * bone.rawRotation).unit(),
				offset = bone.offset + lengthDelta * bonePredictionAmount,
			)
		}
		velocities = newVelocities
		return newBones
	}
}
