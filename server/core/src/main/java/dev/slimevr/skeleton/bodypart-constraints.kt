package dev.slimevr.skeleton

import com.jme3.math.FastMath
import solarxr_protocol.datatypes.BodyPart

// TODO Actually figure out good values for these constraints, maybe a source would be
//  good?
val BODY_PART_CONSTRAINT_MAP: BodyPartMap<Constraint> = BodyPartMap(
	mapOf(
		BodyPart.LEFT_SHOULDER to TwistSwingConstraint(
			0f * FastMath.DEG_TO_RAD,
			30f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_UPPER_ARM to TwistSwingConstraint(
			120f * FastMath.DEG_TO_RAD,
			180f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_LOWER_ARM to LooseHingeConstraint(
			0f * FastMath.DEG_TO_RAD,
			-180f * FastMath.DEG_TO_RAD,
			40f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_HAND to TwistSwingConstraint(
			120f * FastMath.DEG_TO_RAD,
			120f * FastMath.DEG_TO_RAD,
		),

		BodyPart.RIGHT_SHOULDER to TwistSwingConstraint(
			0f * FastMath.DEG_TO_RAD,
			30f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_UPPER_ARM to TwistSwingConstraint(
			120f * FastMath.DEG_TO_RAD,
			180f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_LOWER_ARM to LooseHingeConstraint(
			0f * FastMath.DEG_TO_RAD,
			-180f * FastMath.DEG_TO_RAD,
			40f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_HAND to TwistSwingConstraint(
			120f * FastMath.DEG_TO_RAD,
			120f * FastMath.DEG_TO_RAD,
		),

		BodyPart.UPPER_CHEST to TwistSwingConstraint(
			90f * FastMath.DEG_TO_RAD,
			120f * FastMath.DEG_TO_RAD,
		),
		BodyPart.CHEST to TwistSwingConstraint(
			60f * FastMath.DEG_TO_RAD,
			120f * FastMath.DEG_TO_RAD,
		),
		BodyPart.WAIST to TwistSwingConstraint(
			60f * FastMath.DEG_TO_RAD,
			120f * FastMath.DEG_TO_RAD,
		),
		BodyPart.HIP to TwistSwingConstraint(
			60f * FastMath.DEG_TO_RAD,
			120f * FastMath.DEG_TO_RAD,
		),

		BodyPart.LEFT_HIP to TwistSwingConstraint(
			0f * FastMath.DEG_TO_RAD,
			15f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_UPPER_LEG to TwistSwingConstraint(
			120f * FastMath.DEG_TO_RAD,
			180f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_LOWER_LEG to LooseHingeConstraint(
			180f * FastMath.DEG_TO_RAD,
			0f * FastMath.DEG_TO_RAD,
			50f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_FOOT to TwistSwingConstraint(
			60f * FastMath.DEG_TO_RAD,
			60f * FastMath.DEG_TO_RAD,
		),

		BodyPart.RIGHT_HIP to TwistSwingConstraint(
			0f * FastMath.DEG_TO_RAD,
			15f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_UPPER_LEG to TwistSwingConstraint(
			120f * FastMath.DEG_TO_RAD,
			180f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_LOWER_LEG to LooseHingeConstraint(
			180f * FastMath.DEG_TO_RAD,
			0f * FastMath.DEG_TO_RAD,
			50f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_FOOT to TwistSwingConstraint(
			60f * FastMath.DEG_TO_RAD,
			60f * FastMath.DEG_TO_RAD,
		),
	),
)
