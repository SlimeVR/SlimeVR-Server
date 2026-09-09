package dev.slimevr.solarxr

import dev.slimevr.skeleton.BoneState
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.BoneMask
import solarxr_protocol.datatypes.math.Quat
import solarxr_protocol.datatypes.math.Vec3f

fun createBone(bone: BoneState, mask: BoneMask): solarxr_protocol.datatypes.Bone = solarxr_protocol.datatypes.Bone(
	bodyPart = if (mask.bodyPart) bone.bodyPart else BodyPart.NONE,
	boneLength = if (mask.boneLength) bone.offset.len() else 0f,
	rotation = if (mask.rotation) bone.rotation.let { Quat(it.x, it.y, it.z, it.w) } else null,
	orientation = if (mask.orientation) bone.orientation.let { Quat(it.x, it.y, it.z, it.w) } else null,
	headPosition = if (mask.headPosition) bone.headPosition.let { Vec3f(it.x, it.y, it.z) } else null,
	tailPosition = if (mask.tailPosition) bone.tailPosition.let { Vec3f(it.x, it.y, it.z) } else null,
	linearVelocity = if (mask.linearVelocity) bone.velocity.linear.let { Vec3f(it.x, it.y, it.z) } else null,
	angularVelocity = if (mask.angularVelocity) bone.velocity.angular.let { Vec3f(it.x, it.y, it.z) } else null,
)
