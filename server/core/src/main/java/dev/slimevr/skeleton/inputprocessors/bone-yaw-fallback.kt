package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.Vector3

/**
 * Handles replacing rotations of boneInputs that are not actively receiving data by
 * falling back to their parent's yaw.
 */
class BoneYawFallbackInputProcessor : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val registry = mutableInputSkeleton.registry
		for ((parentId, parentBone) in mutableInputSkeleton) {
			if (!parentBone.isRotationActive) continue // Parent needs to be active

			val parentYaw = parentBone.rotation.project(Vector3.POS_Y).unit()
			for ((_, childId) in registry.hierarchyFrom(parentId, onlyChildren = true)) {
				val childBone = mutableInputSkeleton[childId] ?: continue
				if (childBone.isRotationActive) continue // Child needs to be inactive
				if (parentYaw == childBone.rotation) continue

				mutableInputSkeleton[childId] = childBone.copy(rotation = parentYaw)
			}
		}
	}
}
