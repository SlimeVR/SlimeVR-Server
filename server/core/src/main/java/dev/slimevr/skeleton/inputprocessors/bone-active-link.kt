package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles setting the rotation of an inactive bone with the first active bone in its sources, or keeps
 * the old rotation if none of them are active.
 */
class BoneActiveLinkInputProcessor : SkeletonInputProcessor {
	/**
	 * First element is the BodyPart whose rawBone is not actively receiving data.
	 *
	 * Second element contains a set of BodyParts whose rotation should be used as a fallback prioritized from first to last.
	 * Only active bones will be used.
	 */
	private val linkedToSources = arrayOf(
		BodyPart.UPPER_CHEST to arrayOf(BodyPart.LOWER_CHEST, BodyPart.UPPER_WAIST, BodyPart.LOWER_WAIST, BodyPart.HIP),
		BodyPart.LOWER_CHEST to arrayOf(BodyPart.UPPER_CHEST, BodyPart.UPPER_WAIST, BodyPart.LOWER_WAIST, BodyPart.HIP),
		BodyPart.UPPER_WAIST to arrayOf(BodyPart.LOWER_WAIST, BodyPart.LOWER_CHEST, BodyPart.HIP, BodyPart.UPPER_CHEST),
		BodyPart.LOWER_WAIST to arrayOf(BodyPart.UPPER_WAIST, BodyPart.LOWER_CHEST, BodyPart.HIP, BodyPart.UPPER_CHEST),
		BodyPart.HIP to arrayOf(BodyPart.LOWER_WAIST, BodyPart.UPPER_WAIST, BodyPart.LOWER_CHEST, BodyPart.UPPER_CHEST),

		BodyPart.LEFT_BIG_TOE to arrayOf(BodyPart.LEFT_INDEX_TOE, BodyPart.LEFT_MIDDLE_TOE, BodyPart.LEFT_RING_TOE, BodyPart.LEFT_LITTLE_TOE, BodyPart.LEFT_FOOT, BodyPart.LEFT_LOWER_LEG),
		BodyPart.LEFT_INDEX_TOE to arrayOf(BodyPart.LEFT_MIDDLE_TOE, BodyPart.LEFT_RING_TOE, BodyPart.LEFT_LITTLE_TOE, BodyPart.LEFT_BIG_TOE, BodyPart.LEFT_FOOT, BodyPart.LEFT_LOWER_LEG),
		BodyPart.LEFT_MIDDLE_TOE to arrayOf(BodyPart.LEFT_INDEX_TOE, BodyPart.LEFT_RING_TOE, BodyPart.LEFT_LITTLE_TOE, BodyPart.LEFT_BIG_TOE, BodyPart.LEFT_FOOT, BodyPart.LEFT_LOWER_LEG),
		BodyPart.LEFT_RING_TOE to arrayOf(BodyPart.LEFT_MIDDLE_TOE, BodyPart.LEFT_INDEX_TOE, BodyPart.LEFT_LITTLE_TOE, BodyPart.LEFT_BIG_TOE, BodyPart.LEFT_FOOT, BodyPart.LEFT_LOWER_LEG),
		BodyPart.LEFT_LITTLE_TOE to arrayOf(BodyPart.LEFT_RING_TOE, BodyPart.LEFT_MIDDLE_TOE, BodyPart.LEFT_INDEX_TOE, BodyPart.LEFT_BIG_TOE, BodyPart.LEFT_FOOT, BodyPart.LEFT_LOWER_LEG),

		BodyPart.RIGHT_BIG_TOE to arrayOf(BodyPart.RIGHT_INDEX_TOE, BodyPart.RIGHT_MIDDLE_TOE, BodyPart.RIGHT_RING_TOE, BodyPart.RIGHT_LITTLE_TOE, BodyPart.RIGHT_FOOT, BodyPart.RIGHT_LOWER_LEG),
		BodyPart.RIGHT_INDEX_TOE to arrayOf(BodyPart.RIGHT_MIDDLE_TOE, BodyPart.RIGHT_RING_TOE, BodyPart.RIGHT_LITTLE_TOE, BodyPart.RIGHT_BIG_TOE, BodyPart.RIGHT_FOOT, BodyPart.RIGHT_LOWER_LEG),
		BodyPart.RIGHT_MIDDLE_TOE to arrayOf(BodyPart.RIGHT_INDEX_TOE, BodyPart.RIGHT_RING_TOE, BodyPart.RIGHT_LITTLE_TOE, BodyPart.RIGHT_BIG_TOE, BodyPart.RIGHT_FOOT, BodyPart.RIGHT_LOWER_LEG),
		BodyPart.RIGHT_RING_TOE to arrayOf(BodyPart.RIGHT_MIDDLE_TOE, BodyPart.RIGHT_INDEX_TOE, BodyPart.RIGHT_LITTLE_TOE, BodyPart.RIGHT_BIG_TOE, BodyPart.RIGHT_FOOT, BodyPart.RIGHT_LOWER_LEG),
		BodyPart.RIGHT_LITTLE_TOE to arrayOf(BodyPart.RIGHT_RING_TOE, BodyPart.RIGHT_MIDDLE_TOE, BodyPart.RIGHT_INDEX_TOE, BodyPart.RIGHT_BIG_TOE, BodyPart.RIGHT_FOOT, BodyPart.RIGHT_LOWER_LEG),
	)

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for ((bodyPart, sources) in linkedToSources) {
			val bone = mutableInputSkeleton[bodyPart] ?: continue
			if (bone.isRotationActive) continue

			// Only inactive bones are written, so one written here can never become a source below
			val closestActiveBone = sources.firstNotNullOfOrNull { part ->
				mutableInputSkeleton[part]?.takeIf { it.isRotationActive }
			} ?: continue
			if (closestActiveBone.rotation == bone.rotation) continue

			mutableInputSkeleton[bodyPart] = bone.copy(rotation = closestActiveBone.rotation)
		}
	}
}
