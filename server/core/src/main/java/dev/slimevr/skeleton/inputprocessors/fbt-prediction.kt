package dev.slimevr.skeleton.inputprocessors
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.skeleton.headPartSet
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.collections.set

class FbtPrediction : SkeletonInputProcessor  {

	var lastHeadPosition = Vector3(0,0,0);
	var lastHeadPositionNoY = Vector3(0,0,0);

	var currentLeftLegUpperPitch = 0f;
	var currentRightLegUpperPitch = 0f;
	var currentLeftLegLowerPitch = 0f;
	var currentRightLegLowerPitch = 0f;

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val head = mutableInputSkeleton[BodyPart.HEAD]

		lastHeadPosition = head!!.position
		lastHeadPositionNoY = Vector3(head!!.position!!.x, 0,head!!.position!!.x);
		val leftUpperLeg = mutableInputSkeleton[BodyPart.LEFT_UPPER_LEG]
		val rightUpperLeg = mutableInputSkeleton[BodyPart.RIGHT_UPPER_LEG]

		val leftLowerLeg = mutableInputSkeleton[BodyPart.LEFT_LOWER_LEG]
		val rightLowerLeg = mutableInputSkeleton[BodyPart.RIGHT_LOWER_LEG]

		val rotationRange = 45f;

		val headEuler = head!!.rotation.toEulerAngles(EulerOrder.XYZ)
		val normalizedTilt = (headEuler.z / 45f).coerceIn(-1f, 1f)

		currentLeftLegUpperPitch = (normalizedTilt + 1) / 2
		currentRightLegUpperPitch = (-normalizedTilt + 1) / 2

		currentLeftLegLowerPitch = (-normalizedTilt + 1) / 2
		currentRightLegLowerPitch = (normalizedTilt + 1) / 2

		if(!leftUpperLeg!!.isRotationActive){
		    var euler =	leftUpperLeg.rotation.toEulerAngles(EulerOrder.XYZ)
			euler = EulerAngles(EulerOrder.XYZ, calculateLegMotion(-rotationRange, rotationRange, currentLeftLegUpperPitch), euler.y, euler.z)
			mutableInputSkeleton[BodyPart.LEFT_UPPER_LEG] = leftUpperLeg.copy(rotation = euler.toQuaternion());
		}

		if(!leftLowerLeg!!.isRotationActive){
			var euler =	leftLowerLeg.rotation.toEulerAngles(EulerOrder.XYZ)
			euler = EulerAngles(EulerOrder.XYZ, lerp(-rotationRange, rotationRange, currentLeftLegLowerPitch), euler.y, euler.z)
			mutableInputSkeleton[BodyPart.LEFT_LOWER_LEG] = leftLowerLeg.copy(rotation = euler.toQuaternion());
		}

		if(!rightUpperLeg!!.isRotationActive) {
			var euler = rightUpperLeg.rotation.toEulerAngles(EulerOrder.XYZ)
			euler = EulerAngles(EulerOrder.XYZ, calculateLegMotion(-rotationRange, rotationRange, currentRightLegUpperPitch), euler.y, euler.z)
			mutableInputSkeleton[BodyPart.RIGHT_UPPER_LEG] = rightUpperLeg.copy(rotation = euler.toQuaternion());
		}

		if(!rightLowerLeg!!.isRotationActive) {
			var euler = rightLowerLeg.rotation.toEulerAngles(EulerOrder.XYZ)
			euler = EulerAngles(EulerOrder.XYZ, lerp(-rotationRange, rotationRange, currentRightLegLowerPitch), euler.y, euler.z)
			mutableInputSkeleton[BodyPart.RIGHT_LOWER_LEG] = rightLowerLeg.copy(rotation = euler.toQuaternion());
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
		peak: Float,
		t: Float
	): Float {
		return if (t < 0f) {
			parabola(start, peak * 2,0.50f, -t)
		} else {
			lerp(peak, start, t)
		}
	}
}
