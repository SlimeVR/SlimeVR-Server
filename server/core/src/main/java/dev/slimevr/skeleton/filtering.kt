package dev.slimevr.skeleton

import dev.slimevr.config.Settings
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.FilteringType

class PredictionProcessor(val settings: Settings) : SkeletonProcessor {
    private data class BoneVelocity(
        val lastRotation: Quaternion,
        val rotationDelta: Quaternion,
        val lastOffset: Vector3,
        val offsetDelta: Vector3,
    )

    private var velocities: Map<BodyPart, BoneVelocity> = emptyMap()

    override fun process(state: SkeletonState): SkeletonState {
        val config = settings.context.state.value.data.skeletonConfig.filtering
        if (config.type != FilteringType.PREDICTION) return state

        val newVelocities = mutableMapOf<BodyPart, BoneVelocity>()
        val newBones = state.boneInputs.mapValues { (bodyPart, bone) ->
            val prev = velocities[bodyPart]
            if (prev == null) {
                newVelocities[bodyPart] = BoneVelocity(bone.rawRotation, Quaternion.IDENTITY, bone.offset, Vector3.NULL)
                return@mapValues bone
            }
            val rotationDelta = if (bone.rawRotation !== prev.lastRotation) {
                bone.rawRotation * prev.lastRotation.inv()
            } else {
                prev.rotationDelta
            }
            val lengthDelta = if (bone.offset != prev.lastOffset) {
                bone.offset - prev.lastOffset
            } else {
                prev.offsetDelta
            }
            newVelocities[bodyPart] = BoneVelocity(bone.rawRotation, rotationDelta, bone.offset, lengthDelta)
            val scaledDelta = Quaternion.IDENTITY.lerpR(rotationDelta, config.amount).unit()
            bone.copy(
                rawRotation = (scaledDelta * bone.rawRotation).unit(),
                offset = bone.offset + lengthDelta * config.amount,
            )
        }
        velocities = newVelocities
        return state.copy(boneInputs = newBones)
    }
}

class SmoothingProcessor(val settings: Settings) : SkeletonProcessor {
    private var smoothedRotations: Map<BodyPart, Quaternion> = emptyMap()
    private var smoothedLengths: Map<BodyPart, Vector3> = emptyMap()

    // TODO this isn't linear. Do we want linear smoothing like in main?
    override fun process(state: SkeletonState): SkeletonState {
        val config = settings.context.state.value.data.skeletonConfig.filtering
        if (config.type != FilteringType.SMOOTHING) return state

        val alpha = 1 - (SMOOTH_MIN + config.amount.coerceIn(0f, 1f) * (SMOOTH_MAX - SMOOTH_MIN))

        smoothedRotations = state.boneInputs.mapValues { (bodyPart, bone) ->
            (smoothedRotations[bodyPart] ?: bone.rawRotation).lerpR(bone.rawRotation, alpha).unit()
        }
        smoothedLengths = state.boneInputs.mapValues { (bodyPart, bone) ->
            val prev = smoothedLengths[bodyPart] ?: bone.offset
            prev + (bone.offset - prev) * alpha
        }
        return state.copy(
            boneInputs = state.boneInputs.mapValues { (bodyPart, bone) ->
                bone.copy(
                    rawRotation = smoothedRotations[bodyPart] ?: bone.rawRotation,
                    offset = smoothedLengths[bodyPart] ?: bone.offset,
                )
            },
        )
    }

    companion object {
        private const val SMOOTH_MIN = 0.63f
        private const val SMOOTH_MAX = 0.94f
    }
}
