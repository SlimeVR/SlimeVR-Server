package dev.slimevr.skeleton.processors

import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import dev.slimevr.skeleton.findFirstParent
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.collections.get

/**
 * Handles replacing rotations of boneInputs that are not actively receiving data by either
 * falling back to their parent's yaw or the identity rotation.
 */
class BoneFallbackProcessor : SkeletonProcessor {
    override fun process(state: SkeletonState): SkeletonState {
        val boneInputs = state.boneInputs

        return state.copy(
            boneInputs = boneInputs.mapValues { (bodyPart, bone) ->
                if (bone.isActive) return@mapValues bone

                val firstActiveParentBone = boneInputs[bodyPart.findFirstParent { boneInputs[it]?.isActive == true }]
                val fallbackRotation = firstActiveParentBone?.rawRotation?.project(Vector3.POS_Y)?.unit() ?: Quaternion.IDENTITY
                bone.copy(rawRotation = fallbackRotation)
            },
        )
    }
}
