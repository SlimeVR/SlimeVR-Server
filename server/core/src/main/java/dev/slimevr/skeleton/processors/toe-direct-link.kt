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
		BodyPart.LEFT_ABDUCTOR_HALLUCIS to BodyPart.LEFT_FOOT,
		BodyPart.LEFT_FLEXOR_DIGITORUM_BREVIS to BodyPart.LEFT_ABDUCTOR_HALLUCIS,
		BodyPart.LEFT_ABDUCTOR_DIGITI_MINIMI to BodyPart.LEFT_FLEXOR_DIGITORUM_BREVIS,
		BodyPart.RIGHT_ABDUCTOR_HALLUCIS to BodyPart.RIGHT_FOOT,
		BodyPart.RIGHT_FLEXOR_DIGITORUM_BREVIS to BodyPart.RIGHT_ABDUCTOR_HALLUCIS,
		BodyPart.RIGHT_ABDUCTOR_DIGITI_MINIMI to BodyPart.RIGHT_FLEXOR_DIGITORUM_BREVIS,
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
