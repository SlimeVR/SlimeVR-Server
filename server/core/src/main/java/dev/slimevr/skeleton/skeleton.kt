package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

data class Bone(
	val bodyPart: BodyPart,
	val localRotation: Quaternion,
	val parentBone: Bone?,
	val childBone: List<Bone>,
) {
	val globalRotation: Quaternion
		get() = localRotation // FIXME: do maths LMAO
}

data class SkeletonState(
	val bones: Map<BodyPart, Bone>,
	val rootBone: Bone,
)
