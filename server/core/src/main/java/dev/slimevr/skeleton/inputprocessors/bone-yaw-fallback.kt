package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.forEachBone
import dev.slimevr.skeleton.iterateBodyPartHierarchy
import io.github.axisangles.ktmath.Vector3
import kotlin.collections.set

/**
 * Handles replacing rotations of boneInputs that are not actively receiving data by
 * falling back to their parent's yaw.
 */
class BoneYawFallbackInputProcessor : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		mutableInputSkeleton.forEachBone { parentPart, parentBone ->
			if (!parentBone.isRotationActive) return@forEachBone // Parent needs to be active

			val parentYaw = parentBone.rotation.project(Vector3.POS_Y).unit()
			for (childPart in iterateBodyPartHierarchy(parentPart, true)) {
				val childBone = mutableInputSkeleton[childPart.second] ?: continue
				if (childBone.isRotationActive) continue // Child needs to be inactive
				if (parentYaw == childBone.rotation) continue

				mutableInputSkeleton[childPart.second] = childBone.copy(rotation = parentYaw)
			}
		}
	}
}
