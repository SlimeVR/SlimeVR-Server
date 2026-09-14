package dev.slimevr.skeleton

import dev.slimevr.bones.BoneId
import dev.slimevr.bones.Constraint
import io.github.axisangles.ktmath.Quaternion

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
