package dev.slimevr.skeleton.processors

import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import solarxr_protocol.datatypes.BodyPart
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.get
import kotlin.collections.iterator

/**
 * Handles setting the rotation of an inactive bone with its source bone.
 */
class BoneDirectLinkProcessor : SkeletonProcessor {
	/**
	 * First element is the linked BodyPart.
	 *
	 * Second element is the BodyPart the first element is linked to.
	 */
	private val linkedToSource = mapOf(
		BodyPart.HEAD to BodyPart.NECK,
		BodyPart.NECK to BodyPart.HEAD,

		BodyPart.LEFT_HIP to BodyPart.HIP,
		BodyPart.RIGHT_HIP to BodyPart.HIP,

		BodyPart.LEFT_FOOT to BodyPart.LEFT_LOWER_LEG,
		BodyPart.RIGHT_FOOT to BodyPart.RIGHT_LOWER_LEG,

		BodyPart.LEFT_SHOULDER to BodyPart.UPPER_CHEST,
		BodyPart.RIGHT_SHOULDER to BodyPart.UPPER_CHEST,

		BodyPart.LEFT_UPPER_ARM to BodyPart.LEFT_SHOULDER,
		BodyPart.RIGHT_UPPER_ARM to BodyPart.RIGHT_SHOULDER,

		BodyPart.LEFT_LOWER_ARM to BodyPart.LEFT_UPPER_ARM,
		BodyPart.RIGHT_LOWER_ARM to BodyPart.RIGHT_UPPER_ARM,

		BodyPart.LEFT_HAND to BodyPart.LEFT_LOWER_ARM,
		BodyPart.RIGHT_HAND to BodyPart.RIGHT_LOWER_ARM,
	)

	override fun process(state: SkeletonState): SkeletonState {
		val updatedBoneInputs = state.boneInputs.toMutableMap()

		for ((bodyPart, bone) in state.boneInputs) {
			if (bone.isActive || bodyPart !in linkedToSource) continue

			val sourceBone = updatedBoneInputs[linkedToSource[bodyPart]]
			updatedBoneInputs[bodyPart] = bone.copy(rawRotation = sourceBone?.rawRotation ?: bone.rawRotation)
		}

		return state.copy(boneInputs = updatedBoneInputs)
	}
}
