package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.forEachBone
import dev.slimevr.skeleton.iterateBodyPartHierarchy
import solarxr_protocol.datatypes.BodyPart
import kotlin.collections.set

/**
 * Handles replacing accelerations of boneInputs that are not actively receiving any by
 * falling back to their parent's, or child's, acceleration.
 */
class AccelerationFallbackInputProcessor : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val processedParts: MutableSet<BodyPart> = mutableSetOf()
		mutableInputSkeleton.forEachBone { parentPart, parentBone ->
			if (!parentBone.isAccelerationActive) {
				if (parentPart in processedParts) return@forEachBone

				// Parent doesn't have rotation and didn't get any earlier in the chain.
				// Use this parent's children to set its own yaw.
				val childAccel = iterateBodyPartHierarchy(parentPart, true).firstOrNull { (_, childPart) ->
					mutableInputSkeleton[childPart]?.isAccelerationActive ?: false
				}?.let { (_, childPart) ->
					mutableInputSkeleton[childPart]?.acceleration ?: return@forEachBone
				} ?: return@forEachBone

				mutableInputSkeleton[parentPart] = parentBone.copy(acceleration = childAccel)
			} else {
				val parentAccel = parentBone.acceleration
				for ((_, childPart) in iterateBodyPartHierarchy(parentPart, true)) {
					val childBone = mutableInputSkeleton[childPart] ?: continue
					if (childBone.isAccelerationActive) continue // Child needs to be inactive

					processedParts.add(childPart)
					mutableInputSkeleton[childPart] = childBone.copy(acceleration = parentAccel)
				}
			}
		}
	}
}
