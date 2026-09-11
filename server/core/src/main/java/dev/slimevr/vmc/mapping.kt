package dev.slimevr.vmc

import com.jme3.math.FastMath
import dev.slimevr.resets.ResetBodyParts
import dev.slimevr.skeleton.BodyPartMap
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

// TODO how to handle UPPER_WAIST?
val BODY_PART_TO_UNITY_BONE: BodyPartMap<Array<String>> = BodyPartMap(
	mapOf(
		BodyPart.HEAD to arrayOf("Head"),
		BodyPart.NECK to arrayOf("Neck"),
		BodyPart.UPPER_CHEST to arrayOf("UpperChest"),
		BodyPart.LOWER_CHEST to arrayOf("Chest"),
		BodyPart.UPPER_WAIST to arrayOf("Spine"),
		BodyPart.HIP to arrayOf("Hips"),
		BodyPart.LEFT_SHOULDER to arrayOf("LeftShoulder"),
		BodyPart.RIGHT_SHOULDER to arrayOf("RightShoulder"),
		BodyPart.LEFT_UPPER_ARM to arrayOf("LeftUpperArm"),
		BodyPart.RIGHT_UPPER_ARM to arrayOf("RightUpperArm"),
		BodyPart.LEFT_LOWER_ARM to arrayOf("LeftLowerArm"),
		BodyPart.RIGHT_LOWER_ARM to arrayOf("RightLowerArm"),
		BodyPart.LEFT_HAND to arrayOf("LeftHand"),
		BodyPart.RIGHT_HAND to arrayOf("RightHand"),
		BodyPart.LEFT_UPPER_LEG to arrayOf("LeftUpperLeg"),
		BodyPart.RIGHT_UPPER_LEG to arrayOf("RightUpperLeg"),
		BodyPart.LEFT_LOWER_LEG to arrayOf("LeftLowerLeg"),
		BodyPart.RIGHT_LOWER_LEG to arrayOf("RightLowerLeg"),
		BodyPart.LEFT_FOOT to arrayOf("LeftFoot"),
		BodyPart.RIGHT_FOOT to arrayOf("RightFoot"),
		BodyPart.LEFT_THUMB_METACARPAL to arrayOf("LeftThumbProximal"),
		BodyPart.LEFT_THUMB_PROXIMAL to arrayOf("LeftThumbIntermediate"),
		BodyPart.LEFT_THUMB_DISTAL to arrayOf("LeftThumbDistal"),
		BodyPart.LEFT_INDEX_PROXIMAL to arrayOf("LeftIndexProximal"),
		BodyPart.LEFT_INDEX_INTERMEDIATE to arrayOf("LeftIndexIntermediate"),
		BodyPart.LEFT_INDEX_DISTAL to arrayOf("LeftIndexDistal"),
		BodyPart.LEFT_MIDDLE_PROXIMAL to arrayOf("LeftMiddleProximal"),
		BodyPart.LEFT_MIDDLE_INTERMEDIATE to arrayOf("LeftMiddleIntermediate"),
		BodyPart.LEFT_MIDDLE_DISTAL to arrayOf("LeftMiddleDistal"),
		BodyPart.LEFT_RING_PROXIMAL to arrayOf("LeftRingProximal"),
		BodyPart.LEFT_RING_INTERMEDIATE to arrayOf("LeftRingIntermediate"),
		BodyPart.LEFT_RING_DISTAL to arrayOf("LeftRingDistal"),
		BodyPart.LEFT_LITTLE_PROXIMAL to arrayOf("LeftLittleProximal"),
		BodyPart.LEFT_LITTLE_INTERMEDIATE to arrayOf("LeftLittleIntermediate"),
		BodyPart.LEFT_LITTLE_DISTAL to arrayOf("LeftLittleDistal"),
		BodyPart.RIGHT_THUMB_METACARPAL to arrayOf("RightThumbProximal"),
		BodyPart.RIGHT_THUMB_PROXIMAL to arrayOf("RightThumbIntermediate"),
		BodyPart.RIGHT_THUMB_DISTAL to arrayOf("RightThumbDistal"),
		BodyPart.RIGHT_INDEX_PROXIMAL to arrayOf("RightIndexProximal"),
		BodyPart.RIGHT_INDEX_INTERMEDIATE to arrayOf("RightIndexIntermediate"),
		BodyPart.RIGHT_INDEX_DISTAL to arrayOf("RightIndexDistal"),
		BodyPart.RIGHT_MIDDLE_PROXIMAL to arrayOf("RightMiddleProximal"),
		BodyPart.RIGHT_MIDDLE_INTERMEDIATE to arrayOf("RightMiddleIntermediate"),
		BodyPart.RIGHT_MIDDLE_DISTAL to arrayOf("RightMiddleDistal"),
		BodyPart.RIGHT_RING_PROXIMAL to arrayOf("RightRingProximal"),
		BodyPart.RIGHT_RING_INTERMEDIATE to arrayOf("RightRingIntermediate"),
		BodyPart.RIGHT_RING_DISTAL to arrayOf("RightRingDistal"),
		BodyPart.RIGHT_LITTLE_PROXIMAL to arrayOf("RightLittleProximal"),
		BodyPart.RIGHT_LITTLE_INTERMEDIATE to arrayOf("RightLittleIntermediate"),
		BodyPart.RIGHT_LITTLE_DISTAL to arrayOf("RightLittleDistal"),
		BodyPart.LEFT_BIG_TOE to arrayOf("LeftToes", "LeftBigToe"),
		BodyPart.LEFT_INDEX_TOE to arrayOf("LeftIndexToe"),
		BodyPart.LEFT_MIDDLE_TOE to arrayOf("LeftMiddleToe"),
		BodyPart.LEFT_RING_TOE to arrayOf("LeftRingToe"),
		BodyPart.LEFT_LITTLE_TOE to arrayOf("LeftLittleToe"),
		BodyPart.RIGHT_BIG_TOE to arrayOf("RightToes", "RightBigToe"),
		BodyPart.RIGHT_INDEX_TOE to arrayOf("RightIndexToe"),
		BodyPart.RIGHT_MIDDLE_TOE to arrayOf("RightMiddleToe"),
		BodyPart.RIGHT_RING_TOE to arrayOf("RightRingToe"),
		BodyPart.RIGHT_LITTLE_TOE to arrayOf("RightLittleToe"),
	),
)

// Bones VMC can accept. Used by the routing module.
val VMC_SUPPORTED_BONES: Set<BodyPart> = BODY_PART_TO_UNITY_BONE.keys

// HIP-rooted hierarchy. VMC/Unity expects this; our skeleton is HEAD-rooted.
// TODO Test with a model that has UPPER_CHEST
//  and figure out how to deal with it (check if present in VRM?)
val VMC_HIERARCHY_MAP: BodyPartMap<Array<BodyPart>> = BodyPartMap(
	mapOf(
		BodyPart.HIP to arrayOf(BodyPart.UPPER_WAIST, BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG),
		BodyPart.UPPER_WAIST to arrayOf(BodyPart.LOWER_CHEST),
		BodyPart.LOWER_CHEST to arrayOf(BodyPart.UPPER_CHEST, BodyPart.NECK, BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER),
		BodyPart.NECK to arrayOf(BodyPart.HEAD),
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
		BodyPart.LEFT_FOOT to arrayOf(
			BodyPart.LEFT_BIG_TOE,
			BodyPart.LEFT_INDEX_TOE,
			BodyPart.LEFT_MIDDLE_TOE,
			BodyPart.LEFT_RING_TOE,
			BodyPart.LEFT_LITTLE_TOE,
		),
		BodyPart.RIGHT_FOOT to arrayOf(
			BodyPart.RIGHT_BIG_TOE,
			BodyPart.RIGHT_INDEX_TOE,
			BodyPart.RIGHT_MIDDLE_TOE,
			BodyPart.RIGHT_RING_TOE,
			BodyPart.RIGHT_LITTLE_TOE,
		),
	),
)

private class VmcBoneTree(hierarchy: BodyPartMap<Array<BodyPart>>) {
	val order: List<BodyPart>
	val parents: BodyPartMap<BodyPart?>

	init {
		fun visit(parent: BodyPart?, bone: BodyPart, into: MutableList<Pair<BodyPart?, BodyPart>>) {
			into.add(parent to bone)
			hierarchy[bone]?.forEach { visit(bone, it, into) }
		}
		val traversal = buildList { visit(null, BodyPart.HIP, this) }
		order = traversal.map { (_, bone) -> bone }
		parents = BodyPartMap(traversal.associate { (parent, child) -> child to parent })
	}
}

val VMC_OUTPUT_BONE_PARENTS: BodyPartMap<BodyPart?> = VmcBoneTree(VMC_HIERARCHY_MAP).parents

val VMC_INPUT_HIERARCHY_MAP: BodyPartMap<Array<BodyPart>> = BodyPartMap(
	VMC_HIERARCHY_MAP +
		mapOf(
			BodyPart.LOWER_CHEST to arrayOf(BodyPart.UPPER_CHEST),
			BodyPart.UPPER_CHEST to arrayOf(BodyPart.NECK, BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER),
		),
)
private val vmcInputTree = VmcBoneTree(VMC_INPUT_HIERARCHY_MAP)
val VMC_INPUT_BONE_ORDER: List<BodyPart> = vmcInputTree.order
val VMC_INPUT_BONE_PARENTS: BodyPartMap<BodyPart?> = vmcInputTree.parents

val UNITY_BONE_TO_BODY_PART: Map<String, BodyPart> = BODY_PART_TO_UNITY_BONE.entries
	.flatMap { (bodyPart, names) -> names.map { name -> name.lowercase() to bodyPart } }
	.toMap()

val VMC_MIRROR_BONE_PAIRS: List<Pair<BodyPart, BodyPart>> = listOf(
	BodyPart.LEFT_SHOULDER to BodyPart.RIGHT_SHOULDER,
	BodyPart.LEFT_UPPER_ARM to BodyPart.RIGHT_UPPER_ARM,
	BodyPart.LEFT_LOWER_ARM to BodyPart.RIGHT_LOWER_ARM,
	BodyPart.LEFT_HAND to BodyPart.RIGHT_HAND,
	BodyPart.LEFT_UPPER_LEG to BodyPart.RIGHT_UPPER_LEG,
	BodyPart.LEFT_LOWER_LEG to BodyPart.RIGHT_LOWER_LEG,
	BodyPart.LEFT_FOOT to BodyPart.RIGHT_FOOT,
	BodyPart.LEFT_THUMB_METACARPAL to BodyPart.RIGHT_THUMB_METACARPAL,
	BodyPart.LEFT_THUMB_PROXIMAL to BodyPart.RIGHT_THUMB_PROXIMAL,
	BodyPart.LEFT_THUMB_DISTAL to BodyPart.RIGHT_THUMB_DISTAL,
	BodyPart.LEFT_INDEX_PROXIMAL to BodyPart.RIGHT_INDEX_PROXIMAL,
	BodyPart.LEFT_INDEX_INTERMEDIATE to BodyPart.RIGHT_INDEX_INTERMEDIATE,
	BodyPart.LEFT_INDEX_DISTAL to BodyPart.RIGHT_INDEX_DISTAL,
	BodyPart.LEFT_MIDDLE_PROXIMAL to BodyPart.RIGHT_MIDDLE_PROXIMAL,
	BodyPart.LEFT_MIDDLE_INTERMEDIATE to BodyPart.RIGHT_MIDDLE_INTERMEDIATE,
	BodyPart.LEFT_MIDDLE_DISTAL to BodyPart.RIGHT_MIDDLE_DISTAL,
	BodyPart.LEFT_RING_PROXIMAL to BodyPart.RIGHT_RING_PROXIMAL,
	BodyPart.LEFT_RING_INTERMEDIATE to BodyPart.RIGHT_RING_INTERMEDIATE,
	BodyPart.LEFT_RING_DISTAL to BodyPart.RIGHT_RING_DISTAL,
	BodyPart.LEFT_LITTLE_PROXIMAL to BodyPart.RIGHT_LITTLE_PROXIMAL,
	BodyPart.LEFT_LITTLE_INTERMEDIATE to BodyPart.RIGHT_LITTLE_INTERMEDIATE,
	BodyPart.LEFT_LITTLE_DISTAL to BodyPart.RIGHT_LITTLE_DISTAL,
	BodyPart.LEFT_BIG_TOE to BodyPart.RIGHT_BIG_TOE,
	BodyPart.LEFT_BIG_TOE to BodyPart.RIGHT_BIG_TOE,
	BodyPart.LEFT_INDEX_TOE to BodyPart.RIGHT_INDEX_TOE,
	BodyPart.LEFT_MIDDLE_TOE to BodyPart.RIGHT_MIDDLE_TOE,
	BodyPart.LEFT_RING_TOE to BodyPart.RIGHT_RING_TOE,
	BodyPart.LEFT_LITTLE_TOE to BodyPart.RIGHT_LITTLE_TOE,
)

val VMC_MIRROR_BONES: BodyPartMap<BodyPart> = BodyPartMap(
	VMC_MIRROR_BONE_PAIRS
		.flatMap { (left, right) -> listOf(left to right, right to left) }
		.toMap(),
)

fun vmcMirrorSource(bodyPart: BodyPart): BodyPart = VMC_MIRROR_BONES[bodyPart] ?: bodyPart

// Per-bone rest offset, subtracted from the live world rotation before computing the VMC local.
// Arms remap our hanging rest (NEG_Y) to the VRM rig's T-pose rest direction so the avatar isn't stuck at T regardless of our pose.
val VMC_REST_ROTATIONS: BodyPartMap<Quaternion> = run {
	val leftArm = Quaternion.rotationAroundZAxis(-FastMath.HALF_PI)
	val rightArm = Quaternion.rotationAroundZAxis(FastMath.HALF_PI)
	BodyPartMap(
		mapOf(
			BodyPart.LEFT_UPPER_ARM to leftArm,
			BodyPart.LEFT_LOWER_ARM to leftArm,
			BodyPart.LEFT_HAND to leftArm,
			BodyPart.RIGHT_UPPER_ARM to rightArm,
			BodyPart.RIGHT_LOWER_ARM to rightArm,
			BodyPart.RIGHT_HAND to rightArm,
		) +
			ResetBodyParts.LEFT_FINGERS.associateWith { leftArm } +
			ResetBodyParts.RIGHT_FINGERS.associateWith { rightArm },
	)
}

fun vmcMirrorPosition(pos: Vector3): Vector3 = Vector3(-pos.x, pos.y, pos.z)

fun vmcMirrorRotation(rot: Quaternion): Quaternion = Quaternion(rot.w, rot.x, -rot.y, -rot.z)
