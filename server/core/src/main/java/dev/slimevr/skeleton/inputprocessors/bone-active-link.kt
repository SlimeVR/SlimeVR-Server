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
		BodyPart.UPPER_CHEST to arrayOf(BodyPart.CHEST, BodyPart.WAIST, BodyPart.HIP),
		BodyPart.CHEST to arrayOf(BodyPart.UPPER_CHEST, BodyPart.WAIST, BodyPart.HIP),
		BodyPart.WAIST to arrayOf(BodyPart.CHEST, BodyPart.HIP, BodyPart.UPPER_CHEST),
		BodyPart.HIP to arrayOf(BodyPart.WAIST, BodyPart.CHEST, BodyPart.UPPER_CHEST),
	)

	override fun process(inputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for ((bodyPart, sources) in linkedToSources) {
			val bone = inputSkeleton[bodyPart] ?: continue
			if (bone.isRotationActive) continue

			// Only inactive inputSkeleton are written, so one written here can never become a source below
			val closestActiveBone = sources.firstNotNullOfOrNull { part ->
				inputSkeleton[part]?.takeIf { it.isRotationActive }
			} ?: continue
			// Writing back the rotation the bone already carries would only allocate a new BoneInput
			if (closestActiveBone.rotation == bone.rotation) continue

			inputSkeleton[bodyPart] = bone.copy(rotation = closestActiveBone.rotation)
		}
	}
}
