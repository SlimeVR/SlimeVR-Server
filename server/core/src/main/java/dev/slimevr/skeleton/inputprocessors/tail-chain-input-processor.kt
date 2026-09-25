package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.ResettableSkeletonProcessor
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ResetType
import kotlin.math.exp

/**
 * Simulates tail chain rotation compounding across TAIL_1 through TAIL_6
 */
class TailChainInputProcessor : SkeletonInputProcessor, ResettableSkeletonProcessor {

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

	private var driftYaw = 0f
	private var lastUpdateNanos = 0L

	companion object {
		const val DRIFT_RETURN_RATE = 0.02f

		private const val MAX_DELTA_TIME = 0.05f
	}

	override fun reset(resetType: ResetType) {
		driftYaw = 0f
		lastUpdateNanos = 0L
	}

	override fun process(
		mutableInputSkeleton: InputSkeleton,
		skeletonHeight: Float,
	) {
		val tailRoot = mutableInputSkeleton[BodyPart.TAIL] ?: return
		val hip = mutableInputSkeleton[BodyPart.HIP] ?: return

		val now = System.nanoTime()
		var deltaTime = if (lastUpdateNanos != 0L) {
			(now - lastUpdateNanos).toFloat() / 1_000_000_000f
		} else {
			0f
		}
		lastUpdateNanos = now
		deltaTime = deltaTime.coerceIn(0f, MAX_DELTA_TIME)

		val rawRef = hip.rotation
		val (_, _, refYaw, _) = rawRef.toEulerAngles(EulerOrder.YXZ)
		val refRot = Quaternion.rotationAroundYAxis(refYaw)

		val relativeToRef = refRot.inv() * tailRoot.rotation
		val relativeEuler = relativeToRef.toEulerAngles(EulerOrder.XYZ)
		val currentPitch = relativeEuler.x
		val currentYaw = relativeEuler.y
		val currentRoll = relativeEuler.z

		if (deltaTime > 0f) {
			val alpha = (1f - exp(-deltaTime * DRIFT_RETURN_RATE)).coerceIn(0f, 1f)
			driftYaw += (currentYaw - driftYaw) * alpha
		}

		val compYaw = currentYaw - driftYaw
		val compensatedRelativeRef = EulerAngles(EulerOrder.XYZ, currentPitch, compYaw, currentRoll).toQuaternion()
		val compensatedTailWorld = refRot * compensatedRelativeRef

		mutableInputSkeleton[BodyPart.TAIL] = tailRoot.copy(rotation = compensatedTailWorld)

		val relativeRot = hip.rotation.inv() * compensatedTailWorld
		val euler = relativeRot.toEulerAngles(EulerOrder.XYZ)

		var currentParentRot = compensatedTailWorld

		for ((index, segmentPart) in tailSegments.withIndex()) {
			val bone = mutableInputSkeleton[segmentPart] ?: continue

			val pitchScale = if (index < pitchScaleFactors.size) pitchScaleFactors[index] else 0.9f
			val yawScale = if (index < yawScaleFactors.size) yawScaleFactors[index] else 1.6f
			val stepEuler = Vector3(euler.x * pitchScale, euler.y * yawScale, 0f)
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





