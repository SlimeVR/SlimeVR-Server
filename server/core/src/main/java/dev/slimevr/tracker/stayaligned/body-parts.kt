package dev.slimevr.tracker.stayaligned

import dev.slimevr.util.Side
import solarxr_protocol.datatypes.BodyPart

/**
 * Body parts used by Stay Aligned with some utils.
 */
object StayAlignedBodyParts {

	val head = BodyPart.HEAD

	val upperBodyGroup = listOf(
		BodyPart.NECK,
		BodyPart.UPPER_CHEST,
		BodyPart.CHEST,
		BodyPart.WAIST,
		BodyPart.HIP,
	)
	val upperBodyOrder = upperBodyGroup.withIndex().associate { it.value to it.index }

	val leftUpperLeg = BodyPart.LEFT_UPPER_LEG
	val rightUpperLeg = BodyPart.RIGHT_UPPER_LEG
	fun upperLeg(side: Side): BodyPart = when (side) {
		Side.LEFT -> leftUpperLeg
		Side.RIGHT -> rightUpperLeg
	}

	val leftLowerLeg = BodyPart.LEFT_LOWER_LEG
	val rightLowerLeg = BodyPart.RIGHT_LOWER_LEG
	fun lowerLeg(side: Side): BodyPart = when (side) {
		Side.LEFT -> leftLowerLeg
		Side.RIGHT -> rightLowerLeg
	}

	val leftFoot = BodyPart.LEFT_FOOT
	val rightFoot = BodyPart.RIGHT_FOOT
	fun foot(side: Side): BodyPart = when (side) {
		Side.LEFT -> leftFoot
		Side.RIGHT -> rightFoot
	}
}
