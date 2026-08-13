package dev.slimevr.solarxr

import dev.slimevr.skeleton.BoneState
import solarxr_protocol.datatypes.BoneMask
import solarxr_protocol.datatypes.math.Quat
import solarxr_protocol.datatypes.math.Vec3f

fun createBone(bone: BoneState, mask: BoneMask): solarxr_protocol.datatypes.Bone = solarxr_protocol.datatypes.Bone(
	bodyPart = bone.bodyPart.takeIf { mask.bodyPart == true },
	orientationG = bone.orientation.let { Quat(it.x, it.y, it.z, it.w) }.takeIf { mask.orientationG == true },
	rotationG = bone.rotation.let { Quat(it.x, it.y, it.z, it.w) }.takeIf { mask.rotationG == true },
	boneLength = bone.offset.len().takeIf { mask.boneLength == true },
	headPositionG = bone.headPosition.let { Vec3f(it.x, it.y, it.z) }.takeIf { mask.headPositionG == true },
)
