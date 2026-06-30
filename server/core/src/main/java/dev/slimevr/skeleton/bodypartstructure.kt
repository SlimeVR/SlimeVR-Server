package dev.slimevr.skeleton

import solarxr_protocol.datatypes.BodyPart

val BODY_PART_HIERARCHY_MAP = mapOf(
	BodyPart.HEAD to arrayOf(BodyPart.NECK),
	BodyPart.NECK to arrayOf(BodyPart.UPPER_CHEST, BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER),

	BodyPart.LEFT_SHOULDER to arrayOf(BodyPart.LEFT_UPPER_ARM),
	BodyPart.LEFT_UPPER_ARM to arrayOf(BodyPart.LEFT_LOWER_ARM),
	BodyPart.LEFT_LOWER_ARM to arrayOf(BodyPart.LEFT_HAND),

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

	BodyPart.RIGHT_SHOULDER to arrayOf(BodyPart.RIGHT_UPPER_ARM),
	BodyPart.RIGHT_UPPER_ARM to arrayOf(BodyPart.RIGHT_LOWER_ARM),
	BodyPart.RIGHT_LOWER_ARM to arrayOf(BodyPart.RIGHT_HAND),

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

	BodyPart.UPPER_CHEST to arrayOf(BodyPart.CHEST),
	BodyPart.CHEST to arrayOf(BodyPart.WAIST),
	BodyPart.WAIST to arrayOf(BodyPart.HIP),
	BodyPart.HIP to arrayOf(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP),

	BodyPart.LEFT_HIP to arrayOf(BodyPart.LEFT_UPPER_LEG),
	BodyPart.LEFT_UPPER_LEG to arrayOf(BodyPart.LEFT_LOWER_LEG),
	BodyPart.LEFT_LOWER_LEG to arrayOf(BodyPart.LEFT_FOOT),

	BodyPart.RIGHT_HIP to arrayOf(BodyPart.RIGHT_UPPER_LEG),
	BodyPart.RIGHT_UPPER_LEG to arrayOf(BodyPart.RIGHT_LOWER_LEG),
	BodyPart.RIGHT_LOWER_LEG to arrayOf(BodyPart.RIGHT_FOOT),
)

private suspend fun SequenceScope<Pair<BodyPart?, BodyPart>>.visitBodyPart(parentBone: BodyPart?, bone: BodyPart, onlyChildren: Boolean) {
	if (!onlyChildren) {
		yield(Pair(parentBone, bone))
	}
	val children = BODY_PART_HIERARCHY_MAP[bone] ?: return
	for (child in children) visitBodyPart(bone, child, false)
}

fun iterateBodyPartHierarchy(root: BodyPart = BodyPart.HEAD, onlyChildren: Boolean = false) = sequence {
	visitBodyPart(null, root, onlyChildren)
}
