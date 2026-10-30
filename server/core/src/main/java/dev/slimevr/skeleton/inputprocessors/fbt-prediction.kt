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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

private val DECISION_WAIT_TIME = 200.milliseconds
private val MAXIMUM_FOOT_ACCELERATION = 1.5f
private val MAXIMUM_LEG_RANGE = 25f

class FbtPrediction : SkeletonInputProcessor  {
	private var lastProcessTime = timeSource.markNow()
	private var lastHeadPositionNoY = Vector3(0f,0f,0f)
	private var currentJointPercentages = mutableMapOf<BodyPart, Float>()
	private	var lastJointPercentages = mutableMapOf<BodyPart, Float>()
	private var maxHeight = 0f
	private var lastNormalizedTilt = 0f
	private var lastHeadDirection = false
	private var lastBackwardsMovement = timeSource.markNow()
	private var lastMovement = timeSource.markNow()
	var headTiltMax = 5f;

	private var lastHeadYaw = Quaternion.IDENTITY

	override fun process(mutableInputSkeleton: InputSkeleton, skeletonHeight: Float) {
		val deltaTime = lastProcessTime.elapsedNow().inFloatingSeconds
		lastProcessTime = timeSource.markNow()

		val head = mutableInputSkeleton[BodyPart.HEAD]
		if(head!!.isRotationActive) {

			// We'll need to populate the user's height properly later, this is temporary
			if(head!!.position!!.y > maxHeight) {
				maxHeight = head.position!!.y
			}

			val leftUpperLeg = mutableInputSkeleton[BodyPart.LEFT_UPPER_LEG]
			val rightUpperLeg = mutableInputSkeleton[BodyPart.RIGHT_UPPER_LEG]
			val leftLowerLeg = mutableInputSkeleton[BodyPart.LEFT_LOWER_LEG]
			val rightLowerLeg = mutableInputSkeleton[BodyPart.RIGHT_LOWER_LEG]
			val leftFoot = mutableInputSkeleton[BodyPart.LEFT_FOOT]
			val rightFoot = mutableInputSkeleton[BodyPart.RIGHT_FOOT]

			val heightUnits = maxHeight / 4f
			val standingThreshold = heightUnits * 3.5f
			val currentHeadPositionNoY =
				Vector3(head!!.position!!.x, 0f, head!!.position!!.z);
			val headRotation = head.rotation.unit()
			val yawLength = sqrt(headRotation.w * headRotation.w + headRotation.y * headRotation.y)
			val headYaw = if (yawLength > 0.00001f) {
				Quaternion(headRotation.w / yawLength, 0f, headRotation.y / yawLength, 0f)
					.also { lastHeadYaw = it }
			} else {
				lastHeadYaw
			}
			val relativeForwardMovement = calculateForwardMovement(
				lastHeadPositionNoY, currentHeadPositionNoY, headYaw)
			val headTiltRotation = (headYaw.inv() * headRotation).unit()
			val rollLength = sqrt(headTiltRotation.w * headTiltRotation.w + headTiltRotation.z * headTiltRotation.z)
			val sideTiltRadians = if (rollLength > 0.00001f) {
				2f * atan2(headTiltRotation.z / rollLength, headTiltRotation.w / rollLength)
			} else {
				0f
			}

			val sideTiltDegrees = sideTiltRadians * FastMath.RAD_TO_DEG
			val absoluteSideTilt = abs(sideTiltDegrees)

			// Automatically calibrate to the users usual head tilt over time
			val decaySpeed = (0.001f * deltaTime * 60f).coerceIn(0f, 1f)
			if (absoluteSideTilt > headTiltMax) {
				headTiltMax = absoluteSideTilt
			} else {
				headTiltMax = lerp(headTiltMax, 5f, decaySpeed)
			}

			val normalizedTilt = (sideTiltDegrees / headTiltMax).coerceIn(-1f, 1f)
			var headDirection = if (abs(normalizedTilt - lastNormalizedTilt) > 0.001f) {
				normalizedTilt > lastNormalizedTilt
			} else {
				lastHeadDirection
			}
			if (relativeForwardMovement < 0.001f || lastBackwardsMovement.elapsedNow() < DECISION_WAIT_TIME) {
				headDirection = !headDirection
				lastBackwardsMovement = timeSource.markNow()
			}

			val leftLegTilt = (normalizedTilt + 1) / 2;
			currentJointPercentages[BodyPart.LEFT_UPPER_LEG] = leftLegTilt
			currentJointPercentages[BodyPart.LEFT_LOWER_LEG] = leftLegTilt
			currentJointPercentages[BodyPart.LEFT_FOOT] = leftLegTilt

			val rightLegTilt = (-normalizedTilt + 1) / 2;
			currentJointPercentages[BodyPart.RIGHT_UPPER_LEG] = rightLegTilt
			currentJointPercentages[BodyPart.RIGHT_LOWER_LEG] = rightLegTilt
			currentJointPercentages[BodyPart.RIGHT_FOOT] = rightLegTilt

			if(head.position!!.y > standingThreshold) {
				val absoluteForwardMovement = abs(relativeForwardMovement)

				if(absoluteForwardMovement > 0.0005f){
					lastMovement = timeSource.markNow()
				}

				// Allow leg movement after commiting to a direction.
				if(lastMovement.elapsedNow() < DECISION_WAIT_TIME) {
					generateUpperLeg(mutableInputSkeleton,leftUpperLeg, headYaw,
						MAXIMUM_LEG_RANGE, !headDirection)
					generateLowerLeg(mutableInputSkeleton,leftLowerLeg,
						headYaw, MAXIMUM_LEG_RANGE)
					generateUpperLeg(mutableInputSkeleton,rightUpperLeg,
						headYaw, MAXIMUM_LEG_RANGE, headDirection)
					generateLowerLeg(mutableInputSkeleton,	rightLowerLeg,
						headYaw, MAXIMUM_LEG_RANGE)
					generateFootAcceleration(mutableInputSkeleton, leftFoot)
					generateFootAcceleration(mutableInputSkeleton, rightFoot)
				}else{
					val staticAngle = 0f
					val transitionSpeed = 0.5f
					generateStaticPitch(mutableInputSkeleton,leftUpperLeg, headYaw, staticAngle, transitionSpeed)
					generateStaticPitch(mutableInputSkeleton,leftLowerLeg, headYaw, staticAngle, transitionSpeed)
					generateStaticPitch(mutableInputSkeleton,rightUpperLeg, headYaw, staticAngle, transitionSpeed)
					generateStaticPitch(mutableInputSkeleton,rightLowerLeg, headYaw, staticAngle, transitionSpeed)
				}
			}else if (head.position!!.y < standingThreshold)
			{
				val staticAngle = 90f
				val transitionSpeed = 0.5f
				generateStaticPitch(mutableInputSkeleton,leftUpperLeg, headYaw, staticAngle, transitionSpeed)
				generateStaticPitch(mutableInputSkeleton,leftLowerLeg, headYaw, staticAngle, transitionSpeed)
				generateStaticPitch(mutableInputSkeleton,rightUpperLeg, headYaw, staticAngle, transitionSpeed)
				generateStaticPitch(mutableInputSkeleton,rightLowerLeg, headYaw, staticAngle, transitionSpeed)
				generateStaticPitch(mutableInputSkeleton,leftFoot, headYaw, staticAngle, transitionSpeed)
				generateStaticPitch(mutableInputSkeleton,rightFoot, headYaw, staticAngle, transitionSpeed)
				setFootAcceleration(mutableInputSkeleton, leftFoot, MAXIMUM_FOOT_ACCELERATION)
				setFootAcceleration(mutableInputSkeleton, rightFoot, MAXIMUM_FOOT_ACCELERATION)
				headTiltMax = 5f
			}
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
				currentJointPercentages[boneInput.bodyPart],direction) * FastMath.DEG_TO_RAD
			euler = EulerAngles(
				EulerOrder.XYZ,
				pitch,
				euler.y,
				euler.z
			)
			lastJointPercentages[boneInput.bodyPart] = pitch
			mutableInputSkeleton[boneInput.bodyPart] =
				boneInput.copy(rotation = headYaw * euler.toQuaternion());
		}
	}

	fun generateFootAcceleration (
		mutableInputSkeleton: InputSkeleton, boneInput: BoneInput?){
		if (!boneInput!!.isRotationActive) {
			var acceleration = MAXIMUM_FOOT_ACCELERATION * currentJointPercentages[boneInput.bodyPart]!!;
			mutableInputSkeleton[boneInput.bodyPart] =
				boneInput.copy(acceleration =
					Vector3(acceleration,acceleration,acceleration));
		}
	}

	fun setFootAcceleration (
		mutableInputSkeleton: InputSkeleton, boneInput: BoneInput?, acceleration : Float){
		if (!boneInput!!.isRotationActive) {
			var acceleration = MAXIMUM_FOOT_ACCELERATION * currentJointPercentages[boneInput.bodyPart]!!;
			mutableInputSkeleton[boneInput.bodyPart] =
				boneInput.copy(acceleration =
					Vector3(acceleration,acceleration,acceleration));
		}
	}

	fun generateLowerLeg (
		mutableInputSkeleton: InputSkeleton, boneInput: BoneInput?,
		headYaw: Quaternion, rotationRange: Float){
		if (!boneInput!!.isRotationActive) {
			var euler = (headYaw.inv() * boneInput.rotation).toEulerAngles(EulerOrder.XYZ)
			var pitch = lerp(-rotationRange, rotationRange,
				currentJointPercentages[boneInput.bodyPart]) * FastMath.DEG_TO_RAD
			euler = EulerAngles(
				EulerOrder.XYZ,
				pitch,
				euler.y,
				euler.z
			)
			lastJointPercentages[boneInput.bodyPart] = pitch
			mutableInputSkeleton[boneInput.bodyPart] =
				boneInput.copy(rotation = headYaw * euler.toQuaternion());
		}
	}

	fun generateStaticPitch (
		mutableInputSkeleton: InputSkeleton, boneInput: BoneInput?,
		headYaw: Quaternion, staticAngle: Float, transitionSpeed: Float){
		if (!boneInput!!.isRotationActive) {
			var euler = (headYaw.inv() * boneInput.rotation).toEulerAngles(EulerOrder.XYZ)
			if(!lastJointPercentages.containsKey(boneInput.bodyPart)){
				lastJointPercentages[boneInput.bodyPart] = currentJointPercentages[boneInput.bodyPart]!!
			}
			val pitch = lerp(lastJointPercentages[boneInput.bodyPart],
				staticAngle, transitionSpeed) * FastMath.DEG_TO_RAD
			euler = EulerAngles(
				EulerOrder.XYZ,pitch,
				euler.y,
				euler.z
			)
			lastJointPercentages[boneInput.bodyPart] = pitch
			mutableInputSkeleton[boneInput.bodyPart] =
				boneInput.copy(rotation = headYaw * euler.toQuaternion());
		}
	}

	fun lerp(start: Float?, end: Float, time: Float?): Float {
		return start!! + (end - start) * time!!
	}

	fun calculateForwardMovement(
		previousPosition: Vector3,
		currentPosition: Vector3,
		headYaw: Quaternion
	): Float {
		val differenceX = currentPosition.x - previousPosition.x
		val differenceZ = currentPosition.z - previousPosition.z
		val yaw = 2f * atan2(headYaw.y, headYaw.w)
		val forwardX = -sin(yaw)
		val forwardZ = -cos(yaw)
		return differenceX * forwardX + differenceZ * forwardZ
	}

	fun calculateUpperLegMotion(
		start: Float,
		end: Float,
		time: Float?,
		movingForward : Boolean
	): Float {
		val bumpEnd = 0.80f
		val progress = time!!.coerceIn(0f, 1f)
		val basePitch = lerp(start, end, progress)
		if (!movingForward || progress <= 0.5f || progress >= bumpEnd) {
			return basePitch
		}
		val normalizedProgress = (progress - 0.5f) / (bumpEnd - 0.5f)
		val bump = 4f * normalizedProgress * (1f - normalizedProgress)
		return basePitch + (60f - end) * bump
	}
}
