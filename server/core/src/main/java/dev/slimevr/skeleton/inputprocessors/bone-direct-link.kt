package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles setting the rotation of an inactive bone with its source bone.
 */
class BoneDirectLinkInputProcessor : SkeletonInputProcessor {
	/**
	 * First element is the linked BodyPart.
	 *
	 * Second element is the BodyPart the first element is linked to.
	 */
	private val linkedToSource = arrayOf(
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

	override fun process(inputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for ((bodyPart, source) in linkedToSource) {
			val bone = inputSkeleton[bodyPart] ?: continue
			if (bone.isRotationActive) continue

			val rotation = inputSkeleton[source]?.rotation ?: continue
			// Writing back the rotation the bone already carries would only allocate a new BoneInput
			if (rotation == bone.rotation) continue

			inputSkeleton[bodyPart] = bone.copy(rotation = rotation)
		}
	}
}
