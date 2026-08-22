package dev.slimevr.skeleton.processors

import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
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
	override fun process(state: SkeletonState): SkeletonState {
		val boneInputs = state.boneInputs

		val updatedFallbackBones = boneInputs.mutate { updated ->
			boneInputs.forEachBone { parentPart, parentBone ->
				if (!parentBone.isActive) return@forEachBone // Parent needs to be active
				val children = iterateBodyPartHierarchy(parentPart, true)

				for (childPart in children) {
					val childBone = boneInputs.getValue(childPart.second)
					if (childBone.isActive) continue // Child needs to be inactive

					updated[childPart.second] = childBone.copy(rawRotation = parentBone.rawRotation.project(Vector3.POS_Y).unit())
				}
			}
		}

		return state.copy(boneInputs = updatedFallbackBones)
	}
}
