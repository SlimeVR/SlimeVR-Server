package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.mutate
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

/**
 * Moves the head input to the skeleton height. Else, it would default to (0, 0, 0).
 */
class HeadPositionFallbackProcessor(val settings: Settings) : SkeletonInputProcessor {
	override fun process(inputSkeleton: InputSkeleton, skeletonHeight: Float): InputSkeleton {
		val headBone = inputSkeleton.getValue(BodyPart.HEAD)
		if (headBone.rawPosition != null) return inputSkeleton

		// Set the head position to the be standing up at the origin
		return inputSkeleton.mutate { it[BodyPart.HEAD] = headBone.copy(rawPosition = Vector3(0f, skeletonHeight, 0f)) }
	}
}
