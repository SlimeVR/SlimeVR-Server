package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

data class ArmSource(
	val bodyPart: BodyPart,
	val needsActive: Boolean = true,
	// null means it uses all remaining.
	val ratio: Float? = null,
	// For body-part-prerequisites.kt
	val reliable: Boolean = false,
)

val ARM_TO_SOURCE = arrayOf(
	BodyPart.LEFT_SHOULDER to arrayOf(
		ArmSource(BodyPart.LEFT_UPPER_ARM, true, 0.04f),
		ArmSource(BodyPart.UPPER_CHEST, false, null, true),
	),
	BodyPart.RIGHT_SHOULDER to arrayOf(
		ArmSource(BodyPart.RIGHT_UPPER_ARM, true, 0.04f),
		ArmSource(BodyPart.UPPER_CHEST, false, null, true),
	),

	BodyPart.LEFT_UPPER_ARM to arrayOf(
		ArmSource(BodyPart.LEFT_LOWER_ARM, true),
		ArmSource(BodyPart.LEFT_HAND, true),
		ArmSource(BodyPart.LEFT_SHOULDER, true, 0.4f),
		ArmSource(BodyPart.UPPER_CHEST, false),
	),
	BodyPart.RIGHT_UPPER_ARM to arrayOf(
		ArmSource(BodyPart.RIGHT_LOWER_ARM, true),
		ArmSource(BodyPart.RIGHT_HAND, true),
		ArmSource(BodyPart.RIGHT_SHOULDER, true, 0.4f),
		ArmSource(BodyPart.UPPER_CHEST, false),
	),

	BodyPart.LEFT_LOWER_ARM to arrayOf(
		ArmSource(BodyPart.LEFT_HAND, true),
		ArmSource(BodyPart.LEFT_UPPER_ARM, true),
		ArmSource(BodyPart.LEFT_SHOULDER, true, 0.2f),
		ArmSource(BodyPart.UPPER_CHEST, false),
	),
	BodyPart.RIGHT_LOWER_ARM to arrayOf(
		ArmSource(BodyPart.RIGHT_HAND, true),
		ArmSource(BodyPart.RIGHT_UPPER_ARM, true),
		ArmSource(BodyPart.RIGHT_SHOULDER, true, 0.2f),
		ArmSource(BodyPart.UPPER_CHEST, false),
	),

	BodyPart.LEFT_HAND to arrayOf(
		ArmSource(BodyPart.LEFT_LOWER_ARM),
		ArmSource(BodyPart.LEFT_UPPER_ARM),
		ArmSource(BodyPart.LEFT_SHOULDER, true, 0.25f),
		ArmSource(BodyPart.UPPER_CHEST, false),
	),
	BodyPart.RIGHT_HAND to arrayOf(
		ArmSource(BodyPart.RIGHT_LOWER_ARM),
		ArmSource(BodyPart.RIGHT_UPPER_ARM),
		ArmSource(BodyPart.RIGHT_SHOULDER, true, 0.25f),
		ArmSource(BodyPart.UPPER_CHEST, false),
	),
)

/**
 * Handles imputing missing arm bones from other arm bones and the upper chest.
 *
 * It's useful to set a good starting rotation for arms before IK.
 *
 * This processor must run after the spine processor.
 */
class ArmsInputProcessor : SkeletonInputProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val boneInputs = BodyPartMap(mutableInputSkeleton)
		for ((bodyPart, sources) in ARM_TO_SOURCE) {
			val bone = boneInputs[bodyPart] ?: continue
			if (bone.isRotationActive && bone.isAccelerationActive) continue

			var ratioSum = 0f
			var rotation: Quaternion? = null
			var acceleration: Vector3? = null
			for ((index, source) in sources.withIndex()) {
				val sourceBone = boneInputs[source.bodyPart] ?: continue
				if (source.needsActive && !sourceBone.isRotationActive) continue

				val isLastSource = index == sources.count() - 1
				val remainingRatio = 1f - ratioSum
				val ratio = (if (!isLastSource) source.ratio?.coerceAtMost(remainingRatio) else null) ?: remainingRatio
				if (!bone.isRotationActive) {
					rotation = (rotation ?: Quaternion.ZERO) + (sourceBone.rotation * ratio)
				}
				if (!bone.isAccelerationActive) {
					acceleration = (acceleration ?: Vector3.ZERO) + (sourceBone.acceleration * ratio)
				}

				ratioSum += ratio
				if (ratioSum >= 1f) break
			}

			if ((rotation == null || rotation == bone.rotation) && (acceleration == null || acceleration == bone.acceleration)) continue

			mutableInputSkeleton[bodyPart] = bone.copy(rotation = rotation?.unit() ?: bone.rotation, acceleration = acceleration ?: bone.acceleration)
		}
	}
}
