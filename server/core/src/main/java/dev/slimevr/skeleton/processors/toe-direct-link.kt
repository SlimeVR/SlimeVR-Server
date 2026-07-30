package dev.slimevr.skeleton.processors

import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles rotations of inactive toe bones.
 */
class ToeDirectLinkProcessor : SkeletonProcessor {
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
		BodyPart.LEFT_PINKY_TOE to BodyPart.LEFT_RING_TOE,

		BodyPart.RIGHT_BIG_TOE to BodyPart.RIGHT_FOOT,
		BodyPart.RIGHT_INDEX_TOE to BodyPart.RIGHT_BIG_TOE,
		BodyPart.RIGHT_MIDDLE_TOE to BodyPart.RIGHT_INDEX_TOE,
		BodyPart.RIGHT_RING_TOE to BodyPart.RIGHT_MIDDLE_TOE,
		BodyPart.RIGHT_PINKY_TOE to BodyPart.RIGHT_RING_TOE,
	)

	override fun process(state: SkeletonState): SkeletonState {
		val updatedBoneInputs = BodyPartMap(state.boneInputs)

		for ((bodyPart, source) in toesToSource) {
			val bone = updatedBoneInputs.getValue(bodyPart)
			if (bone.isActive) continue

			val sourceBone = updatedBoneInputs[source]
			updatedBoneInputs[bodyPart] = bone.copy(rawRotation = sourceBone?.rawRotation ?: bone.rawRotation)
		}

		return state.copy(boneInputs = updatedBoneInputs)
	}
}
