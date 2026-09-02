package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.mutateCopy
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles rotating the hip' yaw and roll to match the upper legs' yaw and roll.
 */
class HipYawRollAlignInputProcessor(val settings: Settings) : SkeletonInputProcessor {
	override fun process(inputSkeleton: InputSkeleton, skeletonHeight: Float): InputSkeleton {
		val hipBone = inputSkeleton[BodyPart.HIP] ?: return inputSkeleton
		if (hipBone.isRotationActive) return inputSkeleton
		val leftUpperLegBone = inputSkeleton[BodyPart.LEFT_UPPER_LEG] ?: return inputSkeleton
		val rightUpperLegBone = inputSkeleton[BodyPart.RIGHT_UPPER_LEG] ?: return inputSkeleton

		return inputSkeleton.mutateCopy { updated ->
			val ratio = settings.context.state.value.data.skeletonConfig.ratios.interpolateHipWithUpperLegs
			val sourceRotation = leftUpperLegBone.rotation.lerpQ(rightUpperLegBone.rotation, 0.5f)
			val alignedRotation = alignYawRoll(hipBone.rotation, sourceRotation)
			updated[BodyPart.HIP] = hipBone.copy(rotation = hipBone.rotation.interpQ(alignedRotation, ratio))
		}
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
