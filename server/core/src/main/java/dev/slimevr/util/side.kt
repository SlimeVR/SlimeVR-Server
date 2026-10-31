package dev.slimevr.util

import dev.slimevr.skeleton.BodyPartMap
import solarxr_protocol.datatypes.BodyPart

/**
 * Used to represent the side of the skeleton a bone is on.
 */
enum class Side {
	LEFT,
	RIGHT,
}

val Side.opposite: Side
	get() = when (this) {
		Side.LEFT -> Side.RIGHT
		Side.RIGHT -> Side.LEFT
	}

private val SIDED_BODY_PART_PAIRS: List<Pair<BodyPart, BodyPart>> = listOf(
	BodyPart.LEFT_UPPER_LEG to BodyPart.RIGHT_UPPER_LEG,
	BodyPart.LEFT_LOWER_LEG to BodyPart.RIGHT_LOWER_LEG,
	BodyPart.LEFT_FOOT to BodyPart.RIGHT_FOOT,
	BodyPart.LEFT_UPPER_ARM to BodyPart.RIGHT_UPPER_ARM,
	BodyPart.LEFT_LOWER_ARM to BodyPart.RIGHT_LOWER_ARM,
	BodyPart.LEFT_HAND to BodyPart.RIGHT_HAND,
	BodyPart.LEFT_SHOULDER to BodyPart.RIGHT_SHOULDER,
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
	BodyPart.LEFT_INDEX_TOE to BodyPart.RIGHT_INDEX_TOE,
	BodyPart.LEFT_MIDDLE_TOE to BodyPart.RIGHT_MIDDLE_TOE,
	BodyPart.LEFT_RING_TOE to BodyPart.RIGHT_RING_TOE,
	BodyPart.LEFT_LITTLE_TOE to BodyPart.RIGHT_LITTLE_TOE,
)

private val OPPOSITE_BODY_PARTS: BodyPartMap<BodyPart> = BodyPartMap(
	SIDED_BODY_PART_PAIRS.flatMap { (left, right) -> listOf(left to right, right to left) }.toMap(),
)

private val BODY_PART_SIDES: BodyPartMap<Side> = BodyPartMap(
	SIDED_BODY_PART_PAIRS.flatMap { (left, right) -> listOf(left to Side.LEFT, right to Side.RIGHT) }.toMap(),
)

val BodyPart.opposite: BodyPart?
	get() = OPPOSITE_BODY_PARTS[this]

val BodyPart.side: Side?
	get() = BODY_PART_SIDES[this]
