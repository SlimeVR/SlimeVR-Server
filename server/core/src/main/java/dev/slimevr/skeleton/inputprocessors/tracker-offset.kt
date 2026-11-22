package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor

/**
 * Handles tracker offset. For example, an HMD's rotation will be about 10cm in front of the head bone.
 */
class BoneOffsetInputProcessor : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for ((bodyPart, bone) in mutableInputSkeleton) {
			if (bone.boneOffset.isZero()) continue
			val localOffset = bone.rotation.sandwich(bone.boneOffset)
			mutableInputSkeleton[bodyPart] = bone.copy(position = bone.position?.plus(localOffset))
		}
	}
}
