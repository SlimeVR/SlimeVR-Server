package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

/**
 * Handles setting the rotation of an inactive bone with its source bone.
 */
class BustInputProcessor : SkeletonInputProcessor {

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		for (bodyPart in arrayOf(BodyPart.LEFT_BUST, BodyPart.RIGHT_BUST)) {
				val bone = mutableInputSkeleton[bodyPart] ?: continue
				val correctedRotation = invertBustPitch(bone.rotation)
				mutableInputSkeleton[bodyPart] = bone.copy(rotation = correctedRotation)
			}
		}

	private fun invertBustPitch(rotation: Quaternion): Quaternion {
		val euler = rotation.toEulerAngles(EulerOrder.XYZ)
		val pitch = Math.toDegrees(euler.x.toDouble()).toFloat()
		return EulerAngles(
			EulerOrder.YZX,
			-euler.x,
			euler.y,
			euler.z,
		).toQuaternion()
	}
}
