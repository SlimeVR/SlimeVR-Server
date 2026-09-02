package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles rotations of inactive toe bones.
 */
class ToeDirectLinkInputProcessor : SkeletonInputProcessor {
	/**
	 * First element is the linked BodyPart.
	 *
	 * Second element is the BodyPart the first element is linked to.
	 */
	private val toesToSource = arrayOf(
		BodyPart.LEFT_BIG_TOE to BodyPart.LEFT_FOOT,
		BodyPart.LEFT_INDEX_TOE to BodyPart.LEFT_BIG_TOE,
		BodyPart.LEFT_MIDDLE_TOE to BodyPart.LEFT_INDEX_TOE,
		BodyPart.LEFT_RING_TOE to BodyPart.LEFT_MIDDLE_TOE,
		BodyPart.LEFT_LITTLE_TOE to BodyPart.LEFT_RING_TOE,

		BodyPart.RIGHT_BIG_TOE to BodyPart.RIGHT_FOOT,
		BodyPart.RIGHT_INDEX_TOE to BodyPart.RIGHT_BIG_TOE,
		BodyPart.RIGHT_MIDDLE_TOE to BodyPart.RIGHT_INDEX_TOE,
		BodyPart.RIGHT_RING_TOE to BodyPart.RIGHT_MIDDLE_TOE,
		BodyPart.RIGHT_LITTLE_TOE to BodyPart.RIGHT_RING_TOE,
	)

	override fun process(inputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for ((bodyPart, source) in toesToSource) {
			val bone = inputSkeleton[bodyPart] ?: continue
			if (bone.isRotationActive) continue

			val rotation = inputSkeleton[source]?.rotation ?: continue
			// Writing back the rotation the bone already carries would only allocate a new BoneInput
			if (rotation == bone.rotation) continue

			inputSkeleton[bodyPart] = bone.copy(rotation = rotation)
		}
	}
}
