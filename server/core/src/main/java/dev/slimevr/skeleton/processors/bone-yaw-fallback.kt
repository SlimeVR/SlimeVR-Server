package dev.slimevr.skeleton.processors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.forEachBone
import dev.slimevr.skeleton.iterateBodyPartHierarchy
import dev.slimevr.skeleton.mutate
import io.github.axisangles.ktmath.Vector3
import kotlin.collections.set

/**
 * Handles replacing rotations of boneInputs that are not actively receiving data by
 * falling back to their parent's yaw.
 */
class BoneYawFallbackProcessor : SkeletonProcessor {
	override fun process(inputSkeleton: InputSkeleton): InputSkeleton {
		return inputSkeleton.mutate { updated ->
			inputSkeleton.forEachBone { parentPart, parentBone ->
				if (!parentBone.isActive) return@forEachBone // Parent needs to be active
				val children = iterateBodyPartHierarchy(parentPart, true)

				for (childPart in children) {
					val childBone = inputSkeleton.getValue(childPart.second)
					if (childBone.isActive) continue // Child needs to be inactive

					updated[childPart.second] = childBone.copy(rawRotation = parentBone.rawRotation.project(Vector3.POS_Y).unit())
				}
			}
		}
	}
}
