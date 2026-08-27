package dev.slimevr.skeleton.processors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.mutate
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles rotations of inactive finger bones.
 */
class FingerImputeProcessor : SkeletonProcessor {
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
	override fun process(inputSkeleton: InputSkeleton): InputSkeleton = inputSkeleton.mutate { updated ->
		for ((bodyPart, source) in fingerToSource) {
			val bone = updated.getValue(bodyPart)
			if (bone.isActive) continue

			val sourceBone = updated[source]
			updated[bodyPart] = bone.copy(rawRotation = sourceBone?.rawRotation ?: bone.rawRotation)
		}
	}
}
