package dev.slimevr.skeleton

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

private val FINGER_CONSTRAINT = LooseAxisConstraint(
	25f * FastMath.DEG_TO_RAD,
	Vector3.POS_Z,
)
private val THUMB_CONSTRAINT = LooseAxisConstraint(
	35f * FastMath.DEG_TO_RAD,
	Vector3.POS_X,
)
private val TOE_CONSTRAINT = LooseAxisConstraint(
	25f * FastMath.DEG_TO_RAD,
	Vector3.POS_X,
)

val BODY_PART_CONSTRAINT_MAP: BodyPartMap<Constraint> = BodyPartMap(
	mapOf(
		// Left arm
		BodyPart.LEFT_SHOULDER to TwistSwingConstraint(
			105f * FastMath.DEG_TO_RAD,
			50f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_UPPER_ARM to TwistConstraint(
			45f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_LOWER_ARM to LooseHingeConstraint(
			-170f * FastMath.DEG_TO_RAD,
			0f * FastMath.DEG_TO_RAD,
			55f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_HAND to TwistSwingConstraint(
			70f * FastMath.DEG_TO_RAD,
			95f * FastMath.DEG_TO_RAD,
		),

		// Right arm
		BodyPart.RIGHT_SHOULDER to TwistSwingConstraint(
			105f * FastMath.DEG_TO_RAD,
			50f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_UPPER_ARM to TwistConstraint(
			45f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_LOWER_ARM to LooseHingeConstraint(
			-170f * FastMath.DEG_TO_RAD,
			0f * FastMath.DEG_TO_RAD,
			55f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_HAND to TwistSwingConstraint(
			70f * FastMath.DEG_TO_RAD,
			95f * FastMath.DEG_TO_RAD,
		),

		// Spine
		BodyPart.UPPER_CHEST to TwistSwingConstraint(
			95f * FastMath.DEG_TO_RAD,
			115f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LOWER_CHEST to TwistSwingConstraint(
			40f * FastMath.DEG_TO_RAD,
			90f * FastMath.DEG_TO_RAD,
		),
		BodyPart.UPPER_WAIST to TwistSwingConstraint(
			40f * FastMath.DEG_TO_RAD,
			90f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LOWER_WAIST to TwistSwingConstraint(
			40f * FastMath.DEG_TO_RAD,
			90f * FastMath.DEG_TO_RAD,
		),
		BodyPart.HIP to TwistSwingConstraint(
			60f * FastMath.DEG_TO_RAD,
			100f * FastMath.DEG_TO_RAD,
		),

		// Left leg
		BodyPart.LEFT_UPPER_LEG to LooseHingeConstraint(
			-50f * FastMath.DEG_TO_RAD,
			170f * FastMath.DEG_TO_RAD,
			85f * FastMath.DEG_TO_RAD,
			hingeAxis = Vector3.POS_X,
		),
		BodyPart.LEFT_LOWER_LEG to LooseHingeConstraint(
			-6f * FastMath.DEG_TO_RAD,
			170f * FastMath.DEG_TO_RAD,
			50f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_FOOT to TwistSwingConstraint(
			35f * FastMath.DEG_TO_RAD,
			55f * FastMath.DEG_TO_RAD,
		),

		// Right leg
		BodyPart.RIGHT_UPPER_LEG to LooseHingeConstraint(
			-50f * FastMath.DEG_TO_RAD,
			170f * FastMath.DEG_TO_RAD,
			85f * FastMath.DEG_TO_RAD,
			hingeAxis = Vector3.POS_X,
		),
		BodyPart.RIGHT_LOWER_LEG to LooseHingeConstraint(
			-6f * FastMath.DEG_TO_RAD,
			170f * FastMath.DEG_TO_RAD,
			50f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_FOOT to TwistSwingConstraint(
			35f * FastMath.DEG_TO_RAD,
			55f * FastMath.DEG_TO_RAD,
		),

		// Left fingers
		BodyPart.LEFT_THUMB_METACARPAL to THUMB_CONSTRAINT,
		BodyPart.LEFT_THUMB_PROXIMAL to THUMB_CONSTRAINT,
		BodyPart.LEFT_THUMB_DISTAL to THUMB_CONSTRAINT,
		BodyPart.LEFT_INDEX_PROXIMAL to FINGER_CONSTRAINT,
		BodyPart.LEFT_INDEX_INTERMEDIATE to FINGER_CONSTRAINT,
		BodyPart.LEFT_INDEX_DISTAL to FINGER_CONSTRAINT,
		BodyPart.LEFT_MIDDLE_PROXIMAL to FINGER_CONSTRAINT,
		BodyPart.LEFT_MIDDLE_INTERMEDIATE to FINGER_CONSTRAINT,
		BodyPart.LEFT_MIDDLE_DISTAL to FINGER_CONSTRAINT,
		BodyPart.LEFT_RING_PROXIMAL to FINGER_CONSTRAINT,
		BodyPart.LEFT_RING_INTERMEDIATE to FINGER_CONSTRAINT,
		BodyPart.LEFT_RING_DISTAL to FINGER_CONSTRAINT,
		BodyPart.LEFT_LITTLE_PROXIMAL to FINGER_CONSTRAINT,
		BodyPart.LEFT_LITTLE_INTERMEDIATE to FINGER_CONSTRAINT,
		BodyPart.LEFT_LITTLE_DISTAL to FINGER_CONSTRAINT,

		// Right fingers
		BodyPart.RIGHT_THUMB_METACARPAL to THUMB_CONSTRAINT,
		BodyPart.RIGHT_THUMB_PROXIMAL to THUMB_CONSTRAINT,
		BodyPart.RIGHT_THUMB_DISTAL to THUMB_CONSTRAINT,
		BodyPart.RIGHT_INDEX_PROXIMAL to FINGER_CONSTRAINT,
		BodyPart.RIGHT_INDEX_INTERMEDIATE to FINGER_CONSTRAINT,
		BodyPart.RIGHT_INDEX_DISTAL to FINGER_CONSTRAINT,
		BodyPart.RIGHT_MIDDLE_PROXIMAL to FINGER_CONSTRAINT,
		BodyPart.RIGHT_MIDDLE_INTERMEDIATE to FINGER_CONSTRAINT,
		BodyPart.RIGHT_MIDDLE_DISTAL to FINGER_CONSTRAINT,
		BodyPart.RIGHT_RING_PROXIMAL to FINGER_CONSTRAINT,
		BodyPart.RIGHT_RING_INTERMEDIATE to FINGER_CONSTRAINT,
		BodyPart.RIGHT_RING_DISTAL to FINGER_CONSTRAINT,
		BodyPart.RIGHT_LITTLE_PROXIMAL to FINGER_CONSTRAINT,
		BodyPart.RIGHT_LITTLE_INTERMEDIATE to FINGER_CONSTRAINT,
		BodyPart.RIGHT_LITTLE_DISTAL to FINGER_CONSTRAINT,

		// Toes
		BodyPart.LEFT_BIG_TOE to TOE_CONSTRAINT,
		BodyPart.LEFT_INDEX_TOE to TOE_CONSTRAINT,
		BodyPart.LEFT_MIDDLE_TOE to TOE_CONSTRAINT,
		BodyPart.LEFT_RING_TOE to TOE_CONSTRAINT,
		BodyPart.LEFT_LITTLE_TOE to TOE_CONSTRAINT,
		BodyPart.RIGHT_BIG_TOE to TOE_CONSTRAINT,
		BodyPart.RIGHT_INDEX_TOE to TOE_CONSTRAINT,
		BodyPart.RIGHT_MIDDLE_TOE to TOE_CONSTRAINT,
		BodyPart.RIGHT_RING_TOE to TOE_CONSTRAINT,
		BodyPart.RIGHT_LITTLE_TOE to TOE_CONSTRAINT,
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

/**
 * Used for the initial rotation before doing IK.
 */
val BODY_PART_PREFERRED_ROTATION = BodyPartMap(
	mapOf(
		BodyPart.LEFT_UPPER_LEG to Quaternion.rotationAroundXAxis(90f * FastMath.DEG_TO_RAD),
		BodyPart.RIGHT_UPPER_LEG to Quaternion.rotationAroundXAxis(90f * FastMath.DEG_TO_RAD),
		BodyPart.LEFT_LOWER_LEG to Quaternion.rotationAroundXAxis(-45f * FastMath.DEG_TO_RAD),
		BodyPart.RIGHT_LOWER_LEG to Quaternion.rotationAroundXAxis(-45f * FastMath.DEG_TO_RAD),
		// TODO arms and others
// 		BodyPart.LEFT_UPPER_ARM to Quaternion.rotationAroundXAxis(-45f * FastMath.DEG_TO_RAD),
// 		BodyPart.RIGHT_UPPER_ARM to Quaternion.rotationAroundXAxis(-45f * FastMath.DEG_TO_RAD),
// 		BodyPart.LEFT_LOWER_ARM to Quaternion.rotationAroundXAxis(90f * FastMath.DEG_TO_RAD),
// 		BodyPart.RIGHT_LOWER_ARM to Quaternion.rotationAroundXAxis(90f * FastMath.DEG_TO_RAD),
	),
)

// Constraints to apply to a local offset to best fit a bone's natural movement
// Basically the max angle change per IK iteration within local axes
// TODO May need to decay constraint over iterations
val BODY_PART_OFFSET_CONSTRAINT_MAP: BodyPartMap<Constraint> = BodyPartMap(
	mapOf(
		// TODO Figure out arms
		BodyPart.LEFT_UPPER_ARM to TwistSwingConstraint(
			5f * FastMath.DEG_TO_RAD,
			30f * FastMath.DEG_TO_RAD,
			Vector3.NEG_X,
		),
		BodyPart.LEFT_LOWER_ARM to LooseAxisConstraint(
			1f * FastMath.DEG_TO_RAD,
		),

		BodyPart.RIGHT_UPPER_ARM to TwistSwingConstraint(
			5f * FastMath.DEG_TO_RAD,
			30f * FastMath.DEG_TO_RAD,
			Vector3.NEG_X,
		),
		BodyPart.RIGHT_LOWER_ARM to LooseAxisConstraint(
			1f * FastMath.DEG_TO_RAD,
		),

		BodyPart.UPPER_CHEST to LooseAxisConstraint(
			15f * FastMath.DEG_TO_RAD,
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
			0.2f * FastMath.DEG_TO_RAD,
		),

		BodyPart.RIGHT_UPPER_LEG to LooseAxisConstraint(
			5f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_LOWER_LEG to LooseAxisConstraint(
			0.2f * FastMath.DEG_TO_RAD,
		),
	),
)
