package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.bones.BoneId
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor

/**
 * Copies another bone's rotation onto a bone that has no live tracker of its own. [schedule] pairs
 * a bone with its source, in ancestor-before-descendant order, so a source that itself needed
 * copying this pass is already resolved by the time it's read.
 */
class CopyRotationFallbackInputProcessor(
	private val schedule: List<Pair<BoneId, BoneId>>,
) : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for ((boneId, source) in schedule) {
			val bone = mutableInputSkeleton[boneId] ?: continue
			if (bone.isRotationActive) continue
			val rotation = mutableInputSkeleton[source]?.rotation ?: continue
			mutableInputSkeleton[boneId] = bone.copy(rotation = rotation)
		}
	}
}
