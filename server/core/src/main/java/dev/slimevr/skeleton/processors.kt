package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

interface SkeletonProcessor {
	var enabled: Boolean
	fun process(state: SkeletonState): SkeletonState
}

class SmoothingProcessor(var smoothing: Float) : SkeletonProcessor {
	override var enabled: Boolean = true
	private var smoothedRotations: Map<BodyPart, Quaternion> = emptyMap()
	private var smoothedLengths: Map<BodyPart, Float> = emptyMap()

	override fun process(state: SkeletonState): SkeletonState {
		val alpha = 1f - smoothing.coerceAtMost(0.99f)
		smoothedRotations = state.rawBones.mapValues { (bodyPart, bone) ->
			(smoothedRotations[bodyPart] ?: bone.rawRotation).lerpR(bone.rawRotation, alpha).unit()
		}
		smoothedLengths = state.rawBones.mapValues { (bodyPart, bone) ->
			val prev = smoothedLengths[bodyPart] ?: bone.length
			prev + (bone.length - prev) * alpha
		}
		return state.copy(
			rawBones = state.rawBones.mapValues { (bodyPart, bone) ->
				bone.copy(
					rawRotation = smoothedRotations[bodyPart] ?: bone.rawRotation,
					length = smoothedLengths[bodyPart] ?: bone.length,
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
		val lastLength: Float,
		val lengthDelta: Float,
	)

	private var velocities: Map<BodyPart, BoneVelocity> = emptyMap()

	override fun process(state: SkeletonState): SkeletonState {
		val newVelocities = mutableMapOf<BodyPart, BoneVelocity>()
		val newBones = state.rawBones.mapValues { (bodyPart, bone) ->
			val prev = velocities[bodyPart]
			if (prev == null) {
				newVelocities[bodyPart] = BoneVelocity(bone.rawRotation, Quaternion.IDENTITY, bone.length, 0f)
				return@mapValues bone
			}
			val rotationDelta = if (bone.rawRotation !== prev.lastRotation) {
				bone.rawRotation * prev.lastRotation.inv()
			} else {
				prev.rotationDelta
			}
			val lengthDelta = if (bone.length != prev.lastLength) {
				bone.length - prev.lastLength
			} else {
				prev.lengthDelta
			}
			newVelocities[bodyPart] = BoneVelocity(bone.rawRotation, rotationDelta, bone.length, lengthDelta)
			val scaledDelta = Quaternion.IDENTITY.lerpR(rotationDelta, predictionAmount).unit()
			bone.copy(
				rawRotation = (scaledDelta * bone.rawRotation).unit(),
				length = bone.length + lengthDelta * predictionAmount,
			)
		}
		velocities = newVelocities
		return state.copy(rawBones = newBones)
	}
}
