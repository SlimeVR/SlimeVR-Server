package dev.slimevr.skeleton

import solarxr_protocol.datatypes.BodyPart

// TODO Should this be generated from the BodyPart structure using start and end points?
val BODY_PART_IK_CHAIN_MAP: BodyPartMap<IKChain> = BodyPartMap(
	mapOf(
		BodyPart.HEAD to listOf(
			BodyPart.NECK,
			BodyPart.HEAD,
		),
		BodyPart.LEFT_HAND to listOf(
			BodyPart.LEFT_SHOULDER,
			BodyPart.LEFT_UPPER_ARM,
			BodyPart.LEFT_LOWER_ARM,
			BodyPart.LEFT_HAND,
		),
		BodyPart.RIGHT_HAND to listOf(
			BodyPart.RIGHT_SHOULDER,
			BodyPart.RIGHT_UPPER_ARM,
			BodyPart.RIGHT_LOWER_ARM,
			BodyPart.RIGHT_HAND,
		),
		BodyPart.HIP to listOf(
			BodyPart.UPPER_CHEST,
			BodyPart.CHEST,
			BodyPart.WAIST,
			BodyPart.HIP,
		),
		BodyPart.LEFT_FOOT to listOf(
			BodyPart.LEFT_UPPER_LEG,
			BodyPart.LEFT_LOWER_LEG,
			BodyPart.LEFT_FOOT,
		),
		BodyPart.RIGHT_FOOT to listOf(
			BodyPart.RIGHT_UPPER_LEG,
			BodyPart.RIGHT_LOWER_LEG,
			BodyPart.RIGHT_FOOT,
		),
	),
)
