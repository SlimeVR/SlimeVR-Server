package dev.slimevr.skeleton.processors

import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles setting the rotation of an inactive bone with the first active bone in its sources, or keeps
 * the old rotation if none of them are active.
 */
class BonePriorityLinkProcessor : SkeletonProcessor {
	/**
	 * First element is the BodyPart whose rawBone is not actively receiving data.
	 * Second element contains a set of BodyParts whose rotation should be used as a fallback prioritized from first to last.
	 */
	private val linkedToSources = mapOf(
		BodyPart.UPPER_CHEST to setOf(BodyPart.CHEST, BodyPart.WAIST, BodyPart.HIP),
		BodyPart.CHEST to setOf(BodyPart.UPPER_CHEST, BodyPart.WAIST, BodyPart.HIP),
		BodyPart.WAIST to setOf(BodyPart.CHEST, BodyPart.HIP, BodyPart.UPPER_CHEST),
		BodyPart.HIP to setOf(BodyPart.WAIST, BodyPart.CHEST, BodyPart.UPPER_CHEST),
	)

	override fun process(state: SkeletonState): SkeletonState {
		val boneInputs = state.boneInputs

		return state.copy(
			boneInputs = boneInputs.mapValues { (bodyPart, bone) ->
				if (bone.isActive) return@mapValues bone

				val closestActiveBone = linkedToSources[bodyPart]
					?.firstNotNullOfOrNull { part ->
						boneInputs[part]?.takeIf { it.isActive }
					}
				bone.copy(
					rawRotation = closestActiveBone?.rawRotation ?: bone.rawRotation,
				)
			},
		)
	}
}
