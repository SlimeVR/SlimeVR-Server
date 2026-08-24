package dev.slimevr.solarxr

import dev.slimevr.skeleton.BoneState
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.BoneMask
import solarxr_protocol.datatypes.math.Quat
import solarxr_protocol.datatypes.math.Vec3f

fun createBone(bone: BoneState, mask: BoneMask): solarxr_protocol.datatypes.Bone = solarxr_protocol.datatypes.Bone(
	bodyPart = bone.bodyPart.takeIf { mask.bodyPart } ?: BodyPart.NONE,
	orientationG = bone.orientation.let { Quat(it.x, it.y, it.z, it.w) }.takeIf { mask.orientationG },
	rotationG = bone.rotation.let { Quat(it.x, it.y, it.z, it.w) }.takeIf { mask.rotationG },
	boneLength = bone.offset.len().takeIf { mask.boneLength } ?: 0f,
	headPositionG = bone.headPosition.let { Vec3f(it.x, it.y, it.z) }.takeIf { mask.headPositionG },
)
