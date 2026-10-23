package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor

/**
 * Handles tracker offset. For example, an HMD's rotation will be about 10cm in front of the head bone.
 */
class TrackerOffsetInputProcessor : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for ((bodyPart, bone) in mutableInputSkeleton) {
			if (bone.trackerOffset.isZero()) continue
			val localOffset = bone.rotation.sandwich(bone.trackerOffset)
			mutableInputSkeleton[bodyPart] = bone.copy(position = bone.position?.plus(localOffset))
		}
	}
}
