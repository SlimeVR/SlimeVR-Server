package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

interface SkeletonProcessor {
	var enabled: Boolean
	fun process(state: SkeletonState): SkeletonState
}

class SmoothingProcessor(var smoothing: Float) : SkeletonProcessor {
	override var enabled: Boolean = true
	private var smoothedRotations: Map<BodyPart, Quaternion> = emptyMap()
	private var smoothedLengths: Map<BodyPart, Vector3> = emptyMap()

	override fun process(state: SkeletonState): SkeletonState {
		val alpha = 1f - smoothing.coerceAtMost(0.99f)
		smoothedRotations = state.rawBones.mapValues { (bodyPart, bone) ->
			(smoothedRotations[bodyPart] ?: bone.rawRotation).lerpR(bone.rawRotation, alpha).unit()
		}
		smoothedLengths = state.rawBones.mapValues { (bodyPart, bone) ->
			val prev = smoothedLengths[bodyPart] ?: bone.offset
			prev + (bone.offset - prev) * alpha
		}
		return state.copy(
			rawBones = state.rawBones.mapValues { (bodyPart, bone) ->
				bone.copy(
					rawRotation = smoothedRotations[bodyPart] ?: bone.rawRotation,
					offset = smoothedLengths[bodyPart] ?: bone.offset,
				)
			},
		)
	}
}

class PredictionProcessor(var predictionAmount: Float) : SkeletonProcessor {
	override var enabled: Boolean = true

	private data class BoneVelocity(
		val lastRotation: Quaternion,
		val rotationDelta: Quaternion,
		val lastOffset: Vector3,
		val offsetDelta: Vector3,
	)

	private var velocities: Map<BodyPart, BoneVelocity> = emptyMap()

	override fun process(state: SkeletonState): SkeletonState {
		val newVelocities = mutableMapOf<BodyPart, BoneVelocity>()
		val newBones = state.rawBones.mapValues { (bodyPart, bone) ->
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
			val scaledDelta = Quaternion.IDENTITY.lerpR(rotationDelta, predictionAmount).unit()
			bone.copy(
				rawRotation = (scaledDelta * bone.rawRotation).unit(),
				offset = bone.offset + lengthDelta * predictionAmount,
			)
		}
		velocities = newVelocities
		return state.copy(rawBones = newBones)
	}
}
