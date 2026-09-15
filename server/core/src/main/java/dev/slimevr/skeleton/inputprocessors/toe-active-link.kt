package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles setting the rotation of an inactive toe bone with the first active toe bone in its sources, or keeps
 * the old rotation if none of them are active.
 */
class ToeActiveLinkInputProcessor : SkeletonInputProcessor {
	/**
	 * First element is the BodyPart whose BoneInput is not actively receiving data.
	 *
	 * Second element contains a set of BodyParts whose rotation should be used as a fallback prioritized from first to last.
	 * First active bone will be prioritized. If none are active, the last bone is used.
	 */
	private val toeToSources = arrayOf(
		BodyPart.LEFT_BIG_TOE to arrayOf(BodyPart.LEFT_INDEX_TOE, BodyPart.LEFT_MIDDLE_TOE, BodyPart.LEFT_RING_TOE, BodyPart.LEFT_LITTLE_TOE, BodyPart.LEFT_FOOT),
		BodyPart.LEFT_INDEX_TOE to arrayOf(BodyPart.LEFT_MIDDLE_TOE, BodyPart.LEFT_RING_TOE, BodyPart.LEFT_LITTLE_TOE, BodyPart.LEFT_BIG_TOE, BodyPart.LEFT_FOOT),
		BodyPart.LEFT_MIDDLE_TOE to arrayOf(BodyPart.LEFT_INDEX_TOE, BodyPart.LEFT_RING_TOE, BodyPart.LEFT_LITTLE_TOE, BodyPart.LEFT_BIG_TOE, BodyPart.LEFT_FOOT),
		BodyPart.LEFT_RING_TOE to arrayOf(BodyPart.LEFT_MIDDLE_TOE, BodyPart.LEFT_INDEX_TOE, BodyPart.LEFT_LITTLE_TOE, BodyPart.LEFT_BIG_TOE, BodyPart.LEFT_FOOT),
		BodyPart.LEFT_LITTLE_TOE to arrayOf(BodyPart.LEFT_RING_TOE, BodyPart.LEFT_MIDDLE_TOE, BodyPart.LEFT_INDEX_TOE, BodyPart.LEFT_BIG_TOE, BodyPart.LEFT_FOOT),

		BodyPart.RIGHT_BIG_TOE to arrayOf(BodyPart.RIGHT_INDEX_TOE, BodyPart.RIGHT_MIDDLE_TOE, BodyPart.RIGHT_RING_TOE, BodyPart.RIGHT_LITTLE_TOE, BodyPart.RIGHT_FOOT),
		BodyPart.RIGHT_INDEX_TOE to arrayOf(BodyPart.RIGHT_MIDDLE_TOE, BodyPart.RIGHT_RING_TOE, BodyPart.RIGHT_LITTLE_TOE, BodyPart.RIGHT_BIG_TOE, BodyPart.RIGHT_FOOT),
		BodyPart.RIGHT_MIDDLE_TOE to arrayOf(BodyPart.RIGHT_INDEX_TOE, BodyPart.RIGHT_RING_TOE, BodyPart.RIGHT_LITTLE_TOE, BodyPart.RIGHT_BIG_TOE, BodyPart.RIGHT_FOOT),
		BodyPart.RIGHT_RING_TOE to arrayOf(BodyPart.RIGHT_MIDDLE_TOE, BodyPart.RIGHT_INDEX_TOE, BodyPart.RIGHT_LITTLE_TOE, BodyPart.RIGHT_BIG_TOE, BodyPart.RIGHT_FOOT),
		BodyPart.RIGHT_LITTLE_TOE to arrayOf(BodyPart.RIGHT_RING_TOE, BodyPart.RIGHT_MIDDLE_TOE, BodyPart.RIGHT_INDEX_TOE, BodyPart.RIGHT_BIG_TOE, BodyPart.RIGHT_FOOT),
	)

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for ((bodyPart, sources) in toeToSources) {
			val bone = mutableInputSkeleton[bodyPart] ?: continue
			if (bone.isRotationActive) continue

			val closestActiveBoneOrLast = sources.firstNotNullOfOrNull { part ->
				mutableInputSkeleton[part]?.takeIf { it.isRotationActive }
			} ?: mutableInputSkeleton[sources.last()] ?: continue

			mutableInputSkeleton[bodyPart] = bone.copy(rotation = closestActiveBoneOrLast.rotation)
		}
	}
}
