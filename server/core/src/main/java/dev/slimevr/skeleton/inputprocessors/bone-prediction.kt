package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.mapValues
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.FilteringType

/**
 * Tries to predict future rotations of bones.
 */
class BonePredictionInputProcessor(val settings: Settings) : SkeletonInputProcessor {
	private data class BoneVelocity(
		val lastRotation: Quaternion,
		val rotationDelta: Quaternion,
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

	override fun process(inputSkeleton: InputSkeleton, skeletonHeight: Float): InputSkeleton {
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
				newVelocities[bodyPart] = BoneVelocity(bone.rotation, Quaternion.IDENTITY)
				return@mapValues bone
			}

			val bonePredictionAmount = predictionAmount * getMultiplier(bodyPart)

			val rotationDelta = if (bone.rotation !== prev.lastRotation) {
				bone.rotation * prev.lastRotation.inv()
			} else {
				prev.rotationDelta
			}

			newVelocities[bodyPart] = BoneVelocity(bone.rotation, rotationDelta)
			val scaledDelta = Quaternion.IDENTITY.lerpR(rotationDelta, bonePredictionAmount).unit()
			bone.copy(rotation = (scaledDelta * bone.rotation).unit())
		}
		velocities = newVelocities
		return newBones
	}
}
