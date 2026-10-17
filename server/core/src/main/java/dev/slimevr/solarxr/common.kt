package dev.slimevr.solarxr

import dev.slimevr.skeleton.BoneState
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.BoneMask
import solarxr_protocol.datatypes.math.Quat
import solarxr_protocol.datatypes.math.Vec3f

fun createBone(bone: BoneState, mask: BoneMask): solarxr_protocol.datatypes.Bone = solarxr_protocol.datatypes.Bone(
	bodyPart = if (mask.bodyPart) bone.bodyPart else BodyPart.NONE,
	boneLength = if (mask.boneLength) bone.offset.len() else 0f,
	rotation = if (mask.rotation) bone.rotation.toQuat() else null,
	orientation = if (mask.orientation) bone.orientation.toQuat() else null,
	headPosition = if (mask.headPosition) bone.headPosition.toVec3f() else null,
	tailPosition = if (mask.tailPosition) bone.tailPosition.toVec3f() else null,
	linearVelocity = if (mask.linearVelocity) bone.velocity.linear.toVec3f() else null,
	angularVelocity = if (mask.angularVelocity) bone.velocity.angular.toVec3f() else null,
	trackerOffset = if (mask.trackerOffset) bone.trackerOffset.toVec3f() else null,
)

fun Quat.toQuaternion() = Quaternion(w, x, y, z)

fun Quaternion.toQuat() = Quat(x, y, z, w)

fun Vec3f.toVector3() = Vector3(x, y, z)

fun Vector3.toVec3f() = Vec3f(x, y, z)
