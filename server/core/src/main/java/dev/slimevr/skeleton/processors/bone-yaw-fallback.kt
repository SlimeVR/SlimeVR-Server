package dev.slimevr.skeleton.processors

import dev.slimevr.skeleton.BODY_PART_HIERARCHY_MAP
import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import dev.slimevr.skeleton.mutate
import io.github.axisangles.ktmath.Vector3
import kotlin.collections.set
import kotlin.time.Duration

/**
 * Handles replacing rotations of boneInputs that are not actively receiving data by
 * falling back to their parent's yaw.
 */
class BoneYawFallbackProcessor : SkeletonProcessor {
	override fun process(state: SkeletonState): SkeletonState {
		val boneInputs = state.boneInputs

		val updatedFallbackBones = boneInputs.mutate { updated ->
			for ((parentPart, childParts) in BODY_PART_HIERARCHY_MAP) {
				val parentBone = boneInputs.getValue(parentPart)
				if (!parentBone.isActive) continue // Parent needs to be active

				for (childPart in childParts) {
					val childBone = boneInputs.getValue(childPart)
					if (childBone.isActive) continue // Child needs to be inactive

					updated[childPart] = childBone.copy(rawRotation = parentBone.rawRotation.project(Vector3.POS_Y).unit())
				}
			}
		}

		return state.copy(boneInputs = updatedFallbackBones)
	}
}
