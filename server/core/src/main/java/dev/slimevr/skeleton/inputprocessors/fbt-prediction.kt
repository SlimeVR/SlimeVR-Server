package dev.slimevr.skeleton.inputprocessors
import com.jme3.math.FastMath
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.collections.set

class FbtPrediction : SkeletonInputProcessor  {

	var lastHeadPosition = Vector3(0f,0f,0f);
	var lastHeadPositionNoY = Vector3(0f,0f,0f);

	var currentLeftLegUpperPitch = 0f;
	var currentRightLegUpperPitch = 0f;
	var currentLeftLegLowerPitch = 0f;
	var currentRightLegLowerPitch = 0f

	var lastLeftUpperLegPitchFloat = 0f
	var lastLeftLowerLegPitchFloat  = 0f
	var lastRightUpperLegPitchFloat = 0f
	var lastRightLowerLegPitchFloat = 0f

	var maxHeight = 0f
	private var lastHeadYaw = Quaternion.IDENTITY

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val head = mutableInputSkeleton[BodyPart.HEAD]
		if(head!!.isRotationActive && head!!.isPositionActive) {

			// We'll need to populate the height differently later, this is temporary
			if(head!!.position!!.y > maxHeight) {
				maxHeight = head.position!!.y
			}

			val heightUnits = maxHeight / 4f
			val standingThreshold = heightUnits * 3.5f
			val sittingOnGroundThreshold = heightUnits * 3;

			val currentHeadPosition =
				Vector3(head!!.position!!.x, head!!.position!!.y, head!!.position!!.x)
			val currentHeadPositionNoY =
				Vector3(head!!.position!!.x, 0f, head!!.position!!.x);

			val leftUpperLeg = mutableInputSkeleton[BodyPart.LEFT_UPPER_LEG]
			val rightUpperLeg = mutableInputSkeleton[BodyPart.RIGHT_UPPER_LEG]

			val leftLowerLeg = mutableInputSkeleton[BodyPart.LEFT_LOWER_LEG]
			val rightLowerLeg = mutableInputSkeleton[BodyPart.RIGHT_LOWER_LEG]

			val rotationRange = 45f;

			val headRotation = head.rotation.unit()

			val yawLength = sqrt(headRotation.w * headRotation.w + headRotation.y * headRotation.y)
			val headYawOnly = if (yawLength > 1e-5f) {
				Quaternion(headRotation.w / yawLength, 0f, headRotation.y / yawLength, 0f)
					.also { lastHeadYaw = it }
			} else {
				lastHeadYaw
			}

			val headTiltRotation = (headYawOnly.inv() * headRotation).unit()

			val rollLength = sqrt(headTiltRotation.w * headTiltRotation.w + headTiltRotation.z * headTiltRotation.z)
			val sideTiltRadians = if (rollLength > 1e-5f) {
				2f * atan2(headTiltRotation.z / rollLength, headTiltRotation.w / rollLength)
			} else {
				0f
			}
			val sideTiltDegrees = sideTiltRadians * FastMath.RAD_TO_DEG
			val normalizedTilt = (sideTiltDegrees / 45).coerceIn(-1f, 1f)

			currentLeftLegUpperPitch = (-normalizedTilt + 1) / 2
			currentRightLegUpperPitch = (normalizedTilt + 1) / 2

			currentLeftLegLowerPitch = (normalizedTilt + 1) / 2
			currentRightLegLowerPitch = (-normalizedTilt + 1) / 2

			if(head.position!!.y > standingThreshold) {
				if (!leftUpperLeg!!.isRotationActive) {
					var euler = (headYawOnly.inv() * leftUpperLeg.rotation).toEulerAngles(EulerOrder.XYZ)
					var pitch = calculateLegMotion(
						-rotationRange,
						rotationRange,
						rotationRange * 2,
						currentLeftLegUpperPitch) * FastMath.DEG_TO_RAD
					euler = EulerAngles(
						EulerOrder.XYZ,
						pitch,
						euler.y,
						euler.z
					)
					lastLeftUpperLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.LEFT_UPPER_LEG] =
						leftUpperLeg.copy(rotation = headYawOnly * euler.toQuaternion());
				}

				if (!leftLowerLeg!!.isRotationActive) {
					var euler = (headYawOnly.inv() * leftLowerLeg.rotation).toEulerAngles(EulerOrder.XYZ)
					var pitch = lerp(-rotationRange, rotationRange, currentLeftLegLowerPitch) * FastMath.DEG_TO_RAD
					euler = EulerAngles(
						EulerOrder.XYZ,
						pitch,
						euler.y,
						euler.z
					)
					lastLeftLowerLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.LEFT_LOWER_LEG] =
						leftLowerLeg.copy(rotation = headYawOnly * euler.toQuaternion());
				}

				if (!rightUpperLeg!!.isRotationActive) {
					var euler = (headYawOnly.inv() * rightUpperLeg.rotation).toEulerAngles(EulerOrder.XYZ)
					var pitch = calculateLegMotion(
						-rotationRange,
						rotationRange,
						rotationRange * 2,
						currentRightLegUpperPitch) * FastMath.DEG_TO_RAD
					euler = EulerAngles(
						EulerOrder.XYZ,
						pitch,
						euler.y,
						euler.z
					)
					lastRightUpperLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.RIGHT_UPPER_LEG] =
						rightUpperLeg.copy(rotation = headYawOnly * euler.toQuaternion());
				}

				if (!rightLowerLeg!!.isRotationActive) {
					var euler = (headYawOnly.inv() * rightLowerLeg.rotation).toEulerAngles(EulerOrder.XYZ)
					var pitch = lerp(-rotationRange, rotationRange, currentRightLegLowerPitch) * FastMath.DEG_TO_RAD
					euler = EulerAngles(
						EulerOrder.XYZ,
						pitch,
						euler.y,
						euler.z
					)
					lastRightLowerLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.RIGHT_LOWER_LEG] =
						rightLowerLeg.copy(rotation = headYawOnly * euler.toQuaternion());
				}
			}else if (head.position!!.y < standingThreshold)
			{
				val maxAngle = 45f
				if (!leftUpperLeg!!.isRotationActive) {
					var euler = (headYawOnly.inv() * leftUpperLeg.rotation).toEulerAngles(EulerOrder.XYZ)
					val pitch = lerp(lastLeftUpperLegPitchFloat, maxAngle, 1f) * FastMath.DEG_TO_RAD
					euler = EulerAngles(
						EulerOrder.XYZ,pitch,
						euler.y,
						euler.z
					)
					lastLeftUpperLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.LEFT_UPPER_LEG] =
						leftUpperLeg.copy(rotation = headYawOnly * euler.toQuaternion());
				}

				if (!leftLowerLeg!!.isRotationActive) {
					var euler = (headYawOnly.inv() * leftLowerLeg.rotation).toEulerAngles(EulerOrder.XYZ)
					val pitch = lerp(lastLeftLowerLegPitchFloat, maxAngle, 1f) * FastMath.DEG_TO_RAD
					euler = EulerAngles(
						EulerOrder.XYZ,pitch,
						euler.y,
						euler.z
					)
					lastLeftLowerLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.LEFT_LOWER_LEG] =
						leftLowerLeg.copy(rotation = headYawOnly * euler.toQuaternion());
				}

				if (!rightUpperLeg!!.isRotationActive) {
					var euler = (headYawOnly.inv() * rightUpperLeg.rotation).toEulerAngles(EulerOrder.XYZ)
					val pitch = lerp(lastRightUpperLegPitchFloat, maxAngle, 1f) * FastMath.DEG_TO_RAD
					euler = EulerAngles(
						EulerOrder.XYZ,
						pitch,
						euler.y,
						euler.z
					)
					lastRightUpperLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.RIGHT_UPPER_LEG] =
						rightUpperLeg.copy(rotation = headYawOnly * euler.toQuaternion());
				}

				if (!rightLowerLeg!!.isRotationActive) {
					var euler = (headYawOnly.inv() * rightLowerLeg.rotation).toEulerAngles(EulerOrder.XYZ)
					val pitch = lerp(lastRightLowerLegPitchFloat, maxAngle, 1f) * FastMath.DEG_TO_RAD
					euler = EulerAngles(
						EulerOrder.XYZ,
						pitch,
						euler.y,
						euler.z
					)
					lastRightLowerLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.RIGHT_LOWER_LEG] =
						rightLowerLeg.copy(rotation = headYawOnly * euler.toQuaternion());
				}
			}
			lastHeadPosition = currentHeadPosition;
			lastHeadPositionNoY = currentHeadPositionNoY;
		}
	}

	fun lerp(start: Float, end: Float, t: Float): Float {
		return start + (end - start) * t
	}

	fun parabola(start: Float, peak: Float, peakPercentage: Float, t: Float): Float {
		val x = t.coerceIn(0f, 1f)

		return if (x <= peakPercentage) {
			val u = x / peakPercentage
			start + (peak - start) * (2f * u - u * u)
		} else {
			val u = (x - peakPercentage) / 0.25f
			peak + (start - peak) * u * u
		}
	}

	fun calculateLegMotion(
		start: Float,
		end: Float,
		lift: Float = 0f,
		t: Float
	): Float {
		val progress = t.coerceIn(0f, 1f)
		val basePitch = lerp(start, end, progress)
		val liftAmount = if (progress <= 0.5f) {
			val u = progress * 2f
			4f * u * (1f - u) * lift
		} else {
			0f
		}

		return basePitch + liftAmount
	}
}
