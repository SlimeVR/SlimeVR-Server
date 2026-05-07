package dev.slimevr.vmc

import com.jme3.math.FastMath
import dev.slimevr.skeleton.BoneState
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart


val BODY_PART_TO_UNITY_BONE: Map<BodyPart, String> = mapOf(
	BodyPart.HEAD to "Head",
	BodyPart.NECK to "Neck",
	BodyPart.UPPER_CHEST to "UpperChest",
	BodyPart.CHEST to "Chest",
	BodyPart.WAIST to "Spine",
	BodyPart.HIP to "Hips",
	BodyPart.LEFT_SHOULDER to "LeftShoulder",
	BodyPart.RIGHT_SHOULDER to "RightShoulder",
	BodyPart.LEFT_UPPER_ARM to "LeftUpperArm",
	BodyPart.RIGHT_UPPER_ARM to "RightUpperArm",
	BodyPart.LEFT_LOWER_ARM to "LeftLowerArm",
	BodyPart.RIGHT_LOWER_ARM to "RightLowerArm",
	BodyPart.LEFT_HAND to "LeftHand",
	BodyPart.RIGHT_HAND to "RightHand",
	BodyPart.LEFT_UPPER_LEG to "LeftUpperLeg",
	BodyPart.RIGHT_UPPER_LEG to "RightUpperLeg",
	BodyPart.LEFT_LOWER_LEG to "LeftLowerLeg",
	BodyPart.RIGHT_LOWER_LEG to "RightLowerLeg",
	BodyPart.LEFT_FOOT to "LeftFoot",
	BodyPart.RIGHT_FOOT to "RightFoot",
	BodyPart.LEFT_THUMB_METACARPAL to "LeftThumbProximal",
	BodyPart.LEFT_THUMB_PROXIMAL to "LeftThumbIntermediate",
	BodyPart.LEFT_THUMB_DISTAL to "LeftThumbDistal",
	BodyPart.LEFT_INDEX_PROXIMAL to "LeftIndexProximal",
	BodyPart.LEFT_INDEX_INTERMEDIATE to "LeftIndexIntermediate",
	BodyPart.LEFT_INDEX_DISTAL to "LeftIndexDistal",
	BodyPart.LEFT_MIDDLE_PROXIMAL to "LeftMiddleProximal",
	BodyPart.LEFT_MIDDLE_INTERMEDIATE to "LeftMiddleIntermediate",
	BodyPart.LEFT_MIDDLE_DISTAL to "LeftMiddleDistal",
	BodyPart.LEFT_RING_PROXIMAL to "LeftRingProximal",
	BodyPart.LEFT_RING_INTERMEDIATE to "LeftRingIntermediate",
	BodyPart.LEFT_RING_DISTAL to "LeftRingDistal",
	BodyPart.LEFT_LITTLE_PROXIMAL to "LeftLittleProximal",
	BodyPart.LEFT_LITTLE_INTERMEDIATE to "LeftLittleIntermediate",
	BodyPart.LEFT_LITTLE_DISTAL to "LeftLittleDistal",
	BodyPart.RIGHT_THUMB_METACARPAL to "RightThumbProximal",
	BodyPart.RIGHT_THUMB_PROXIMAL to "RightThumbIntermediate",
	BodyPart.RIGHT_THUMB_DISTAL to "RightThumbDistal",
	BodyPart.RIGHT_INDEX_PROXIMAL to "RightIndexProximal",
	BodyPart.RIGHT_INDEX_INTERMEDIATE to "RightIndexIntermediate",
	BodyPart.RIGHT_INDEX_DISTAL to "RightIndexDistal",
	BodyPart.RIGHT_MIDDLE_PROXIMAL to "RightMiddleProximal",
	BodyPart.RIGHT_MIDDLE_INTERMEDIATE to "RightMiddleIntermediate",
	BodyPart.RIGHT_MIDDLE_DISTAL to "RightMiddleDistal",
	BodyPart.RIGHT_RING_PROXIMAL to "RightRingProximal",
	BodyPart.RIGHT_RING_INTERMEDIATE to "RightRingIntermediate",
	BodyPart.RIGHT_RING_DISTAL to "RightRingDistal",
	BodyPart.RIGHT_LITTLE_PROXIMAL to "RightLittleProximal",
	BodyPart.RIGHT_LITTLE_INTERMEDIATE to "RightLittleIntermediate",
	BodyPart.RIGHT_LITTLE_DISTAL to "RightLittleDistal",
)

// HIP-rooted hierarchy. VMC/Unity expects this; our skeleton is HEAD-rooted.
val VMC_HIERARCHY_MAP: Map<BodyPart, Array<BodyPart>> = mapOf(
	BodyPart.HIP to arrayOf(BodyPart.WAIST, BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG),
	BodyPart.WAIST to arrayOf(BodyPart.CHEST),
	BodyPart.CHEST to arrayOf(BodyPart.UPPER_CHEST),
	BodyPart.UPPER_CHEST to arrayOf(BodyPart.NECK),
	BodyPart.NECK to arrayOf(BodyPart.HEAD, BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER),
	BodyPart.LEFT_UPPER_LEG to arrayOf(BodyPart.LEFT_LOWER_LEG),
	BodyPart.RIGHT_UPPER_LEG to arrayOf(BodyPart.RIGHT_LOWER_LEG),
	BodyPart.LEFT_LOWER_LEG to arrayOf(BodyPart.LEFT_FOOT),
	BodyPart.RIGHT_LOWER_LEG to arrayOf(BodyPart.RIGHT_FOOT),
	BodyPart.LEFT_SHOULDER to arrayOf(BodyPart.LEFT_UPPER_ARM),
	BodyPart.RIGHT_SHOULDER to arrayOf(BodyPart.RIGHT_UPPER_ARM),
	BodyPart.LEFT_UPPER_ARM to arrayOf(BodyPart.LEFT_LOWER_ARM),
	BodyPart.RIGHT_UPPER_ARM to arrayOf(BodyPart.RIGHT_LOWER_ARM),
	BodyPart.LEFT_LOWER_ARM to arrayOf(BodyPart.LEFT_HAND),
	BodyPart.RIGHT_LOWER_ARM to arrayOf(BodyPart.RIGHT_HAND),
	BodyPart.LEFT_HAND to arrayOf(
		BodyPart.LEFT_THUMB_METACARPAL,
		BodyPart.LEFT_INDEX_PROXIMAL,
		BodyPart.LEFT_MIDDLE_PROXIMAL,
		BodyPart.LEFT_RING_PROXIMAL,
		BodyPart.LEFT_LITTLE_PROXIMAL,
	),
	BodyPart.LEFT_THUMB_METACARPAL to arrayOf(BodyPart.LEFT_THUMB_PROXIMAL),
	BodyPart.LEFT_THUMB_PROXIMAL to arrayOf(BodyPart.LEFT_THUMB_DISTAL),
	BodyPart.LEFT_INDEX_PROXIMAL to arrayOf(BodyPart.LEFT_INDEX_INTERMEDIATE),
	BodyPart.LEFT_INDEX_INTERMEDIATE to arrayOf(BodyPart.LEFT_INDEX_DISTAL),
	BodyPart.LEFT_MIDDLE_PROXIMAL to arrayOf(BodyPart.LEFT_MIDDLE_INTERMEDIATE),
	BodyPart.LEFT_MIDDLE_INTERMEDIATE to arrayOf(BodyPart.LEFT_MIDDLE_DISTAL),
	BodyPart.LEFT_RING_PROXIMAL to arrayOf(BodyPart.LEFT_RING_INTERMEDIATE),
	BodyPart.LEFT_RING_INTERMEDIATE to arrayOf(BodyPart.LEFT_RING_DISTAL),
	BodyPart.LEFT_LITTLE_PROXIMAL to arrayOf(BodyPart.LEFT_LITTLE_INTERMEDIATE),
	BodyPart.LEFT_LITTLE_INTERMEDIATE to arrayOf(BodyPart.LEFT_LITTLE_DISTAL),
	BodyPart.RIGHT_HAND to arrayOf(
		BodyPart.RIGHT_THUMB_METACARPAL,
		BodyPart.RIGHT_INDEX_PROXIMAL,
		BodyPart.RIGHT_MIDDLE_PROXIMAL,
		BodyPart.RIGHT_RING_PROXIMAL,
		BodyPart.RIGHT_LITTLE_PROXIMAL,
	),
	BodyPart.RIGHT_THUMB_METACARPAL to arrayOf(BodyPart.RIGHT_THUMB_PROXIMAL),
	BodyPart.RIGHT_THUMB_PROXIMAL to arrayOf(BodyPart.RIGHT_THUMB_DISTAL),
	BodyPart.RIGHT_INDEX_PROXIMAL to arrayOf(BodyPart.RIGHT_INDEX_INTERMEDIATE),
	BodyPart.RIGHT_INDEX_INTERMEDIATE to arrayOf(BodyPart.RIGHT_INDEX_DISTAL),
	BodyPart.RIGHT_MIDDLE_PROXIMAL to arrayOf(BodyPart.RIGHT_MIDDLE_INTERMEDIATE),
	BodyPart.RIGHT_MIDDLE_INTERMEDIATE to arrayOf(BodyPart.RIGHT_MIDDLE_DISTAL),
	BodyPart.RIGHT_RING_PROXIMAL to arrayOf(BodyPart.RIGHT_RING_INTERMEDIATE),
	BodyPart.RIGHT_RING_INTERMEDIATE to arrayOf(BodyPart.RIGHT_RING_DISTAL),
	BodyPart.RIGHT_LITTLE_PROXIMAL to arrayOf(BodyPart.RIGHT_LITTLE_INTERMEDIATE),
	BodyPart.RIGHT_LITTLE_INTERMEDIATE to arrayOf(BodyPart.RIGHT_LITTLE_DISTAL),
)

private suspend fun SequenceScope<Pair<BodyPart?, BodyPart>>.visitVMC(parent: BodyPart?, bone: BodyPart) {
	yield(parent to bone)
	VMC_HIERARCHY_MAP[bone]?.forEach { visitVMC(bone, it) }
}

fun iterateVMCHierarchy() = sequence { visitVMC(null, BodyPart.HIP) }

val VMC_BONE_PARENTS: Map<BodyPart, BodyPart?> =
	iterateVMCHierarchy().associate { (parent, child) -> child to parent }

// Per-bone rest offset, subtracted from the live world rotation before computing the VMC local.
// Foot cancels R(90deg,X) baked into DEFAULT_SKELETON_STATE. Arms remap our hanging rest (NEG_Y)
// to the VRM rig's T-pose rest direction so the avatar isn't stuck at T regardless of our pose.
val VMC_REST_ROTATIONS: Map<BodyPart, Quaternion> = run {
	val leftArm = Quaternion.rotationAroundZAxis(-FastMath.HALF_PI)
	val rightArm = Quaternion.rotationAroundZAxis(FastMath.HALF_PI)
	val foot = Quaternion.rotationAroundXAxis(FastMath.HALF_PI)
	val leftFingers = listOf(
		BodyPart.LEFT_THUMB_METACARPAL, BodyPart.LEFT_THUMB_PROXIMAL, BodyPart.LEFT_THUMB_DISTAL,
		BodyPart.LEFT_INDEX_PROXIMAL, BodyPart.LEFT_INDEX_INTERMEDIATE, BodyPart.LEFT_INDEX_DISTAL,
		BodyPart.LEFT_MIDDLE_PROXIMAL, BodyPart.LEFT_MIDDLE_INTERMEDIATE, BodyPart.LEFT_MIDDLE_DISTAL,
		BodyPart.LEFT_RING_PROXIMAL, BodyPart.LEFT_RING_INTERMEDIATE, BodyPart.LEFT_RING_DISTAL,
		BodyPart.LEFT_LITTLE_PROXIMAL, BodyPart.LEFT_LITTLE_INTERMEDIATE, BodyPart.LEFT_LITTLE_DISTAL,
	)
	val rightFingers = listOf(
		BodyPart.RIGHT_THUMB_METACARPAL, BodyPart.RIGHT_THUMB_PROXIMAL, BodyPart.RIGHT_THUMB_DISTAL,
		BodyPart.RIGHT_INDEX_PROXIMAL, BodyPart.RIGHT_INDEX_INTERMEDIATE, BodyPart.RIGHT_INDEX_DISTAL,
		BodyPart.RIGHT_MIDDLE_PROXIMAL, BodyPart.RIGHT_MIDDLE_INTERMEDIATE, BodyPart.RIGHT_MIDDLE_DISTAL,
		BodyPart.RIGHT_RING_PROXIMAL, BodyPart.RIGHT_RING_INTERMEDIATE, BodyPart.RIGHT_RING_DISTAL,
		BodyPart.RIGHT_LITTLE_PROXIMAL, BodyPart.RIGHT_LITTLE_INTERMEDIATE, BodyPart.RIGHT_LITTLE_DISTAL,
	)
	mapOf(
		BodyPart.LEFT_FOOT to foot,
		BodyPart.RIGHT_FOOT to foot,
		BodyPart.LEFT_UPPER_ARM to leftArm,
		BodyPart.LEFT_LOWER_ARM to leftArm,
		BodyPart.LEFT_HAND to leftArm,
		BodyPart.RIGHT_UPPER_ARM to rightArm,
		BodyPart.RIGHT_LOWER_ARM to rightArm,
		BodyPart.RIGHT_HAND to rightArm,
	) + leftFingers.associateWith { leftArm } + rightFingers.associateWith { rightArm }
}

private fun restAdjustedWorld(bone: BoneState): Quaternion {
	val rest = VMC_REST_ROTATIONS[bone.bodyPart] ?: return bone.rotation
	return bone.rotation * rest.inv()
}

// Local rotation relative to the VMC parent. Rest offset is applied to both bone and parent
// before taking the relative rotation. Parent is explicit because VMC hierarchy differs from
// the skeleton's head-rooted parentBone.
fun vmcLocalRotation(bone: BoneState, parent: BoneState?): Quaternion {
	val adjusted = restAdjustedWorld(bone)
	if (parent == null) return adjusted
	return restAdjustedWorld(parent).inv() * adjusted
}

// Local position relative to the VMC parent. Overrides the VRM bind-pose offset so each bone
// lands at our skeleton's world position when the receiver honors it. HIP root is handled by
// the caller since the root has no parent in VMC.
fun vmcLocalPosition(bone: BoneState, parent: BoneState): Vector3 {
	val parentAdjusted = restAdjustedWorld(parent)
	return parentAdjusted.inv().sandwich(bone.headPosition - parent.headPosition)
}

