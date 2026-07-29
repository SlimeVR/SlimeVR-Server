package dev.slimevr.skeleton.processors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import dev.slimevr.skeleton.bodyPartMap
import dev.slimevr.skeleton.mapValues
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.rpc.FilteringType
import kotlin.time.Duration

/**
 * Tries to predict future rotations of bones.
 */
class BonePredictionProcessor(val settings: Settings) : SkeletonProcessor {
	private data class BoneVelocity(
		val lastRotation: Quaternion,
		val rotationDelta: Quaternion,
		val lastOffset: Vector3,
		val offsetDelta: Vector3,
	)

	private var velocities: BodyPartMap<BoneVelocity> = bodyPartMap()

	override fun process(state: SkeletonState, lastFrameTime: Duration): SkeletonState {
		val config = settings.context.state.value.data.skeletonConfig.filtering
		if (config.type != FilteringType.PREDICTION) return state
		val predictionAmount = config.amount

		val newVelocities = bodyPartMap<BoneVelocity>()
		val newBones = state.boneInputs.mapValues { bodyPart, bone ->
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
		return state.copy(boneInputs = newBones)
	}
}
