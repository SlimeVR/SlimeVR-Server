package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import solarxr_protocol.datatypes.BodyPart

/**
 * Linked BodyPart to the BodyPart it takes its rotation from. Order matters.
 */
val BONE_DIRECT_LINK_SOURCES = arrayOf(
	BodyPart.HEAD to BodyPart.NECK,
	BodyPart.LEFT_FOOT to BodyPart.LEFT_LOWER_LEG,
	BodyPart.RIGHT_FOOT to BodyPart.RIGHT_LOWER_LEG,
)

/**
 * Handles setting the rotation and acceleration of an inactive bone with its source bone.
 */
class DirectLinkInputProcessor : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for ((bodyPart, source) in BONE_DIRECT_LINK_SOURCES) {
			val bone = mutableInputSkeleton[bodyPart] ?: continue
			if (bone.isRotationActive && bone.isAccelerationActive) continue

			val source = mutableInputSkeleton[source]
			val rotation = if (!bone.isRotationActive) source?.rotation ?: bone.rotation else bone.rotation
			val acceleration = if (!bone.isAccelerationActive) source?.acceleration ?: bone.acceleration else bone.acceleration

			if (rotation == bone.rotation && acceleration == bone.acceleration) continue

			mutableInputSkeleton[bodyPart] = bone.copy(rotation = rotation, acceleration = acceleration)
		}
	}
}
