package dev.slimevr.skeleton

import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.Test

class IKTest {
	@Test
	fun testCcdIk() {
		val boneInputs: InputSkeleton = bodyPartMap()
		boneInputs[BodyPart.NECK] = BoneInput(
			bodyPart = BodyPart.NECK,
			offset = Vector3.NEG_Y,
			rawRotation = Quaternion.IDENTITY,
			rawPosition = Vector3.NULL,
			isRotationActive = false,
			isPositionActive = false,
		)
		boneInputs[BodyPart.UPPER_CHEST] = BoneInput(
			bodyPart = BodyPart.UPPER_CHEST,
			offset = Vector3.NEG_Y,
			rawRotation = Quaternion.IDENTITY,
			rawPosition = Vector3.NULL,
			isRotationActive = false,
			isPositionActive = false,
		)
		boneInputs[BodyPart.CHEST] = BoneInput(
			bodyPart = BodyPart.CHEST,
			offset = Vector3.NEG_Y,
			rawRotation = Quaternion.IDENTITY,
			rawPosition = Vector3.NULL,
			isRotationActive = false,
			isPositionActive = false,
		)
		boneInputs[BodyPart.WAIST] = BoneInput(
			bodyPart = BodyPart.WAIST,
			offset = Vector3.NEG_Y,
			rawRotation = Quaternion.IDENTITY,
			rawPosition = Vector3.NULL,
			isRotationActive = false,
			isPositionActive = false,
		)
		boneInputs[BodyPart.HIP] = BoneInput(
			bodyPart = BodyPart.HIP,
			offset = Vector3.NEG_Y,
			rawRotation = Quaternion.IDENTITY,
			rawPosition = Vector3.NULL,
			isRotationActive = false,
			isPositionActive = false,
		)

		val bones = buildBones(boneInputs)
		val target = Vector3.POS_X * 4f
		val goal = IKChainGoal(
			listOf(
				BodyPart.NECK,
				BodyPart.UPPER_CHEST,
				BodyPart.CHEST,
				BodyPart.WAIST,
				BodyPart.HIP,
			),
			target,
		)

		val ikOut = ccdIk(boneInputs, bones, listOf(goal), null, 0.01f, 100)
		assert(ikOut.goalsReached.all { it.value }) {
			val boneRots = ikOut.bones.values.joinToString {
				"${it.bodyPart}: ${it.rotation.toEulerAngles(EulerOrder.YZX)}"
			}
			val targetDist = chainDistanceFromTarget(ikOut.bones, goal.chain, goal.target)
			"Failed to reach target:\nDistance from target: $targetDist\nBone rotations: $boneRots"
		}
	}
}
