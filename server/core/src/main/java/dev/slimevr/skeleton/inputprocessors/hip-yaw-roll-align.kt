package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

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

/**
 * Handles rotating the hip' yaw and roll to match the upper legs' yaw and roll.
 */
class HipYawRollAlignInputProcessor(val settings: Settings) : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val hipBone = mutableInputSkeleton[BodyPart.HIP] ?: return
		if (hipBone.isRotationActive) return
		val leftUpperLegBone = mutableInputSkeleton[BodyPart.LEFT_UPPER_LEG] ?: return
		val rightUpperLegBone = mutableInputSkeleton[BodyPart.RIGHT_UPPER_LEG] ?: return
		if (!leftUpperLegBone.isRotationActive || !rightUpperLegBone.isRotationActive) return

		val ratio = settings.context.state.value.data.skeletonConfig.ratios.interpolateHipWithUpperLegs
		val sourceRotation = leftUpperLegBone.rotation.lerpQ(rightUpperLegBone.rotation, 0.5f)
		val alignedRotation = alignYawRoll(hipBone.rotation, sourceRotation)
		mutableInputSkeleton[BodyPart.HIP] = hipBone.copy(rotation = hipBone.rotation.interpQ(alignedRotation, ratio))
	}
}
