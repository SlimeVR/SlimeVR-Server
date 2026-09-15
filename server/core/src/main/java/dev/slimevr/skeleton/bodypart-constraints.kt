package dev.slimevr.skeleton

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
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
			-180f * FastMath.DEG_TO_RAD,
			0f * FastMath.DEG_TO_RAD,
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
			-180f * FastMath.DEG_TO_RAD,
			0f * FastMath.DEG_TO_RAD,
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
		BodyPart.LOWER_CHEST to TwistSwingConstraint(
			60f * FastMath.DEG_TO_RAD,
			120f * FastMath.DEG_TO_RAD,
		),
		BodyPart.UPPER_WAIST to TwistSwingConstraint(
			60f * FastMath.DEG_TO_RAD,
			120f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LOWER_WAIST to TwistSwingConstraint(
			60f * FastMath.DEG_TO_RAD,
			120f * FastMath.DEG_TO_RAD,
		),
		BodyPart.HIP to TwistSwingConstraint(
			60f * FastMath.DEG_TO_RAD,
			120f * FastMath.DEG_TO_RAD,
		),

		BodyPart.LEFT_UPPER_LEG to TwistSwingConstraint(
			120f * FastMath.DEG_TO_RAD,
			170f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_LOWER_LEG to LooseHingeConstraint(
			-5f * FastMath.DEG_TO_RAD,
			180f * FastMath.DEG_TO_RAD,
			10f * FastMath.DEG_TO_RAD,
		),
		BodyPart.LEFT_FOOT to LooseHingeConstraint(
			-60f * FastMath.DEG_TO_RAD,
			90f * FastMath.DEG_TO_RAD,
			60f * FastMath.DEG_TO_RAD,
		),

		BodyPart.RIGHT_UPPER_LEG to TwistSwingConstraint(
			120f * FastMath.DEG_TO_RAD,
			170f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_LOWER_LEG to LooseHingeConstraint(
			-5f * FastMath.DEG_TO_RAD,
			180f * FastMath.DEG_TO_RAD,
			10f * FastMath.DEG_TO_RAD,
		),
		BodyPart.RIGHT_FOOT to LooseHingeConstraint(
			-60f * FastMath.DEG_TO_RAD,
			90f * FastMath.DEG_TO_RAD,
			60f * FastMath.DEG_TO_RAD,
		),
	),
)

fun constrainOffset(
	constraint: Constraint,
	parent: Quaternion,
	bone: Quaternion,
	offset: Quaternion,
): Quaternion {
	// TODO: Ensure the quaternion multiplication order is correct here
	return constraint.apply(parent, offset * bone) * bone.inv()
}

fun constrainOffsetWithSkeleton(
	boneId: BoneId,
	offset: Quaternion,
	bones: ComputedSkeleton,
	constraints: Map<BoneId, Constraint>,
): Quaternion {
	val registry = bones.registry
	val constraint = constraints[boneId] ?: return offset
	val boneRot = bones[boneId]?.rotation ?: return offset
	val parentRot = registry.parentOf(boneId)?.let { parent ->
		bones[parent]?.rotation
	} ?: Quaternion.IDENTITY

	return constrainOffset(
		constraint,
		parentRot,
		boneRot,
		offset,
	)
}

fun constrainSkeleton(
	bones: InputSkeleton,
	constraints: Map<BoneId, Constraint>,
): InputSkeleton {
	val registry = bones.registry
	// Apply constraints top-down, from the root
	for ((parentId, boneId) in registry.hierarchyFrom(registry.root)) {
		val bone = bones[boneId] ?: continue
		val constraint = constraints[boneId] ?: continue
		val parentRotation = parentId?.let { bones[it]?.rotation } ?: Quaternion.IDENTITY
		bones[boneId] = bone.copy(
			rotation = constraint.apply(
				parentRotation,
				bone.rotation,
			),
		)
	}
	return bones
}
