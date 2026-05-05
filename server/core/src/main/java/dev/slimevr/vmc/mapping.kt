package dev.slimevr.vmc

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
)

// HIP-rooted hierarchy for VMC. The new skeleton is HEAD-rooted but VMC/Unity expects HIP-rooted.
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
)

private suspend fun SequenceScope<Pair<BodyPart?, BodyPart>>.visitVMC(parent: BodyPart?, bone: BodyPart) {
	yield(parent to bone)
	VMC_HIERARCHY_MAP[bone]?.forEach { visitVMC(bone, it) }
}

fun iterateVMCHierarchy() = sequence { visitVMC(null, BodyPart.HIP) }
