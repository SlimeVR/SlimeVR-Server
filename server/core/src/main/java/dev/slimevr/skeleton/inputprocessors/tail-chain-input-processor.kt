package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

/**
 * Simulates tail chain rotation compounding across TAIL_1 through TAIL_6.
 */
class TailChainInputProcessor : SkeletonInputProcessor {

	private val pitchScaleFactors = floatArrayOf(1.2f, 1.3f, 1.4f, 1.3f, 1.1f, 0.9f)
	private val yawScaleFactors = floatArrayOf(2.0f, 2.3f, 2.5f, 2.3f, 2.0f, 1.6f)

	private val tailSegments = arrayOf(
		BodyPart.TAIL_1,
		BodyPart.TAIL_2,
		BodyPart.TAIL_3,
		BodyPart.TAIL_4,
		BodyPart.TAIL_5,
		BodyPart.TAIL_6,
	)

	override fun process(
		mutableInputSkeleton: InputSkeleton,
		skeletonHeight: Float,
	) {
		val tailRoot = mutableInputSkeleton[BodyPart.TAIL] ?: return
		val hip = mutableInputSkeleton[BodyPart.HIP] ?: return

		val relativeRot = hip.rotation.inv() * tailRoot.rotation
		val euler = relativeRot.toEulerAngles(EulerOrder.XYZ)

		var currentParentRot = tailRoot.rotation

		for ((index, segmentPart) in tailSegments.withIndex()) {
			val bone = mutableInputSkeleton[segmentPart] ?: continue

			val pitchScale = if (index < pitchScaleFactors.size) pitchScaleFactors[index] else 0.9f
			val yawScale = if (index < yawScaleFactors.size) yawScaleFactors[index] else 1.6f

			val stepEuler = Vector3(euler.x * pitchScale, euler.y * yawScale, euler.z * pitchScale)
			val stepLocalRot = EulerAngles(EulerOrder.XYZ, stepEuler.x, stepEuler.y, stepEuler.z).toQuaternion()

			currentParentRot *= stepLocalRot

			mutableInputSkeleton[segmentPart] = bone.copy(
				rotation = if (!bone.isRotationActive) currentParentRot else bone.rotation,
			)
			if (bone.isRotationActive) {
				currentParentRot = bone.rotation
			}
		}
	}
}
