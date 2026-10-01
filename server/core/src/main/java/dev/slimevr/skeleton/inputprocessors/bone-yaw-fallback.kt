package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.forEachBone
import dev.slimevr.skeleton.iterateBodyPartHierarchy
import solarxr_protocol.datatypes.BodyPart
import kotlin.collections.set

/**
 * Handles replacing rotations of boneInputs that are not actively receiving data by
 * falling back to their parent's, then children's, yaw.
 */
class BoneYawFallbackInputProcessor : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val processedParts: MutableList<BodyPart> = mutableListOf()
		mutableInputSkeleton.forEachBone { parentPart, parentBone ->
			if (!parentBone.isRotationActive) {
				if (parentPart in processedParts) return@forEachBone

				// Parent doesn't have rotation and didn't get any earlier in the chain.
				// Use this parent's children to set its own yaw.
				val childYaw = iterateBodyPartHierarchy(parentPart, true).firstOrNull { (_, childPart) ->
					mutableInputSkeleton[childPart]?.isRotationActive ?: false
				}?.let { (_, childPart) ->
					mutableInputSkeleton[childPart]?.rotation?.eulerHeading() ?: return@forEachBone
				} ?: return@forEachBone

				mutableInputSkeleton[parentPart] = parentBone.copy(rotation = childYaw)
			} else {
				// Use this parent's yaw to set the children's yaw.
				val parentYaw = parentBone.rotation.eulerHeading()
				for ((_, childPart) in iterateBodyPartHierarchy(parentPart, true)) {
					val childBone = mutableInputSkeleton[childPart] ?: continue
					if (childBone.isRotationActive) continue // Child needs to be inactive

					processedParts.add(childPart)
					mutableInputSkeleton[childPart] = childBone.copy(rotation = parentYaw)
				}
			}
		}
	}
}
