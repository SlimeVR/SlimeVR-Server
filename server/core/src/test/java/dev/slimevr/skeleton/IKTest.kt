package dev.slimevr.skeleton

import dev.slimevr.bones.BoneMap
import dev.slimevr.bones.BoneRegistry
import dev.slimevr.bones.boneId
import dev.slimevr.bones.resolveToBoneIds
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Vector3
import dev.slimevr.bones.BodyPart
import kotlin.test.Test

class IKTest {
	@Test
	fun testCcdIk() {
		val registry = BoneRegistry.standard()

		val boneInputs: InputSkeleton = BoneMap.of(registry)
		boneInputs[BodyPart.NECK.boneId] = DEFAULT_BONE_INPUT.copy(
			boneId = BodyPart.NECK.boneId,
			offset = Vector3.NEG_Y,
		)
		boneInputs[BodyPart.UPPER_CHEST.boneId] = DEFAULT_BONE_INPUT.copy(
			boneId = BodyPart.UPPER_CHEST.boneId,
			offset = Vector3.NEG_Y,
		)
		boneInputs[BodyPart.LOWER_CHEST.boneId] = DEFAULT_BONE_INPUT.copy(
			boneId = BodyPart.LOWER_CHEST.boneId,
			offset = Vector3.NEG_Y,
		)
		boneInputs[BodyPart.UPPER_WAIST.boneId] = DEFAULT_BONE_INPUT.copy(
			boneId = BodyPart.UPPER_WAIST.boneId,
			offset = Vector3.NEG_Y,
		)
		boneInputs[BodyPart.LOWER_WAIST.boneId] = DEFAULT_BONE_INPUT.copy(
			boneId = BodyPart.LOWER_WAIST.boneId,
			offset = Vector3.NEG_Y,
		)
		boneInputs[BodyPart.HIP.boneId] = DEFAULT_BONE_INPUT.copy(
			boneId = BodyPart.HIP.boneId,
			offset = Vector3.NEG_Y,
		)

		val bones = buildBones(boneInputs)
		val target = Vector3.POS_X * 3f
		val goal = IKChainGoal(
			listOf(
				BodyPart.NECK.boneId,
				BodyPart.UPPER_CHEST.boneId,
				BodyPart.LOWER_CHEST.boneId,
				BodyPart.UPPER_WAIST.boneId,
				BodyPart.LOWER_WAIST.boneId,
				BodyPart.HIP.boneId,
			),
			target,
		)

		val ikOut = ccdIk(boneInputs, bones, listOf(goal), BODY_PART_CONSTRAINT_MAP.resolveToBoneIds(), 0.01f, 100)
		assert(ikOut.goalsReached.all { it.value }) {
			val boneRots = ikOut.bones.values.joinToString {
				"${it.boneId}: ${it.rotation.toEulerAngles(EulerOrder.YZX)}"
			}
			val targetDist = chainDistanceFromTarget(ikOut.bones, goal.chain, goal.target)
			"Failed to reach target:\nDistance from target: $targetDist\nBone rotations: $boneRots"
		}
	}
}
