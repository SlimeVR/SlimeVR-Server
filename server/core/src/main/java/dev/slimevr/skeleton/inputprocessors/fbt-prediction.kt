package dev.slimevr.skeleton.inputprocessors
import com.jme3.math.FastMath
import dev.slimevr.skeleton.BoneInput
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.collections.set
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

private val DECISION_WAIT_TIME = 200.milliseconds

class FbtPrediction : SkeletonInputProcessor  {
	private var lastProcessTime = timeSource.markNow()

	var lastHeadPosition = Vector3(0f,0f,0f)
	var lastHeadPositionNoY = Vector3(0f,0f,0f)

	var currentJointPitches = mutableMapOf<BodyPart, Float>()
	var lastJointPitches = mutableMapOf<BodyPart, Float>()

	var maxHeight = 0f
	var lastNormalizedTilt = 0f
	private var lastLegDirection = false
	private var lastBackwardsMovement = timeSource.markNow()
	private var lastMovement = timeSource.markNow()
	var headTiltMax = 5f;
	var wasWalking = false
	private var lastHeadYaw = Quaternion.IDENTITY

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val deltaTime = lastProcessTime.elapsedNow().inFloatingSeconds
		lastProcessTime = timeSource.markNow()

		val head = mutableInputSkeleton[BodyPart.HEAD]
		if(head!!.isRotationActive) {
			val rotationRange = 25f

			// We'll need to populate the height differently later, this is temporary
			if(head!!.position!!.y > maxHeight) {
				maxHeight = head.position!!.y
			}

			val heightUnits = maxHeight / 4f
			val standingThreshold = heightUnits * 3.5f
			val sittingOnGroundThreshold = heightUnits * 3;

			val currentHeadPosition =
				Vector3(head!!.position!!.x, head!!.position!!.y, head!!.position!!.z)
			val currentHeadPositionNoY =
				Vector3(head!!.position!!.x, 0f, head!!.position!!.z);

			val leftUpperLeg = mutableInputSkeleton[BodyPart.LEFT_UPPER_LEG]
			val rightUpperLeg = mutableInputSkeleton[BodyPart.RIGHT_UPPER_LEG]
			val leftLowerLeg = mutableInputSkeleton[BodyPart.LEFT_LOWER_LEG]
			val rightLowerLeg = mutableInputSkeleton[BodyPart.RIGHT_LOWER_LEG]

			val headRotation = head.rotation.unit()

			val yawLength = sqrt(headRotation.w * headRotation.w + headRotation.y * headRotation.y)
			val headYawOnly = if (yawLength > 1e-5f) {
				Quaternion(headRotation.w / yawLength, 0f, headRotation.y / yawLength, 0f)
					.also { lastHeadYaw = it }
			} else {
				lastHeadYaw
			}

			val relativeForwardMovement = calculateForwardMovement(
				lastHeadPositionNoY, currentHeadPositionNoY, headYawOnly)

			val headTiltRotation = (headYawOnly.inv() * headRotation).unit()

			val rollLength = sqrt(headTiltRotation.w * headTiltRotation.w + headTiltRotation.z * headTiltRotation.z)

			val sideTiltRadians = if (rollLength > 1e-5f) {
				2f * atan2(headTiltRotation.z / rollLength, headTiltRotation.w / rollLength)
			} else {
				0f
			}

			val sideTiltDegrees = sideTiltRadians * FastMath.RAD_TO_DEG
			val absoluteSideTilt = Math.abs(sideTiltDegrees)

			val decaySpeed = (0.001f * deltaTime * 60f).coerceIn(0f, 1f)

			if (absoluteSideTilt > headTiltMax) {
				headTiltMax = absoluteSideTilt
			} else {
				headTiltMax = lerp(headTiltMax, 5f, decaySpeed)
			}
			val normalizedTilt = (sideTiltDegrees / headTiltMax).coerceIn(-1f, 1f)
			var headDirection = if (kotlin.math.abs(normalizedTilt - lastNormalizedTilt) > 0.001f) {
				normalizedTilt > lastNormalizedTilt
			} else {
				lastLegDirection
			}
			if (relativeForwardMovement < 0.001f || lastBackwardsMovement.elapsedNow() < DECISION_WAIT_TIME) {
				headDirection = !headDirection
				lastBackwardsMovement = timeSource.markNow()
			}

			currentJointPitches[BodyPart.LEFT_UPPER_LEG] = (normalizedTilt + 1) / 2
			currentJointPitches[BodyPart.RIGHT_UPPER_LEG] = (-normalizedTilt + 1) / 2

			currentJointPitches[BodyPart.LEFT_LOWER_LEG] = (normalizedTilt + 1) / 2
			currentJointPitches[BodyPart.RIGHT_LOWER_LEG] = (-normalizedTilt + 1) / 2

			if(head.position!!.y > standingThreshold) {
				val absoluteForwardMovement = Math.abs(relativeForwardMovement)
				if(absoluteForwardMovement > 0.0005f){
					lastMovement = timeSource.markNow()
				}
				if(lastMovement.elapsedNow() < DECISION_WAIT_TIME) {
					generateUpperLeg(mutableInputSkeleton,leftUpperLeg, headYawOnly, rotationRange, !headDirection)
					generateLowerLeg(mutableInputSkeleton,leftLowerLeg, headYawOnly, rotationRange)
					generateUpperLeg(mutableInputSkeleton,rightUpperLeg, headYawOnly, rotationRange, headDirection)
					generateLowerLeg(mutableInputSkeleton,	rightLowerLeg, headYawOnly, rotationRange)
				}else{
					val staticAngle = 0f
					val transitionSpeed = 0.5f
					generateStaticPitch(mutableInputSkeleton,leftUpperLeg, headYawOnly, staticAngle, transitionSpeed)
					generateStaticPitch(mutableInputSkeleton,leftLowerLeg, headYawOnly, staticAngle, transitionSpeed)
					generateStaticPitch(mutableInputSkeleton,rightUpperLeg, headYawOnly, staticAngle, transitionSpeed)
					generateStaticPitch(mutableInputSkeleton,rightLowerLeg, headYawOnly, staticAngle, transitionSpeed)
				}
			}else if (head.position!!.y < standingThreshold)
			{
				val staticAngle = 45f
				val transitionSpeed = 0.5f
				generateStaticPitch(mutableInputSkeleton,leftUpperLeg, headYawOnly, staticAngle, transitionSpeed)
				generateStaticPitch(mutableInputSkeleton,leftLowerLeg, headYawOnly, staticAngle, transitionSpeed)
				generateStaticPitch(mutableInputSkeleton,rightUpperLeg, headYawOnly, staticAngle, transitionSpeed)
				generateStaticPitch(mutableInputSkeleton,rightLowerLeg, headYawOnly, staticAngle, transitionSpeed)
				headTiltMax = 5f
			}
			lastHeadPosition = currentHeadPosition
			lastHeadPositionNoY = currentHeadPositionNoY
			lastNormalizedTilt = normalizedTilt
		}
	}

	fun generateUpperLeg (
		mutableInputSkeleton: InputSkeleton, boneInput: BoneInput?,
		headYaw: Quaternion, rotationRange: Float, direction: Boolean
	){
		if (!boneInput!!.isRotationActive) {
			var euler = (headYaw.inv() * boneInput.rotation).toEulerAngles(EulerOrder.XYZ)
			var pitch = calculateUpperLegMotion(
				-rotationRange,
				rotationRange,
				currentJointPitches[boneInput.bodyPart],direction) * FastMath.DEG_TO_RAD
			euler = EulerAngles(
				EulerOrder.XYZ,
				pitch,
				euler.y,
				euler.z
			)
			lastJointPitches[boneInput.bodyPart] = pitch
			mutableInputSkeleton[boneInput.bodyPart] =
				boneInput.copy(rotation = headYaw * euler.toQuaternion());
		}
	}

	fun generateLowerLeg (
		mutableInputSkeleton: InputSkeleton, boneInput: BoneInput?,
		headYaw: Quaternion, rotationRange: Float){
		if (!boneInput!!.isRotationActive) {
			var euler = (headYaw.inv() * boneInput.rotation).toEulerAngles(EulerOrder.XYZ)
			var pitch = lerp(-rotationRange, rotationRange, currentJointPitches[boneInput.bodyPart]) * FastMath.DEG_TO_RAD
			euler = EulerAngles(
				EulerOrder.XYZ,
				pitch,
				euler.y,
				euler.z
			)
			lastJointPitches[boneInput.bodyPart] = pitch
			mutableInputSkeleton[boneInput.bodyPart] =
				boneInput.copy(rotation = headYaw * euler.toQuaternion());
		}
	}

	fun generateStaticPitch (
		mutableInputSkeleton: InputSkeleton, boneInput: BoneInput?,
		headYaw: Quaternion, staticAngle: Float, transitionSpeed: Float){
		if (!boneInput!!.isRotationActive) {
			var euler = (headYaw.inv() * boneInput.rotation).toEulerAngles(EulerOrder.XYZ)
			if(!lastJointPitches.containsKey(boneInput.bodyPart)){
				lastJointPitches[boneInput.bodyPart] = currentJointPitches[boneInput.bodyPart]!!
			}
			val pitch = lerp(lastJointPitches[boneInput.bodyPart], staticAngle, transitionSpeed) * FastMath.DEG_TO_RAD
			euler = EulerAngles(
				EulerOrder.XYZ,pitch,
				euler.y,
				euler.z
			)
			lastJointPitches[boneInput.bodyPart] = pitch
			mutableInputSkeleton[boneInput.bodyPart] =
				boneInput.copy(rotation = headYaw * euler.toQuaternion());
		}
	}

	fun lerp(start: Float?, end: Float, t: Float?): Float {
		return start!! + (end - start) * t!!
	}

	fun calculateForwardMovement(
		previousPosition: Vector3,
		currentPosition: Vector3,
		headYaw: Quaternion
	): Float {
		val dx = currentPosition.x - previousPosition.x
		val dz = currentPosition.z - previousPosition.z
		val yaw = 2f * atan2(headYaw.y, headYaw.w)
		val forwardX = -sin(yaw)
		val forwardZ = -cos(yaw)
		return dx * forwardX + dz * forwardZ
	}

	fun calculateUpperLegMotion(
		start: Float,
		end: Float,
		t: Float?,
		movingForward : Boolean
	): Float {
		val bumpEnd = 0.80f
		val progress = t!!.coerceIn(0f, 1f)
		val basePitch = lerp(start, end, progress)
		if (!movingForward || progress <= 0.5f || progress >= bumpEnd) return basePitch
		val u = (progress - 0.5f) / (bumpEnd - 0.5f)
		val bump = 4f * u * (1f - u)
		return basePitch + (60f - end) * bump
	}
}
