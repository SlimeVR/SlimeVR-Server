package dev.slimevr.skeleton

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

// For fingers and toes
// TODO: should constrain splay separately from swing.
private val DIGIT_CONSTRAINT = TwistSwingConstraint(
	10f * FastMath.DEG_TO_RAD,
	115f * FastMath.DEG_TO_RAD,
)

// TODO Actually figure out good values for these constraints, maybe a source would be
//  good?
val BODY_PART_CONSTRAINT_MAP: BodyPartMap<Constraint> = BodyPartMap(
	mapOf(
		// Left arm
		BodyPart.LEFT_SHOULDER to TwistSwingConstraint(
			105f * FastMath.DEG_TO_RAD,
			45f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_UPPER_ARM to TwistSwingConstraint(
			120f * FastMath.DEG_TO_RAD,
			180f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_LOWER_ARM to LooseHingeConstraint(
			-180f * FastMath.DEG_TO_RAD,
			0f * FastMath.DEG_TO_RAD,
			40f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_HAND to TwistSwingConstraint(
			120f * FastMath.DEG_TO_RAD,
			120f * FastMath.DEG_TO_RAD,
		),

		// Right arm
		BodyPart.RIGHT_SHOULDER to TwistSwingConstraint(
			105f * FastMath.DEG_TO_RAD,
			45f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_UPPER_ARM to TwistSwingConstraint(
			120f * FastMath.DEG_TO_RAD,
			180f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_LOWER_ARM to LooseHingeConstraint(
			-180f * FastMath.DEG_TO_RAD,
			0f * FastMath.DEG_TO_RAD,
			40f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_HAND to TwistSwingConstraint(
			120f * FastMath.DEG_TO_RAD,
			120f * FastMath.DEG_TO_RAD,
		),

		// Spine
		BodyPart.UPPER_CHEST to TwistSwingConstraint(
			95f * FastMath.DEG_TO_RAD,
			120f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LOWER_CHEST to TwistSwingConstraint(
			40f * FastMath.DEG_TO_RAD,
			110f * FastMath.DEG_TO_RAD,
		),
		BodyPart.UPPER_WAIST to TwistSwingConstraint(
			40f * FastMath.DEG_TO_RAD,
			110f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LOWER_WAIST to TwistSwingConstraint(
			40f * FastMath.DEG_TO_RAD,
			110f * FastMath.DEG_TO_RAD,
		),
		BodyPart.HIP to TwistSwingConstraint(
			65f * FastMath.DEG_TO_RAD,
			120f * FastMath.DEG_TO_RAD,
		),

		// Left leg
		BodyPart.LEFT_UPPER_LEG to TwistSwingConstraint(
			120f * FastMath.DEG_TO_RAD,
			170f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_LOWER_LEG to LooseHingeConstraint(
			-5f * FastMath.DEG_TO_RAD,
			180f * FastMath.DEG_TO_RAD,
			40f * FastMath.DEG_TO_RAD,
		),
		// TODO enforcing constraint looks bad
// 		BodyPart.LEFT_FOOT to LooseHingeConstraint(
// 			-60f * FastMath.DEG_TO_RAD,
// 			90f * FastMath.DEG_TO_RAD,
// 			80f * FastMath.DEG_TO_RAD,
// 		),

		// Right leg
		BodyPart.RIGHT_UPPER_LEG to TwistSwingConstraint(
			120f * FastMath.DEG_TO_RAD,
			170f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_LOWER_LEG to LooseHingeConstraint(
			-5f * FastMath.DEG_TO_RAD,
			180f * FastMath.DEG_TO_RAD,
			40f * FastMath.DEG_TO_RAD,
		),
// 		BodyPart.RIGHT_FOOT to LooseHingeConstraint(
// 			-60f * FastMath.DEG_TO_RAD,
// 			90f * FastMath.DEG_TO_RAD,
// 			80f * FastMath.DEG_TO_RAD,
// 		),

		// Left fingers
		BodyPart.LEFT_THUMB_METACARPAL to DIGIT_CONSTRAINT,
		BodyPart.LEFT_THUMB_PROXIMAL to DIGIT_CONSTRAINT,
		BodyPart.LEFT_THUMB_DISTAL to DIGIT_CONSTRAINT,
		BodyPart.LEFT_INDEX_PROXIMAL to DIGIT_CONSTRAINT,
		BodyPart.LEFT_INDEX_INTERMEDIATE to DIGIT_CONSTRAINT,
		BodyPart.LEFT_INDEX_DISTAL to DIGIT_CONSTRAINT,
		BodyPart.LEFT_MIDDLE_PROXIMAL to DIGIT_CONSTRAINT,
		BodyPart.LEFT_MIDDLE_INTERMEDIATE to DIGIT_CONSTRAINT,
		BodyPart.LEFT_MIDDLE_DISTAL to DIGIT_CONSTRAINT,
		BodyPart.LEFT_RING_PROXIMAL to DIGIT_CONSTRAINT,
		BodyPart.LEFT_RING_INTERMEDIATE to DIGIT_CONSTRAINT,
		BodyPart.LEFT_RING_DISTAL to DIGIT_CONSTRAINT,
		BodyPart.LEFT_LITTLE_PROXIMAL to DIGIT_CONSTRAINT,
		BodyPart.LEFT_LITTLE_INTERMEDIATE to DIGIT_CONSTRAINT,
		BodyPart.LEFT_LITTLE_DISTAL to DIGIT_CONSTRAINT,

		// Right fingers
		BodyPart.RIGHT_THUMB_METACARPAL to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_THUMB_PROXIMAL to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_THUMB_DISTAL to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_INDEX_PROXIMAL to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_INDEX_INTERMEDIATE to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_INDEX_DISTAL to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_MIDDLE_PROXIMAL to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_MIDDLE_INTERMEDIATE to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_MIDDLE_DISTAL to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_RING_PROXIMAL to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_RING_INTERMEDIATE to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_RING_DISTAL to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_LITTLE_PROXIMAL to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_LITTLE_INTERMEDIATE to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_LITTLE_DISTAL to DIGIT_CONSTRAINT,

		// Left toes
		BodyPart.LEFT_BIG_TOE to DIGIT_CONSTRAINT,
		BodyPart.LEFT_INDEX_TOE to DIGIT_CONSTRAINT,
		BodyPart.LEFT_MIDDLE_TOE to DIGIT_CONSTRAINT,
		BodyPart.LEFT_RING_TOE to DIGIT_CONSTRAINT,
		BodyPart.LEFT_LITTLE_TOE to DIGIT_CONSTRAINT,

		// Right toes
		BodyPart.RIGHT_BIG_TOE to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_INDEX_TOE to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_MIDDLE_TOE to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_RING_TOE to DIGIT_CONSTRAINT,
		BodyPart.RIGHT_LITTLE_TOE to DIGIT_CONSTRAINT,
	),
)

fun constrainSkeleton(
	bones: InputSkeleton,
	constraints: BodyPartMap<Constraint>,
): InputSkeleton {
	// Apply constraints top-down
	for ((parentBodyPart, bodyPart) in iterateBodyPartHierarchy()) {
		val bone = bones[bodyPart] ?: continue
		val constraint = constraints[bodyPart] ?: continue
		val parentRotation = bones[parentBodyPart]?.rotation ?: Quaternion.IDENTITY
		bones[bodyPart] = bone.copy(
			rotation = constraint.apply(
				parentRotation,
				bone.rotation,
			),
		)
	}
	return bones
}

// Constraints to apply to a local offset to best fit a bone's natural movement
// Basically the max angle change per IK iteration within local axes
// TODO May need to decay constraint over iterations
val BODY_PART_OFFSET_CONSTRAINT_MAP: BodyPartMap<Constraint> = BodyPartMap(
	mapOf(
		// TODO Figure out arms
		// BodyPart.LEFT_UPPER_ARM to AxisConstraint(),
		// BodyPart.RIGHT_UPPER_ARM to AxisConstraint(),

		BodyPart.UPPER_CHEST to LooseAxisConstraint(
			10f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LOWER_CHEST to LooseAxisConstraint(
			10f * FastMath.DEG_TO_RAD,
		),
		BodyPart.UPPER_WAIST to LooseAxisConstraint(
			10f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LOWER_WAIST to LooseAxisConstraint(
			10f * FastMath.DEG_TO_RAD,
		),

		BodyPart.LEFT_UPPER_LEG to LooseAxisConstraint(
			5f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_LOWER_LEG to LooseAxisConstraint(
			0.1f * FastMath.DEG_TO_RAD,
		),

		BodyPart.RIGHT_UPPER_LEG to LooseAxisConstraint(
			5f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_LOWER_LEG to LooseAxisConstraint(
			0.1f * FastMath.DEG_TO_RAD,
		),
	),
)
