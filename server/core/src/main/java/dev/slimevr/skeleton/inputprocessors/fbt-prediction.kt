package dev.slimevr.skeleton.inputprocessors
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
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

			val headEuler = head!!.rotation.toEulerAngles(EulerOrder.XYZ)
			val normalizedTilt = (headEuler.z / 45f).coerceIn(-1f, 1f)

			currentLeftLegUpperPitch = (-normalizedTilt + 1) / 2
			currentRightLegUpperPitch = (normalizedTilt + 1) / 2

			currentLeftLegLowerPitch = (normalizedTilt + 1) / 2
			currentRightLegLowerPitch = (-normalizedTilt + 1) / 2

			if(head.position!!.y > standingThreshold) {
				if (!leftUpperLeg!!.isRotationActive) {
					var euler = leftUpperLeg.rotation.toEulerAngles(EulerOrder.XYZ)
					var pitch = calculateLegMotion(
						-rotationRange,
						rotationRange,
						currentLeftLegUpperPitch)
					euler = EulerAngles(
						EulerOrder.XYZ,
						pitch,
						euler.y,
						euler.z
					)
					lastLeftUpperLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.LEFT_UPPER_LEG] =
						leftUpperLeg.copy(rotation = euler.toQuaternion());
				}

				if (!leftLowerLeg!!.isRotationActive) {
					var euler = leftLowerLeg.rotation.toEulerAngles(EulerOrder.XYZ)
					var pitch = lerp(-rotationRange, rotationRange, currentLeftLegLowerPitch)
					euler = EulerAngles(
						EulerOrder.XYZ,
						pitch,
						euler.y,
						euler.z
					)
					lastLeftLowerLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.LEFT_LOWER_LEG] =
						leftLowerLeg.copy(rotation = euler.toQuaternion());
				}

				if (!rightUpperLeg!!.isRotationActive) {
					var euler = rightUpperLeg.rotation.toEulerAngles(EulerOrder.XYZ)
					var pitch = calculateLegMotion(
						-rotationRange,
						rotationRange,
						currentRightLegUpperPitch)
					euler = EulerAngles(
						EulerOrder.XYZ,
						pitch,
						euler.y,
						euler.z
					)
					lastRightUpperLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.RIGHT_UPPER_LEG] =
						rightUpperLeg.copy(rotation = euler.toQuaternion());
				}

				if (!rightLowerLeg!!.isRotationActive) {
					var euler = rightLowerLeg.rotation.toEulerAngles(EulerOrder.XYZ)
					var pitch = lerp(-rotationRange, rotationRange, currentRightLegLowerPitch)
					euler = EulerAngles(
						EulerOrder.XYZ,
						pitch,
						euler.y,
						euler.z
					)
					lastRightLowerLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.RIGHT_LOWER_LEG] =
						rightLowerLeg.copy(rotation = euler.toQuaternion());
				}
			}else if (head.position!!.y < standingThreshold)
			{
				if (!leftUpperLeg!!.isRotationActive) {
					var euler = leftUpperLeg.rotation.toEulerAngles(EulerOrder.XYZ)
					val pitch = lerp(lastLeftUpperLegPitchFloat, 90f, 1f)
					euler = EulerAngles(
						EulerOrder.XYZ,pitch,
						euler.y,
						euler.z
					)
					lastLeftUpperLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.LEFT_UPPER_LEG] =
						leftUpperLeg.copy(rotation = euler.toQuaternion());
				}

				if (!leftLowerLeg!!.isRotationActive) {
					var euler = leftLowerLeg.rotation.toEulerAngles(EulerOrder.XYZ)
					val pitch = lerp(lastLeftLowerLegPitchFloat, 90f, 1f)
					euler = EulerAngles(
						EulerOrder.XYZ,pitch,
						euler.y,
						euler.z
					)
					lastLeftLowerLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.LEFT_LOWER_LEG] =
						leftLowerLeg.copy(rotation = euler.toQuaternion());
				}

				if (!rightUpperLeg!!.isRotationActive) {
					var euler = rightUpperLeg.rotation.toEulerAngles(EulerOrder.XYZ)
					val pitch = lerp(lastRightUpperLegPitchFloat, 90f, 1f)
					euler = EulerAngles(
						EulerOrder.XYZ,
						pitch,
						euler.y,
						euler.z
					)
					lastRightUpperLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.RIGHT_UPPER_LEG] =
						rightUpperLeg.copy(rotation = euler.toQuaternion());
				}

				if (!rightLowerLeg!!.isRotationActive) {
					var euler = rightLowerLeg.rotation.toEulerAngles(EulerOrder.XYZ)
					val pitch = lerp(lastRightLowerLegPitchFloat, 90f, 1f)
					euler = EulerAngles(
						EulerOrder.XYZ,
						pitch,
						euler.y,
						euler.z
					)
					lastRightLowerLegPitchFloat = pitch
					mutableInputSkeleton[BodyPart.RIGHT_LOWER_LEG] =
						rightLowerLeg.copy(rotation = euler.toQuaternion());
				}
			}
			lastHeadPosition = currentHeadPosition;
			lastHeadPositionNoY = currentHeadPositionNoY;
		}
	}

	fun lerp(start: Float, end: Float, t: Float): Float {
		return start + (end - start) * t
	}
	fun lerpAngle(start: Float, end: Float, t: Float): Float {
		val difference = ((end - start + 540f) % 360f) - 180f
		val result = start + difference * t

		return ((result % 360f) + 360f) % 360f
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
		peak: Float,
		t: Float
	): Float {
		return if (t < 0f) {
			parabola(start, peak * 3,0.50f, -t)
		} else {
			lerp(peak, start, t)
		}
	}
}
