package dev.slimevr.vmc

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import java.util.EnumMap

// Bones VMC can accept. Used by the routing module.
val VMC_SUPPORTED_BONES: Set<BodyPart> = UnityBone.entries.flatMap { unityBone -> unityBone.bodyParts.map { it } }.toSet()

// HIP-rooted hierarchy. VMC/Unity expects this; our skeleton is HEAD-rooted.
val VMC_HIERARCHY: EnumMap<UnityBone, Array<UnityBone>> = EnumMap(
	mapOf(
		UnityBone.HIPS to arrayOf(UnityBone.LEFT_UPPER_LEG, UnityBone.RIGHT_UPPER_LEG, UnityBone.SPINE),
		UnityBone.SPINE to arrayOf(UnityBone.CHEST),
		UnityBone.CHEST to arrayOf(UnityBone.UPPER_CHEST),
		UnityBone.UPPER_CHEST to arrayOf(UnityBone.LEFT_SHOULDER, UnityBone.RIGHT_SHOULDER, UnityBone.NECK),
		UnityBone.NECK to arrayOf(UnityBone.HEAD),
		UnityBone.LEFT_UPPER_LEG to arrayOf(UnityBone.LEFT_LOWER_LEG),
		UnityBone.RIGHT_UPPER_LEG to arrayOf(UnityBone.RIGHT_LOWER_LEG),
		UnityBone.LEFT_LOWER_LEG to arrayOf(UnityBone.LEFT_FOOT),
		UnityBone.RIGHT_LOWER_LEG to arrayOf(UnityBone.RIGHT_FOOT),
		UnityBone.LEFT_SHOULDER to arrayOf(UnityBone.LEFT_UPPER_ARM),
		UnityBone.RIGHT_SHOULDER to arrayOf(UnityBone.RIGHT_UPPER_ARM),
		UnityBone.LEFT_UPPER_ARM to arrayOf(UnityBone.LEFT_LOWER_ARM),
		UnityBone.RIGHT_UPPER_ARM to arrayOf(UnityBone.RIGHT_LOWER_ARM),
		UnityBone.LEFT_LOWER_ARM to arrayOf(UnityBone.LEFT_HAND),
		UnityBone.RIGHT_LOWER_ARM to arrayOf(UnityBone.RIGHT_HAND),
		UnityBone.LEFT_HAND to arrayOf(
			UnityBone.LEFT_THUMB_PROXIMAL,
			UnityBone.LEFT_INDEX_PROXIMAL,
			UnityBone.LEFT_MIDDLE_PROXIMAL,
			UnityBone.LEFT_RING_PROXIMAL,
			UnityBone.LEFT_LITTLE_PROXIMAL,
		),
		UnityBone.LEFT_THUMB_PROXIMAL to arrayOf(UnityBone.LEFT_THUMB_INTERMEDIATE),
		UnityBone.LEFT_THUMB_INTERMEDIATE to arrayOf(UnityBone.LEFT_THUMB_DISTAL),
		UnityBone.LEFT_INDEX_PROXIMAL to arrayOf(UnityBone.LEFT_INDEX_INTERMEDIATE),
		UnityBone.LEFT_INDEX_INTERMEDIATE to arrayOf(UnityBone.LEFT_INDEX_DISTAL),
		UnityBone.LEFT_MIDDLE_PROXIMAL to arrayOf(UnityBone.LEFT_MIDDLE_INTERMEDIATE),
		UnityBone.LEFT_MIDDLE_INTERMEDIATE to arrayOf(UnityBone.LEFT_MIDDLE_DISTAL),
		UnityBone.LEFT_RING_PROXIMAL to arrayOf(UnityBone.LEFT_RING_INTERMEDIATE),
		UnityBone.LEFT_RING_INTERMEDIATE to arrayOf(UnityBone.LEFT_RING_DISTAL),
		UnityBone.LEFT_LITTLE_PROXIMAL to arrayOf(UnityBone.LEFT_LITTLE_INTERMEDIATE),
		UnityBone.LEFT_LITTLE_INTERMEDIATE to arrayOf(UnityBone.LEFT_LITTLE_DISTAL),
		UnityBone.RIGHT_HAND to arrayOf(
			UnityBone.RIGHT_THUMB_PROXIMAL,
			UnityBone.RIGHT_INDEX_PROXIMAL,
			UnityBone.RIGHT_MIDDLE_PROXIMAL,
			UnityBone.RIGHT_RING_PROXIMAL,
			UnityBone.RIGHT_LITTLE_PROXIMAL,
		),
		UnityBone.RIGHT_THUMB_PROXIMAL to arrayOf(UnityBone.RIGHT_THUMB_INTERMEDIATE),
		UnityBone.RIGHT_THUMB_INTERMEDIATE to arrayOf(UnityBone.RIGHT_THUMB_DISTAL),
		UnityBone.RIGHT_INDEX_PROXIMAL to arrayOf(UnityBone.RIGHT_INDEX_INTERMEDIATE),
		UnityBone.RIGHT_INDEX_INTERMEDIATE to arrayOf(UnityBone.RIGHT_INDEX_DISTAL),
		UnityBone.RIGHT_MIDDLE_PROXIMAL to arrayOf(UnityBone.RIGHT_MIDDLE_INTERMEDIATE),
		UnityBone.RIGHT_MIDDLE_INTERMEDIATE to arrayOf(UnityBone.RIGHT_MIDDLE_DISTAL),
		UnityBone.RIGHT_RING_PROXIMAL to arrayOf(UnityBone.RIGHT_RING_INTERMEDIATE),
		UnityBone.RIGHT_RING_INTERMEDIATE to arrayOf(UnityBone.RIGHT_RING_DISTAL),
		UnityBone.RIGHT_LITTLE_PROXIMAL to arrayOf(UnityBone.RIGHT_LITTLE_INTERMEDIATE),
		UnityBone.RIGHT_LITTLE_INTERMEDIATE to arrayOf(UnityBone.RIGHT_LITTLE_DISTAL),
		UnityBone.LEFT_FOOT to arrayOf(
			UnityBone.LEFT_BIG_TOE,
			UnityBone.LEFT_INDEX_TOE,
			UnityBone.LEFT_MIDDLE_TOE,
			UnityBone.LEFT_RING_TOE,
			UnityBone.LEFT_LITTLE_TOE,
		),
		UnityBone.RIGHT_FOOT to arrayOf(
			UnityBone.RIGHT_BIG_TOE,
			UnityBone.RIGHT_INDEX_TOE,
			UnityBone.RIGHT_MIDDLE_TOE,
			UnityBone.RIGHT_RING_TOE,
			UnityBone.RIGHT_LITTLE_TOE,
		),
	),
)

private class VmcBoneTree(hierarchy: EnumMap<UnityBone, Array<UnityBone>>) {
	val order: List<UnityBone>
	val parents: EnumMap<UnityBone, UnityBone?>

	init {
		fun visit(parent: UnityBone?, bone: UnityBone, into: MutableList<Pair<UnityBone?, UnityBone>>) {
			into.add(parent to bone)
			hierarchy[bone]?.forEach { visit(bone, it, into) }
		}
		val traversal = buildList { visit(null, UnityBone.HIPS, this) }
		order = traversal.map { (_, bone) -> bone }
		parents = EnumMap(traversal.associate { (parent, child) -> child to parent })
	}
}

private val VMC_TREE = VmcBoneTree(VMC_HIERARCHY)
val VMC_BONE_ORDER: List<UnityBone> = VMC_TREE.order
val VMC_BONE_PARENTS: EnumMap<UnityBone, UnityBone?> = VMC_TREE.parents

// Bones between the hips and the neck, hips first, as VMC's hierarchy links them.
val VMC_HIP_TO_NECK_CHAIN: List<UnityBone> = generateSequence(UnityBone.NECK) { VMC_BONE_PARENTS[it] }
	.takeWhile { it != UnityBone.HIPS }
	.toList()
	.asReversed()

// Spine bones between the hips and the neck in the actual skeleton hierarchy
// Used to derive a rest hip height above the floor when anchored with no VRM loaded.
val SPINE_CHAIN_ABOVE_HIP: List<UnityBone> = listOf(
	UnityBone.NECK,
	UnityBone.UPPER_CHEST,
	UnityBone.CHEST,
	UnityBone.SPINE,
)

fun getAllChildren(bone: UnityBone, includeSelf: Boolean): List<UnityBone> {
	val children = VMC_HIERARCHY[bone]?.flatMap {
		listOf(it) + getAllChildren(it, false)
	}.orEmpty()
	if (includeSelf) return listOf(bone) + children
	return children
}

// Per-bone rest offset, subtracted from the live world rotation before computing the VMC local.
// Arms remap our hanging rest (NEG_Y) to the VRM rig's T-pose rest direction so the avatar isn't stuck at T regardless of our pose.
val VMC_REST_ROTATIONS: EnumMap<UnityBone, Quaternion> = run {
	val leftArmOffset = Quaternion.rotationAroundZAxis(-FastMath.HALF_PI)
	val rightArmOffset = Quaternion.rotationAroundZAxis(FastMath.HALF_PI)
	EnumMap(
		getAllChildren(UnityBone.LEFT_UPPER_ARM, true).associateWith { leftArmOffset } +
			getAllChildren(UnityBone.RIGHT_UPPER_ARM, true).associateWith { rightArmOffset },
	)
}

fun vmcMirrorPosition(pos: Vector3): Vector3 = Vector3(-pos.x, pos.y, pos.z)

fun vmcMirrorRotation(rot: Quaternion): Quaternion = Quaternion(rot.w, rot.x, -rot.y, -rot.z)
