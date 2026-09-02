package dev.slimevr.solarxr

import dev.slimevr.skeleton.BoneState
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.BoneMask
import solarxr_protocol.datatypes.math.Quat
import solarxr_protocol.datatypes.math.Vec3f

fun createBone(bone: BoneState, mask: BoneMask): solarxr_protocol.datatypes.Bone = solarxr_protocol.datatypes.Bone(
	bodyPart = bone.bodyPart.takeIf { mask.bodyPart } ?: BodyPart.NONE,
	boneLength = bone.offset.len().takeIf { mask.boneLength } ?: 0f,
	rotation = bone.rotation.let { Quat(it.x, it.y, it.z, it.w) }.takeIf { mask.rotation },
	orientation = bone.orientation.let { Quat(it.x, it.y, it.z, it.w) }.takeIf { mask.orientation },
	headPosition = bone.headPosition.let { Vec3f(it.x, it.y, it.z) }.takeIf { mask.headPosition },
	tailPosition = bone.tailPosition.let { Vec3f(it.x, it.y, it.z) }.takeIf { mask.tailPosition },
	linearVelocity = bone.velocity.linear.let { Vec3f(it.x, it.y, it.z) }.takeIf { mask.linearVelocity },
	angularVelocity = bone.velocity.angular.let { Vec3f(it.x, it.y, it.z) }.takeIf { mask.angularVelocity },
)
