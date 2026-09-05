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
		boneInputs[BodyPart.NECK] = DEFAULT_BONE_INPUT.copy(
			bodyPart = BodyPart.NECK,
			offset = Vector3.NEG_Y,
		)
		boneInputs[BodyPart.UPPER_CHEST] = DEFAULT_BONE_INPUT.copy(
			bodyPart = BodyPart.UPPER_CHEST,
			offset = Vector3.NEG_Y,
		)
		boneInputs[BodyPart.CHEST] = DEFAULT_BONE_INPUT.copy(
			bodyPart = BodyPart.CHEST,
			offset = Vector3.NEG_Y,
		)
		boneInputs[BodyPart.WAIST] = DEFAULT_BONE_INPUT.copy(
			bodyPart = BodyPart.WAIST,
			offset = Vector3.NEG_Y,
		)
		boneInputs[BodyPart.HIP] = DEFAULT_BONE_INPUT.copy(
			bodyPart = BodyPart.HIP,
			offset = Vector3.NEG_Y,
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

		val ikOut = ccdIk(boneInputs, bones, listOf(goal), BODY_PART_CONSTRAINT_MAP, 0.01f, 100)
		assert(ikOut.goalsReached.all { it.value }) {
			val boneRots = ikOut.bones.values.joinToString {
				"${it.bodyPart}: ${it.rotation.toEulerAngles(EulerOrder.YZX)}"
			}
			val targetDist = chainDistanceFromTarget(ikOut.bones, goal.chain, goal.target)
			"Failed to reach target:\nDistance from target: $targetDist\nBone rotations: $boneRots"
		}
	}
}
