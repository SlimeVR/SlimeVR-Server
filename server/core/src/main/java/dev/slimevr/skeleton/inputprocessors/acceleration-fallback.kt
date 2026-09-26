package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.forEachBone
import dev.slimevr.skeleton.iterateBodyPartHierarchy
import io.github.axisangles.ktmath.Vector3
import kotlin.collections.set

/**
 * Handles replacing accelerations of boneInputs that are not actively receiving any by
 * falling back to their parent's acceleration.
 */
class AccelerationFallbackInputProcessor : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		mutableInputSkeleton.forEachBone { parentPart, parentBone ->
			if (!parentBone.isAccelerationActive) return@forEachBone // Parent needs to be active

			val parentAccel = parentBone.acceleration
			for (childPart in iterateBodyPartHierarchy(parentPart, true)) {
				val childBone = mutableInputSkeleton[childPart.second] ?: continue
				if (childBone.isAccelerationActive) continue // Child needs to be inactive

				mutableInputSkeleton[childPart.second] = childBone.copy(acceleration = parentAccel)
			}
		}
	}
}
