package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import solarxr_protocol.datatypes.BodyPart

/**
 * Linked BodyPart to the BodyPart it takes its rotation from.
 *
 * Order matters: an untracked arm cascades down from the upper chest in one pass.
 */
val BONE_DIRECT_LINK_SOURCES = arrayOf(
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

	BodyPart.LEFT_BIG_TOE to BodyPart.LEFT_FOOT,
	BodyPart.LEFT_INDEX_TOE to BodyPart.LEFT_BIG_TOE,
	BodyPart.LEFT_MIDDLE_TOE to BodyPart.LEFT_INDEX_TOE,
	BodyPart.LEFT_RING_TOE to BodyPart.LEFT_MIDDLE_TOE,
	BodyPart.LEFT_LITTLE_TOE to BodyPart.LEFT_RING_TOE,

	BodyPart.RIGHT_BIG_TOE to BodyPart.RIGHT_FOOT,
	BodyPart.RIGHT_INDEX_TOE to BodyPart.RIGHT_BIG_TOE,
	BodyPart.RIGHT_MIDDLE_TOE to BodyPart.RIGHT_INDEX_TOE,
	BodyPart.RIGHT_RING_TOE to BodyPart.RIGHT_MIDDLE_TOE,
	BodyPart.RIGHT_LITTLE_TOE to BodyPart.RIGHT_RING_TOE,

	BodyPart.LEFT_BUST to BodyPart.UPPER_CHEST,
	BodyPart.RIGHT_BUST to BodyPart.UPPER_CHEST,
)

/**
 * Handles setting the rotation and acceleration of an inactive bone with its source bone.
 */
class DirectLinkInputProcessor : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for ((bodyPart, source) in BONE_DIRECT_LINK_SOURCES) {
			val bone = mutableInputSkeleton[bodyPart] ?: continue
			if (bone.isRotationActive && bone.isAccelerationActive) continue

			val rotation = if (!bone.isRotationActive) mutableInputSkeleton[source]?.rotation ?: bone.rotation else bone.rotation
			val acceleration = if (!bone.isAccelerationActive) mutableInputSkeleton[source]?.acceleration ?: bone.acceleration else bone.acceleration

			if (rotation == bone.rotation && acceleration == bone.acceleration) continue

			mutableInputSkeleton[bodyPart] = bone.copy(rotation = rotation, acceleration = acceleration)
		}
	}
}

