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
	override fun process(inputSkeleton: InputSkeleton, skeletonHeight: Float) {
		inputSkeleton.forEachBone { parentPart, parentBone ->
			if (!parentBone.isRotationActive) return@forEachBone // Parent needs to be active
			val children = iterateBodyPartHierarchy(parentPart, true)
			// Depends only on the parent, so every child below reuses it
			val yaw = parentBone.rotation.project(Vector3.POS_Y).unit()

			for (childPart in children) {
				val childBone = inputSkeleton[childPart.second] ?: continue
				if (childBone.isRotationActive) continue // Child needs to be inactive
				// Writing back the rotation the bone already carries would only allocate a new BoneInput
				if (yaw == childBone.rotation) continue

				inputSkeleton[childPart.second] = childBone.copy(rotation = yaw)
			}
		}
	}
}
