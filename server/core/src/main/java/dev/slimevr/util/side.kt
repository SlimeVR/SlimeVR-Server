package dev.slimevr.util

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

val BodyPart.side: Side?
	get() = when (this) {
		BodyPart.LEFT_HIP,
		BodyPart.LEFT_UPPER_LEG,
		BodyPart.LEFT_LOWER_LEG,
		BodyPart.LEFT_FOOT,
		BodyPart.LEFT_SHOULDER,
		BodyPart.LEFT_UPPER_ARM,
		BodyPart.LEFT_LOWER_ARM,
		BodyPart.LEFT_HAND,
		BodyPart.LEFT_THUMB_METACARPAL,
		BodyPart.LEFT_THUMB_PROXIMAL,
		BodyPart.LEFT_THUMB_DISTAL,
		BodyPart.LEFT_INDEX_PROXIMAL,
		BodyPart.LEFT_INDEX_INTERMEDIATE,
		BodyPart.LEFT_INDEX_DISTAL,
		BodyPart.LEFT_MIDDLE_PROXIMAL,
		BodyPart.LEFT_MIDDLE_INTERMEDIATE,
		BodyPart.LEFT_MIDDLE_DISTAL,
		BodyPart.LEFT_RING_PROXIMAL,
		BodyPart.LEFT_RING_INTERMEDIATE,
		BodyPart.LEFT_RING_DISTAL,
		BodyPart.LEFT_LITTLE_PROXIMAL,
		BodyPart.LEFT_LITTLE_INTERMEDIATE,
		BodyPart.LEFT_LITTLE_DISTAL,
		-> Side.LEFT

		BodyPart.RIGHT_HIP,
		BodyPart.RIGHT_UPPER_LEG,
		BodyPart.RIGHT_LOWER_LEG,
		BodyPart.RIGHT_FOOT,
		BodyPart.RIGHT_SHOULDER,
		BodyPart.RIGHT_UPPER_ARM,
		BodyPart.RIGHT_LOWER_ARM,
		BodyPart.RIGHT_HAND,
		BodyPart.RIGHT_THUMB_METACARPAL,
		BodyPart.RIGHT_THUMB_PROXIMAL,
		BodyPart.RIGHT_THUMB_DISTAL,
		BodyPart.RIGHT_INDEX_PROXIMAL,
		BodyPart.RIGHT_INDEX_INTERMEDIATE,
		BodyPart.RIGHT_INDEX_DISTAL,
		BodyPart.RIGHT_MIDDLE_PROXIMAL,
		BodyPart.RIGHT_MIDDLE_INTERMEDIATE,
		BodyPart.RIGHT_MIDDLE_DISTAL,
		BodyPart.RIGHT_RING_PROXIMAL,
		BodyPart.RIGHT_RING_INTERMEDIATE,
		BodyPart.RIGHT_RING_DISTAL,
		BodyPart.RIGHT_LITTLE_PROXIMAL,
		BodyPart.RIGHT_LITTLE_INTERMEDIATE,
		BodyPart.RIGHT_LITTLE_DISTAL,
		-> Side.RIGHT

		else -> null
	}
