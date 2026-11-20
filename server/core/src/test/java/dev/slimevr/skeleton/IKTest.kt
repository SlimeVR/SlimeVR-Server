package dev.slimevr.skeleton

import com.jme3.math.FastMath
import dev.slimevr.quaternionAssertEquals
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
		boneInputs[BodyPart.LOWER_CHEST] = DEFAULT_BONE_INPUT.copy(
			bodyPart = BodyPart.LOWER_CHEST,
			offset = Vector3.NEG_Y,
		)
		boneInputs[BodyPart.UPPER_WAIST] = DEFAULT_BONE_INPUT.copy(
			bodyPart = BodyPart.UPPER_WAIST,
			offset = Vector3.NEG_Y,
		)
		boneInputs[BodyPart.LOWER_WAIST] = DEFAULT_BONE_INPUT.copy(
			bodyPart = BodyPart.LOWER_WAIST,
			offset = Vector3.NEG_Y,
		)
		boneInputs[BodyPart.HIP] = DEFAULT_BONE_INPUT.copy(
			bodyPart = BodyPart.HIP,
			offset = Vector3.NEG_Y,
		)

		val bones: ComputedSkeleton = bodyPartMap()
		buildBones(bones, boneInputs)
		val target = Vector3.POS_X * 3f
		val goal = IKChainGoal(
			listOf(
				BodyPart.NECK,
				BodyPart.UPPER_CHEST,
				BodyPart.LOWER_CHEST,
				BodyPart.UPPER_WAIST,
				BodyPart.LOWER_WAIST,
				BodyPart.HIP,
			),
			target,
		)

		val goalsMet = ccdIk(
			boneInputs,
			bones,
			listOf(goal),
			BODY_PART_OFFSET_CONSTRAINT_MAP,
			BODY_PART_CONSTRAINT_MAP,
			0.01f,
			100,
		)
		assert(goalsMet) {
			val boneRots = bones.values.joinToString {
				"${it.bodyPart}: ${it.rotation.toEulerAngles(EulerOrder.YZX)}"
			}
			val targetDist = chainDistanceFromTarget(bones, goal.chain, goal.target)
			"Failed to reach target:\nDistance from target: $targetDist\nBone rotations: $boneRots"
		}
	}

	@Test
	fun testRotateChain() {
		val boneInputs: InputSkeleton = bodyPartMap()
		boneInputs[BodyPart.NECK] = DEFAULT_BONE_INPUT.copy(
			bodyPart = BodyPart.NECK,
			offset = Vector3.NEG_Y,
			rotation = Quaternion.rotationAroundXAxis(FastMath.QUARTER_PI),
		)
		boneInputs[BodyPart.UPPER_CHEST] = DEFAULT_BONE_INPUT.copy(
			bodyPart = BodyPart.UPPER_CHEST,
			offset = Vector3.NEG_Y,
			rotation = Quaternion.rotationAroundYAxis(FastMath.QUARTER_PI),
		)
		boneInputs[BodyPart.LOWER_CHEST] = DEFAULT_BONE_INPUT.copy(
			bodyPart = BodyPart.LOWER_CHEST,
			offset = Vector3.NEG_Y,
			rotation = Quaternion.rotationAroundZAxis(FastMath.QUARTER_PI),
		)
		boneInputs[BodyPart.UPPER_WAIST] = DEFAULT_BONE_INPUT.copy(
			bodyPart = BodyPart.UPPER_WAIST,
			offset = Vector3.NEG_Y,
			rotation = Quaternion.rotationAroundXAxis(FastMath.HALF_PI),
		)
		boneInputs[BodyPart.LOWER_WAIST] = DEFAULT_BONE_INPUT.copy(
			bodyPart = BodyPart.LOWER_WAIST,
			offset = Vector3.NEG_Y,
			rotation = Quaternion.rotationAroundYAxis(FastMath.HALF_PI),
		)
		boneInputs[BodyPart.HIP] = DEFAULT_BONE_INPUT.copy(
			bodyPart = BodyPart.HIP,
			offset = Vector3.NEG_Y,
			rotation = Quaternion.rotationAroundZAxis(FastMath.HALF_PI),
		)

		val rotatedBoneInputs = boneInputs.clone()
		val chain = listOf(
			BodyPart.NECK,
			BodyPart.UPPER_CHEST,
			BodyPart.LOWER_CHEST,
			BodyPart.UPPER_WAIST,
			BodyPart.LOWER_WAIST,
			BodyPart.HIP,
		)
		rotateChain(
			rotatedBoneInputs,
			chain,
			Quaternion.rotationAroundYAxis(FastMath.QUARTER_PI),
		)

		assert(boneInputs != rotatedBoneInputs) {
			"Bones were not rotated"
		}

		fun getLocalRotation(
			bones: InputSkeleton,
			bone: BodyPart,
			parent: BodyPart,
		) = bones[bone]!!.rotation.localAbout(bones[parent]!!.rotation)

		for (i in 1 until chain.size) {
			val bone = chain[i]
			val parent = chain[i - 1]

			val originalLocal = getLocalRotation(boneInputs, bone, parent)
			val newLocal = getLocalRotation(rotatedBoneInputs, bone, parent)

			quaternionAssertEquals(
				originalLocal,
				newLocal,
				message = "Local rotations are not matching after chain rotation",
			)
		}
	}
}
