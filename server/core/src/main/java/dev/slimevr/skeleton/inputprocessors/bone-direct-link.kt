package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.BoneId
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.boneId
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles setting the rotation of an inactive bone with its source bone.
 */
class BoneDirectLinkInputProcessor : SkeletonInputProcessor {
	/**
	 * First element is the linked bone.
	 *
	 * Second element is the bone the first element is linked to.
	 */
	private val linkedToSource: Array<Pair<BoneId, BoneId>> = arrayOf(
		BodyPart.HEAD to BodyPart.NECK,
		BodyPart.NECK to BodyPart.HEAD,

		BodyPart.LEFT_FOOT to BodyPart.LEFT_LOWER_LEG,
		BodyPart.RIGHT_FOOT to BodyPart.RIGHT_LOWER_LEG,

		BodyPart.LEFT_SHOULDER to BodyPart.UPPER_CHEST,
		BodyPart.RIGHT_SHOULDER to BodyPart.UPPER_CHEST,

		BodyPart.LEFT_UPPER_ARM to BodyPart.LEFT_SHOULDER,
		BodyPart.RIGHT_UPPER_ARM to BodyPart.RIGHT_SHOULDER,

		BodyPart.LEFT_LOWER_ARM to BodyPart.LEFT_UPPER_ARM,
		BodyPart.RIGHT_LOWER_ARM to BodyPart.RIGHT_UPPER_ARM,

		BodyPart.LEFT_HAND to BodyPart.LEFT_LOWER_ARM,
		BodyPart.RIGHT_HAND to BodyPart.RIGHT_LOWER_ARM,
	).map { (bodyPart, source) -> bodyPart.boneId to source.boneId }.toTypedArray()

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for ((boneId, sourceId) in linkedToSource) {
			val bone = mutableInputSkeleton[boneId] ?: continue
			if (bone.isRotationActive) continue

			val rotation = mutableInputSkeleton[sourceId]?.rotation ?: continue
			if (rotation == bone.rotation) continue

			mutableInputSkeleton[boneId] = bone.copy(rotation = rotation)
		}
	}
}
