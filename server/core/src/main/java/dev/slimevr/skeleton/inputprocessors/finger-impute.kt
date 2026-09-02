package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles rotations of inactive finger bones.
 */
class FingerImputeInputProcessor : SkeletonInputProcessor {
	/**
	 * First element is the linked BodyPart.
	 *
	 * Second element is the BodyPart the first element is linked to.
	 */
	private val fingerToSource = arrayOf(
		BodyPart.LEFT_THUMB_METACARPAL to BodyPart.LEFT_HAND,
		BodyPart.LEFT_THUMB_PROXIMAL to BodyPart.LEFT_THUMB_METACARPAL,
		BodyPart.LEFT_THUMB_DISTAL to BodyPart.LEFT_THUMB_PROXIMAL,
		BodyPart.LEFT_INDEX_PROXIMAL to BodyPart.LEFT_HAND,
		BodyPart.LEFT_INDEX_INTERMEDIATE to BodyPart.LEFT_INDEX_PROXIMAL,
		BodyPart.LEFT_INDEX_DISTAL to BodyPart.LEFT_INDEX_INTERMEDIATE,
		BodyPart.LEFT_MIDDLE_PROXIMAL to BodyPart.LEFT_HAND,
		BodyPart.LEFT_MIDDLE_INTERMEDIATE to BodyPart.LEFT_MIDDLE_PROXIMAL,
		BodyPart.LEFT_MIDDLE_DISTAL to BodyPart.LEFT_MIDDLE_INTERMEDIATE,
		BodyPart.LEFT_RING_PROXIMAL to BodyPart.LEFT_HAND,
		BodyPart.LEFT_RING_INTERMEDIATE to BodyPart.LEFT_RING_PROXIMAL,
		BodyPart.LEFT_RING_DISTAL to BodyPart.LEFT_RING_INTERMEDIATE,
		BodyPart.LEFT_LITTLE_PROXIMAL to BodyPart.LEFT_HAND,
		BodyPart.LEFT_LITTLE_INTERMEDIATE to BodyPart.LEFT_LITTLE_PROXIMAL,
		BodyPart.LEFT_LITTLE_DISTAL to BodyPart.LEFT_LITTLE_INTERMEDIATE,
		BodyPart.RIGHT_THUMB_METACARPAL to BodyPart.RIGHT_HAND,
		BodyPart.RIGHT_THUMB_PROXIMAL to BodyPart.RIGHT_THUMB_METACARPAL,
		BodyPart.RIGHT_THUMB_DISTAL to BodyPart.RIGHT_THUMB_PROXIMAL,
		BodyPart.RIGHT_INDEX_PROXIMAL to BodyPart.RIGHT_HAND,
		BodyPart.RIGHT_INDEX_INTERMEDIATE to BodyPart.RIGHT_INDEX_PROXIMAL,
		BodyPart.RIGHT_INDEX_DISTAL to BodyPart.RIGHT_INDEX_INTERMEDIATE,
		BodyPart.RIGHT_MIDDLE_PROXIMAL to BodyPart.RIGHT_HAND,
		BodyPart.RIGHT_MIDDLE_INTERMEDIATE to BodyPart.RIGHT_MIDDLE_PROXIMAL,
		BodyPart.RIGHT_MIDDLE_DISTAL to BodyPart.RIGHT_MIDDLE_INTERMEDIATE,
		BodyPart.RIGHT_RING_PROXIMAL to BodyPart.RIGHT_HAND,
		BodyPart.RIGHT_RING_INTERMEDIATE to BodyPart.RIGHT_RING_PROXIMAL,
		BodyPart.RIGHT_RING_DISTAL to BodyPart.RIGHT_RING_INTERMEDIATE,
		BodyPart.RIGHT_LITTLE_PROXIMAL to BodyPart.RIGHT_HAND,
		BodyPart.RIGHT_LITTLE_INTERMEDIATE to BodyPart.RIGHT_LITTLE_PROXIMAL,
		BodyPart.RIGHT_LITTLE_DISTAL to BodyPart.RIGHT_LITTLE_INTERMEDIATE,
	)

	// TODO : There's more math to do here. Reference the original code.
	override fun process(inputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for ((bodyPart, source) in fingerToSource) {
			val bone = inputSkeleton[bodyPart] ?: continue
			if (bone.isRotationActive) continue

			val rotation = inputSkeleton[source]?.rotation ?: continue
			// Writing back the rotation the bone already carries would only allocate a new BoneInput
			if (rotation == bone.rotation) continue

			inputSkeleton[bodyPart] = bone.copy(rotation = rotation)
		}
	}
}
