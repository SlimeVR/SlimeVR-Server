package dev.slimevr.skeleton.processors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.SkeletonProcessor
import dev.slimevr.skeleton.SkeletonState
import dev.slimevr.skeleton.mutate
import dev.slimevr.skeleton.resolveAverageRotationFor
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles rotating the hip' yaw and roll to match the upper legs' yaw and roll.
 */
class HipYawRollAlignProcessor(val settings: Settings) : SkeletonProcessor {

	val source = arrayOf(BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG)

	override fun process(state: SkeletonState): SkeletonState {
		val boneInputs = state.boneInputs

		val hipBone = boneInputs.getValue(BodyPart.HIP)
		if (!hipBone.isActive) {
			val ratio = settings.context.state.value.data.skeletonConfig.ratios.interpolateHipWithUpperLegs
			val sourceRotation = boneInputs.resolveAverageRotationFor(source)
			val alignedRotation = alignYawRoll(hipBone.rawRotation, sourceRotation)
			return state.copy(boneInputs = boneInputs.mutate { it[BodyPart.HIP] = hipBone.copy(rawRotation = hipBone.rawRotation.interpQ(alignedRotation, ratio)) })
		}

		return state
	}

	/**
	 * Rotates the first Quaternion to match its yaw and roll to the rotation of
	 * the second Quaternion
	 *
	 * @param from the first Quaternion
	 * @param to the second Quaternion
	 * @return the rotated Quaternion
	 */
	private fun alignYawRoll(from: Quaternion, to: Quaternion): Quaternion {
		val r = from.inv() * to
		val c = Quaternion(r.w, -r.x, 0f, 0f)
		return (from * r * c).unit()
	}
}
