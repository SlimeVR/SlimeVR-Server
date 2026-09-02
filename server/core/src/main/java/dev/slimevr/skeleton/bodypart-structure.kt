package dev.slimevr.skeleton

import solarxr_protocol.datatypes.BodyPart

val BODY_PART_HIERARCHY_MAP: BodyPartMap<Array<BodyPart>> = BodyPartMap(
	mapOf(
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

		BodyPart.CHEST to arrayOf(
			BodyPart.LEFT_BUST,
			BodyPart.RIGHT_BUST
		)
	),
)

private val BODY_PART_PARENTS: Array<BodyPart?> = arrayOfNulls<BodyPart>(BodyPart.entries.size).also { parents ->
	for ((parent, children) in BODY_PART_HIERARCHY_MAP) {
		for (child in children) parents[child.ordinal] = parent
	}
}

fun parentOf(bodyPart: BodyPart): BodyPart? = BODY_PART_PARENTS[bodyPart.ordinal]

inline fun BodyPart.findFirstParent(predicate: (BodyPart) -> Boolean): BodyPart? {
	var current = parentOf(this)
	while (current != null) {
		if (predicate(current)) return current
		current = parentOf(current)
	}
	return null
}

val headPartSet = setOf(BodyPart.HEAD)
fun highestBodyParts(bodyParts: Set<BodyPart>): Set<BodyPart> {
	if (BodyPart.HEAD in bodyParts) return headPartSet
	val result = bodyParts.toMutableSet()
	for (bodyPart in bodyParts) {
		var parent = parentOf(bodyPart)
		while (parent != null) {
			if (parent in bodyParts) {
				result.remove(bodyPart)
				break
			}
			parent = parentOf(parent)
		}
	}
	return result
}

private fun buildHierarchy(root: BodyPart, onlyChildren: Boolean): List<Pair<BodyPart?, BodyPart>> {
	val result = mutableListOf<Pair<BodyPart?, BodyPart>>()
	fun visit(parentBone: BodyPart?, bone: BodyPart, skipSelf: Boolean) {
		if (!skipSelf) result += parentBone to bone
		val children = BODY_PART_HIERARCHY_MAP[bone] ?: return
		for (child in children) visit(bone, child, false)
	}
	visit(null, root, onlyChildren)
	return result
}

// The hierarchy is constant, so every traversal of it is too. Derived once for the same reason
// BODY_PART_PARENTS is: walking it lazily per call cost a continuation and a Pair per bone, and
// BoneYawFallbackProcessor asks for one per active bone per frame.
private val BODY_PART_HIERARCHIES: Array<List<Pair<BodyPart?, BodyPart>>> =
	Array(ALL_BODY_PARTS.size * 2) { index -> buildHierarchy(ALL_BODY_PARTS[index / 2], index % 2 == 1) }

fun iterateBodyPartHierarchy(
	root: BodyPart = BodyPart.HEAD,
	onlyChildren: Boolean = false,
): List<Pair<BodyPart?, BodyPart>> = BODY_PART_HIERARCHIES[root.ordinal * 2 + if (onlyChildren) 1 else 0]
