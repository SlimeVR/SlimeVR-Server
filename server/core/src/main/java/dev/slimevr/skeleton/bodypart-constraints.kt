package dev.slimevr.skeleton

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

private val METACARPAL_THUMB_CONSTRAINT = LooseHingeConstraint(
	-80f * FastMath.DEG_TO_RAD,
	100f * FastMath.DEG_TO_RAD,
	55f * FastMath.DEG_TO_RAD,
	Vector3.NEG_X,
)
private val THUMB_CONSTRAINT = LooseHingeConstraint(
	-5f * FastMath.DEG_TO_RAD,
	100f * FastMath.DEG_TO_RAD,
	3f * FastMath.DEG_TO_RAD,
	Vector3.NEG_X,
)

private val LEFT_PROXIMAL_FINGER_CONSTRAINT = LooseHingeConstraint(
	-65f * FastMath.DEG_TO_RAD,
	110f * FastMath.DEG_TO_RAD,
	30f * FastMath.DEG_TO_RAD,
	Vector3.POS_Z,
)
private val LEFT_FINGER_CONSTRAINT = LooseHingeConstraint(
	-5f * FastMath.DEG_TO_RAD,
	110f * FastMath.DEG_TO_RAD,
	3f,
	Vector3.POS_Z,
)
private val RIGHT_PROXIMAL_FINGER_CONSTRAINT = LEFT_PROXIMAL_FINGER_CONSTRAINT.copy(hingeAxis = Vector3.NEG_Z)
private val RIGHT_FINGER_CONSTRAINT = LEFT_FINGER_CONSTRAINT.copy(hingeAxis = Vector3.NEG_Z)

private val TOE_CONSTRAINT = LooseHingeConstraint(
	-70f * FastMath.DEG_TO_RAD,
	90f * FastMath.DEG_TO_RAD,
	30f * FastMath.DEG_TO_RAD,
	Vector3.NEG_X,
)

val BODY_PART_CONSTRAINT_MAP: BodyPartMap<Constraint> = BodyPartMap(
	mapOf(
		// Left arm
		BodyPart.LEFT_SHOULDER to TwistSwingConstraint(
			105f * FastMath.DEG_TO_RAD,
			70f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_UPPER_ARM to FreeHingeConstraint(
			-40f * FastMath.DEG_TO_RAD,
			125f * FastMath.DEG_TO_RAD,
			Vector3.POS_Y,
		),
		BodyPart.LEFT_LOWER_ARM to LooseHingeConstraint(
			-160f * FastMath.DEG_TO_RAD,
			0f * FastMath.DEG_TO_RAD,
			55f * FastMath.DEG_TO_RAD,
			Vector3.NEG_Z,
		),
		BodyPart.LEFT_HAND to TwistSwingConstraint(
			70f * FastMath.DEG_TO_RAD,
			95f * FastMath.DEG_TO_RAD,
		),

		// Right arm
		BodyPart.RIGHT_SHOULDER to TwistSwingConstraint(
			105f * FastMath.DEG_TO_RAD,
			70f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_UPPER_ARM to FreeHingeConstraint(
			-40f * FastMath.DEG_TO_RAD,
			125f * FastMath.DEG_TO_RAD,
			Vector3.NEG_Y,
		),
		BodyPart.RIGHT_LOWER_ARM to LooseHingeConstraint(
			-160f * FastMath.DEG_TO_RAD,
			0f * FastMath.DEG_TO_RAD,
			55f * FastMath.DEG_TO_RAD,
			Vector3.POS_Z,
		),
		BodyPart.RIGHT_HAND to TwistSwingConstraint(
			70f * FastMath.DEG_TO_RAD,
			95f * FastMath.DEG_TO_RAD,
		),

		// Spine
		BodyPart.UPPER_CHEST to TwistSwingConstraint(
			95f * FastMath.DEG_TO_RAD,
			105f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LOWER_CHEST to TwistSwingConstraint(
			30f * FastMath.DEG_TO_RAD,
			40f * FastMath.DEG_TO_RAD,
		),
		BodyPart.UPPER_WAIST to TwistSwingConstraint(
			35f * FastMath.DEG_TO_RAD,
			60f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LOWER_WAIST to TwistSwingConstraint(
			35f * FastMath.DEG_TO_RAD,
			55f * FastMath.DEG_TO_RAD,
		),
		BodyPart.HIP to TwistSwingConstraint(
			50f * FastMath.DEG_TO_RAD,
			80f * FastMath.DEG_TO_RAD,
		),

		// Left leg
		BodyPart.LEFT_UPPER_LEG to LooseHingeConstraint(
			-50f * FastMath.DEG_TO_RAD,
			165f * FastMath.DEG_TO_RAD,
			90f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_LOWER_LEG to LooseHingeConstraint(
			-150f * FastMath.DEG_TO_RAD,
			6f * FastMath.DEG_TO_RAD,
			65f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_FOOT to LooseHingeConstraint(
			-45f * FastMath.DEG_TO_RAD,
			20f * FastMath.DEG_TO_RAD,
			55f * FastMath.DEG_TO_RAD,
		),

		// Right leg
		BodyPart.RIGHT_UPPER_LEG to LooseHingeConstraint(
			-50f * FastMath.DEG_TO_RAD,
			165f * FastMath.DEG_TO_RAD,
			90f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_LOWER_LEG to LooseHingeConstraint(
			-150f * FastMath.DEG_TO_RAD,
			6f * FastMath.DEG_TO_RAD,
			65f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_FOOT to LooseHingeConstraint(
			-45f * FastMath.DEG_TO_RAD,
			20f * FastMath.DEG_TO_RAD,
			55f * FastMath.DEG_TO_RAD,
		),

		// Left fingers
		BodyPart.LEFT_THUMB_METACARPAL to METACARPAL_THUMB_CONSTRAINT,
		BodyPart.LEFT_THUMB_PROXIMAL to THUMB_CONSTRAINT,
		BodyPart.LEFT_THUMB_DISTAL to THUMB_CONSTRAINT,
		BodyPart.LEFT_INDEX_PROXIMAL to LEFT_PROXIMAL_FINGER_CONSTRAINT,
		BodyPart.LEFT_INDEX_INTERMEDIATE to LEFT_FINGER_CONSTRAINT,
		BodyPart.LEFT_INDEX_DISTAL to LEFT_FINGER_CONSTRAINT,
		BodyPart.LEFT_MIDDLE_PROXIMAL to LEFT_PROXIMAL_FINGER_CONSTRAINT,
		BodyPart.LEFT_MIDDLE_INTERMEDIATE to LEFT_FINGER_CONSTRAINT,
		BodyPart.LEFT_MIDDLE_DISTAL to LEFT_FINGER_CONSTRAINT,
		BodyPart.LEFT_RING_PROXIMAL to LEFT_PROXIMAL_FINGER_CONSTRAINT,
		BodyPart.LEFT_RING_INTERMEDIATE to LEFT_FINGER_CONSTRAINT,
		BodyPart.LEFT_RING_DISTAL to LEFT_FINGER_CONSTRAINT,
		BodyPart.LEFT_LITTLE_PROXIMAL to LEFT_PROXIMAL_FINGER_CONSTRAINT,
		BodyPart.LEFT_LITTLE_INTERMEDIATE to LEFT_FINGER_CONSTRAINT,
		BodyPart.LEFT_LITTLE_DISTAL to LEFT_FINGER_CONSTRAINT,

		// Right fingers
		BodyPart.RIGHT_THUMB_METACARPAL to METACARPAL_THUMB_CONSTRAINT,
		BodyPart.RIGHT_THUMB_PROXIMAL to THUMB_CONSTRAINT,
		BodyPart.RIGHT_THUMB_DISTAL to THUMB_CONSTRAINT,
		BodyPart.RIGHT_INDEX_PROXIMAL to RIGHT_PROXIMAL_FINGER_CONSTRAINT,
		BodyPart.RIGHT_INDEX_INTERMEDIATE to RIGHT_FINGER_CONSTRAINT,
		BodyPart.RIGHT_INDEX_DISTAL to RIGHT_FINGER_CONSTRAINT,
		BodyPart.RIGHT_MIDDLE_PROXIMAL to RIGHT_PROXIMAL_FINGER_CONSTRAINT,
		BodyPart.RIGHT_MIDDLE_INTERMEDIATE to RIGHT_FINGER_CONSTRAINT,
		BodyPart.RIGHT_MIDDLE_DISTAL to RIGHT_FINGER_CONSTRAINT,
		BodyPart.RIGHT_RING_PROXIMAL to RIGHT_PROXIMAL_FINGER_CONSTRAINT,
		BodyPart.RIGHT_RING_INTERMEDIATE to RIGHT_FINGER_CONSTRAINT,
		BodyPart.RIGHT_RING_DISTAL to RIGHT_FINGER_CONSTRAINT,
		BodyPart.RIGHT_LITTLE_PROXIMAL to RIGHT_PROXIMAL_FINGER_CONSTRAINT,
		BodyPart.RIGHT_LITTLE_INTERMEDIATE to RIGHT_FINGER_CONSTRAINT,
		BodyPart.RIGHT_LITTLE_DISTAL to RIGHT_FINGER_CONSTRAINT,

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
	onlyActive: Boolean,
): InputSkeleton {
	// Apply constraints top-down
	for ((parentBodyPart, bodyPart) in iterateBodyPartHierarchy()) {
		val bone = bones[bodyPart] ?: continue
		if (onlyActive && !bone.isRotationActive) continue
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
 * Used for the initial rotation before doing IK to promote movement and preferred axis.
 */
val BODY_PART_PREFERRED_ROTATION = BodyPartMap(
	mapOf(
		BodyPart.LEFT_UPPER_ARM to Quaternion.fromRotationVector(-45f * FastMath.DEG_TO_RAD, 90f * FastMath.DEG_TO_RAD, -100f * FastMath.DEG_TO_RAD),
		BodyPart.RIGHT_UPPER_ARM to Quaternion.fromRotationVector(-45f * FastMath.DEG_TO_RAD, -90f * FastMath.DEG_TO_RAD, 100f * FastMath.DEG_TO_RAD),
		BodyPart.LEFT_LOWER_ARM to Quaternion.fromRotationVector(0f, 70f * FastMath.DEG_TO_RAD, 0f),
		BodyPart.RIGHT_LOWER_ARM to Quaternion.fromRotationVector(0f, -70f * FastMath.DEG_TO_RAD, 0f),

		BodyPart.LEFT_UPPER_LEG to Quaternion.rotationAroundXAxis(115f * FastMath.DEG_TO_RAD),
		BodyPart.RIGHT_UPPER_LEG to Quaternion.rotationAroundXAxis(115f * FastMath.DEG_TO_RAD),
		BodyPart.LEFT_LOWER_LEG to Quaternion.rotationAroundXAxis(-48f * FastMath.DEG_TO_RAD),
		BodyPart.RIGHT_LOWER_LEG to Quaternion.rotationAroundXAxis(-48f * FastMath.DEG_TO_RAD),

// 		BodyPart.UPPER_CHEST to Quaternion.rotationAroundXAxis(-10f * FastMath.DEG_TO_RAD),
// 		BodyPart.LOWER_CHEST to Quaternion.rotationAroundXAxis(-5f * FastMath.DEG_TO_RAD),
// 		BodyPart.UPPER_WAIST to Quaternion.rotationAroundXAxis(7.5f * FastMath.DEG_TO_RAD),
// 		BodyPart.LOWER_WAIST to Quaternion.rotationAroundXAxis(7.5f * FastMath.DEG_TO_RAD),
	),
)

// Constraints to apply to a local offset to best fit a bone's natural movement
// Basically the max angle change per IK iteration within local axes
// TODO May need to decay constraint over iterations
val BODY_PART_OFFSET_CONSTRAINT_MAP: BodyPartMap<Constraint> = BodyPartMap(
	mapOf(
		BodyPart.LEFT_UPPER_ARM to LooseAxisConstraint(
			5f * FastMath.DEG_TO_RAD,
			Vector3.POS_X,
		),
		BodyPart.LEFT_LOWER_ARM to LooseAxisConstraint(
			5f * FastMath.DEG_TO_RAD,
			Vector3.POS_X,
		),

		BodyPart.RIGHT_UPPER_ARM to LooseAxisConstraint(
			5f * FastMath.DEG_TO_RAD,
			Vector3.POS_X,
		),
		BodyPart.RIGHT_LOWER_ARM to LooseAxisConstraint(
			5f * FastMath.DEG_TO_RAD,
			Vector3.POS_X,
		),

// 		BodyPart.UPPER_CHEST to TwistSwingConstraint(
// 			8f * FastMath.DEG_TO_RAD,
// 			4f * FastMath.DEG_TO_RAD,
// 			Vector3.POS_X,
// 		),
// 		BodyPart.LOWER_CHEST to TwistSwingConstraint(
// 			4f * FastMath.DEG_TO_RAD,
// 			2f * FastMath.DEG_TO_RAD,
// 			Vector3.POS_X,
// 		),
// 		BodyPart.UPPER_WAIST to TwistSwingConstraint(
// 			6f * FastMath.DEG_TO_RAD,
// 			4f * FastMath.DEG_TO_RAD,
// 			Vector3.POS_X,
// 		),
// 		BodyPart.LOWER_WAIST to TwistSwingConstraint(
// 			3f * FastMath.DEG_TO_RAD,
// 			2f * FastMath.DEG_TO_RAD,
// 			Vector3.POS_X,
// 		),

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
