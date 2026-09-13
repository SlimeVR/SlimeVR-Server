package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.bones.boneId
import dev.slimevr.config.Settings
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

/**
 * Moves the head input to the skeleton height. Else, it would default to (0, 0, 0).
 */
class HeadPositionFallbackProcessor(val settings: Settings) : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val headBone = mutableInputSkeleton[BodyPart.HEAD.boneId] ?: return
		if (headBone.position != null) return

		// Set the head position to the be standing up at the origin
		mutableInputSkeleton[BodyPart.HEAD.boneId] = headBone.copy(position = Vector3(0f, skeletonHeight, 0f))
	}
}
