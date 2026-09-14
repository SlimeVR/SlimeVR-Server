package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.bones.BoneId
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor

/**
 * Sets an untracked bone's rotation from the first actively-tracked bone in its source list,
 * falling back to the list's last entry regardless of its activity if none are active.
 */
class FirstActiveRotationFallbackInputProcessor(
	private val schedule: List<Pair<BoneId, List<BoneId>>>,
) : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for ((boneId, sources) in schedule) {
			val bone = mutableInputSkeleton[boneId] ?: continue
			if (bone.isRotationActive) continue
			val active = sources.firstNotNullOfOrNull { mutableInputSkeleton[it]?.takeIf { b -> b.isRotationActive } }
			val rotation = (active ?: sources.lastOrNull()?.let { mutableInputSkeleton[it] })?.rotation ?: continue
			mutableInputSkeleton[boneId] = bone.copy(rotation = rotation)
		}
	}
}
