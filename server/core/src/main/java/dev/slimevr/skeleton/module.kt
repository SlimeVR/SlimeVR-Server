package dev.slimevr.skeleton

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

data class BoneState(
	val bodyPart: BodyPart,
	val length: Float,
	val rotation: Quaternion,
	val headPosition: Vector3,
	val tailPosition: Vector3,
	val parentBone: BoneState?,
) {
	val localRotation: Quaternion
		get() = parentBone?.let { it.rotation.inv() * rotation } ?: rotation
	val localHeadPosition: Vector3
		get() = parentBone?.let { headPosition - it.tailPosition } ?: headPosition
	val localTailPosition: Vector3
		get() = tailPosition - headPosition
}

data class SkeletonState(
	val bones: Map<BodyPart, BoneState>,
	val rootBone: BoneState,
)

fun makeBone(bodyPart: BodyPart, parent: BoneState? = null, length: Float = 0.1f): BoneState {
	val head = parent?.tailPosition ?: Vector3.NULL
	val offset = when (bodyPart) {
		BodyPart.HEAD -> Vector3(0f, 0f, length)
		BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND -> Vector3(0f, 0f, -length)
		BodyPart.LEFT_SHOULDER -> Vector3(-length, -0.08f, 0f)
		BodyPart.RIGHT_SHOULDER -> Vector3(length, -0.08f, 0f)
		BodyPart.LEFT_HIP -> Vector3(-length, 0f, 0f)
		BodyPart.RIGHT_HIP -> Vector3(length, 0f, 0f)
		else -> Vector3(0f, -length, 0f)
	}
	val rot = when (bodyPart) {
		BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT -> Quaternion.rotationAroundXAxis(FastMath.HALF_PI)
		else -> Quaternion.IDENTITY
	}

	val bone = BoneState(
		bodyPart = bodyPart,
		length = length,
		rotation = rot,
		headPosition = head,
		tailPosition = head + offset,
		parentBone = parent,
	)
	return bone
}

suspend fun SequenceScope<BoneState>.visitBone(bone: BoneState) {
	yield(bone)
}

fun navigateHierarchy(rootBone: BoneState): Sequence<BoneState> = sequence {
	visitBone(rootBone)
}

val DEFAULT_SKELETON_STATE = run {
	// Head/torso
	val head = makeBone(BodyPart.HEAD, length = 0.1f)
	val neck = makeBone(BodyPart.NECK, head, 0.1f)
	val upperChest = makeBone(BodyPart.UPPER_CHEST, neck, 0.16f)
	val chest = makeBone(BodyPart.CHEST, upperChest, 0.16f)
	val waist = makeBone(BodyPart.WAIST, chest, 0.2f)
	val hip = makeBone(BodyPart.HIP, waist, 0.04f)

	// Left leg
	val leftHip = makeBone(BodyPart.LEFT_HIP, hip, 0.13f)
	val leftUpperLeg = makeBone(BodyPart.LEFT_UPPER_LEG, leftHip, 0.42f)
	val leftLowerLeg = makeBone(BodyPart.LEFT_LOWER_LEG, leftUpperLeg, 0.5f)
	val leftFoot = makeBone(BodyPart.LEFT_FOOT, leftLowerLeg, 0.05f)

	// Right leg
	val rightHip = makeBone(BodyPart.RIGHT_HIP, hip, 0.13f)
	val rightUpperLeg = makeBone(BodyPart.RIGHT_UPPER_LEG, rightHip, 0.42f)
	val rightLowerLeg = makeBone(BodyPart.RIGHT_LOWER_LEG, rightUpperLeg, 0.5f)
	val rightFoot = makeBone(BodyPart.RIGHT_FOOT, rightLowerLeg, 0.05f)

	// Left arm
	val leftShoulder = makeBone(BodyPart.LEFT_SHOULDER, neck, 0.175f)
	val leftUpperArm = makeBone(BodyPart.LEFT_UPPER_ARM, leftShoulder, 0.26f)
	val leftLowerArm = makeBone(BodyPart.LEFT_LOWER_ARM, leftUpperArm, 0.26f)
	val leftHand = makeBone(BodyPart.LEFT_HAND, leftLowerArm, 0.13f)

	// Left fingers
	val leftThumbMetacarpal = makeBone(BodyPart.LEFT_THUMB_METACARPAL, leftHand, 0.025f)
	val leftThumbProximal = makeBone(BodyPart.LEFT_THUMB_PROXIMAL, leftThumbMetacarpal, 0.025f)
	val leftThumbDistal = makeBone(BodyPart.LEFT_THUMB_DISTAL, leftThumbProximal, 0.025f)

	val leftIndexProximal = makeBone(BodyPart.LEFT_INDEX_PROXIMAL, leftHand, 0.025f)
	val leftIndexIntermediate = makeBone(BodyPart.LEFT_INDEX_INTERMEDIATE, leftIndexProximal, 0.025f)
	val leftIndexDistal = makeBone(BodyPart.LEFT_INDEX_DISTAL, leftIndexIntermediate, 0.025f)

	val leftMiddleProximal = makeBone(BodyPart.LEFT_MIDDLE_PROXIMAL, leftHand, 0.025f)
	val leftMiddleIntermediate = makeBone(BodyPart.LEFT_MIDDLE_INTERMEDIATE, leftMiddleProximal, 0.025f)
	val leftMiddleDistal = makeBone(BodyPart.LEFT_MIDDLE_DISTAL, leftMiddleIntermediate, 0.025f)

	val leftRingProximal = makeBone(BodyPart.LEFT_RING_PROXIMAL, leftHand, 0.025f)
	val leftRingIntermediate = makeBone(BodyPart.LEFT_RING_INTERMEDIATE, leftRingProximal, 0.025f)
	val leftRingDistal = makeBone(BodyPart.LEFT_RING_DISTAL, leftRingIntermediate, 0.025f)

	val leftLittleProximal = makeBone(BodyPart.LEFT_LITTLE_PROXIMAL, leftHand, 0.025f)
	val leftLittleIntermediate = makeBone(BodyPart.LEFT_LITTLE_INTERMEDIATE, leftLittleProximal, 0.025f)
	val leftLittleDistal = makeBone(BodyPart.LEFT_LITTLE_DISTAL, leftLittleIntermediate, 0.025f)

	// Right arm
	val rightShoulder = makeBone(BodyPart.RIGHT_SHOULDER, neck, 0.175f)
	val rightUpperArm = makeBone(BodyPart.RIGHT_UPPER_ARM, rightShoulder, 0.26f)
	val rightLowerArm = makeBone(BodyPart.RIGHT_LOWER_ARM, rightUpperArm, 0.26f)
	val rightHand = makeBone(BodyPart.RIGHT_HAND, rightLowerArm, 0.13f)

	// Right fingers
	val rightThumbMetacarpal = makeBone(BodyPart.RIGHT_THUMB_METACARPAL, rightHand, 0.025f)
	val rightThumbProximal = makeBone(BodyPart.RIGHT_THUMB_PROXIMAL, rightThumbMetacarpal, 0.025f)
	val rightThumbDistal = makeBone(BodyPart.RIGHT_THUMB_DISTAL, rightThumbProximal, 0.025f)

	val rightIndexProximal = makeBone(BodyPart.RIGHT_INDEX_PROXIMAL, rightHand, 0.025f)
	val rightIndexIntermediate = makeBone(BodyPart.RIGHT_INDEX_INTERMEDIATE, rightIndexProximal, 0.025f)
	val rightIndexDistal = makeBone(BodyPart.RIGHT_INDEX_DISTAL, rightIndexIntermediate, 0.025f)

	val rightMiddleProximal = makeBone(BodyPart.RIGHT_MIDDLE_PROXIMAL, rightHand, 0.025f)
	val rightMiddleIntermediate = makeBone(BodyPart.RIGHT_MIDDLE_INTERMEDIATE, rightMiddleProximal, 0.025f)
	val rightMiddleDistal = makeBone(BodyPart.RIGHT_MIDDLE_DISTAL, rightMiddleIntermediate, 0.025f)

	val rightRingProximal = makeBone(BodyPart.RIGHT_RING_PROXIMAL, rightHand, 0.025f)
	val rightRingIntermediate = makeBone(BodyPart.RIGHT_RING_INTERMEDIATE, rightRingProximal, 0.025f)
	val rightRingDistal = makeBone(BodyPart.RIGHT_RING_DISTAL, rightRingIntermediate, 0.025f)

	val rightLittleProximal = makeBone(BodyPart.RIGHT_LITTLE_PROXIMAL, rightHand, 0.025f)
	val rightLittleIntermediate = makeBone(BodyPart.RIGHT_LITTLE_INTERMEDIATE, rightLittleProximal, 0.025f)
	val rightLittleDistal = makeBone(BodyPart.RIGHT_LITTLE_DISTAL, rightLittleIntermediate, 0.025f)

	SkeletonState(
		bones = navigateHierarchy(head).associateBy { it.bodyPart },
		rootBone = head,
	)
}
