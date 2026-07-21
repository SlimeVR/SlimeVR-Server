package dev.slimevr.skeleton.processors

import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import solarxr_protocol.datatypes.BodyPart

class SpineLinkProcessor : SkeletonProcessor {
    /**
     * First element is the BodyPart whose rawBone is not actively receiving data.
     * Second element contains a set of BodyParts whose rotation should be used as a fallback prioritized from first to last.
     */
    private val missingToFallbacks = mapOf(
        BodyPart.UPPER_CHEST to setOf(BodyPart.CHEST, BodyPart.WAIST, BodyPart.HIP),
        BodyPart.CHEST to setOf(BodyPart.UPPER_CHEST, BodyPart.WAIST, BodyPart.HIP),
        BodyPart.WAIST to setOf(BodyPart.CHEST, BodyPart.HIP, BodyPart.UPPER_CHEST),
        BodyPart.HIP to setOf(BodyPart.WAIST, BodyPart.CHEST, BodyPart.UPPER_CHEST),
    )

    override fun process(state: SkeletonState): SkeletonState {
        val boneInputs = state.boneInputs

        return state.copy(
            boneInputs = boneInputs.mapValues { (bodyPart, bone) ->
                if (bone.isActive) return@mapValues bone

                val closestActiveBone = missingToFallbacks[bodyPart]
                    ?.firstNotNullOfOrNull { part ->
                        boneInputs[part]?.takeIf { it.isActive }
                    }
                bone.copy(
                    rawRotation = closestActiveBone?.rawRotation ?: bone.rawRotation,
                )
            },
        )
    }
}
