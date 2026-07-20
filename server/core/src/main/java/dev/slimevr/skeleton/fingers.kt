package dev.slimevr.skeleton

import solarxr_protocol.datatypes.BodyPart
import kotlin.collections.get

/**
 * Handles the finger tracking logic.
 */
class ImputeFingersProcessor : SkeletonProcessor {
	/**
	 * First element is the linked BodyPart.
	 *
	 * Second element is the BodyPart the first element is linked to.
	 */
	private val fingerToSource = mapOf(
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
	override fun process(state: SkeletonState): SkeletonState {
		val updatedBoneInputs = state.boneInputs.toMutableMap()

		for ((bodyPart, bone) in state.boneInputs) {
			if (bone.isActive || bodyPart !in fingerToSource) continue

			val sourceBone = updatedBoneInputs[fingerToSource[bodyPart]]
			updatedBoneInputs[bodyPart] = bone.copy(rawRotation = sourceBone?.rawRotation ?: bone.rawRotation)
		}

		return state.copy(boneInputs = updatedBoneInputs)
	}
}
