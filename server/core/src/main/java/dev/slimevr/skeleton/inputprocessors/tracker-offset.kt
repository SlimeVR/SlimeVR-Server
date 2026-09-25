package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.Vector3

/**
 * Handles tracker offset. For example, an HMD's rotation will be about 10cm in front of the head bone.
 */
class TrackerOffsetInputProcessor : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		mutableInputSkeleton.forEach { (part, input) ->
			if (input.trackerOffset == Vector3.ZERO) return@forEach
			val localOffset = input.rotation.sandwich(input.trackerOffset)
			mutableInputSkeleton[part] = input.copy(position = input.position?.plus(localOffset))
		}
	}
}
