package dev.slimevr.driver

import solarxr_protocol.datatypes.BodyPart

/**
 * Tracker roles the driver understands, SteamVR or Monado alike.
 * The values are the wire numbers used by the driver IPC protobuf.
 */
enum class TrackerRole(
	val value: UByte,
) {
	NONE(0.toUByte()),
	WAIST(1.toUByte()),
	LEFT_FOOT(2.toUByte()),
	RIGHT_FOOT(3.toUByte()),
	CHEST(4.toUByte()),
	LEFT_KNEE(5.toUByte()),
	RIGHT_KNEE(6.toUByte()),
	LEFT_ELBOW(7.toUByte()),
	RIGHT_ELBOW(8.toUByte()),
	LEFT_SHOULDER(9.toUByte()),
	RIGHT_SHOULDER(10.toUByte()),
	LEFT_HAND(11.toUByte()),
	RIGHT_HAND(12.toUByte()),
	LEFT_CONTROLLER(13.toUByte()),
	RIGHT_CONTROLLER(14.toUByte()),
	HEAD(15.toUByte()),
	NECK(16.toUByte()),
	CAMERA(17.toUByte()),
	KEYBOARD(18.toUByte()),
	HMD(19.toUByte()),
	BEACON(20.toUByte()),
	GENERIC_CONTROLLER(21.toUByte()),
	;

	companion object {
		fun fromValue(value: UByte): TrackerRole? = entries.firstOrNull { it.value == value }
	}
}

val bodyPartToRole: Map<BodyPart, TrackerRole> = mapOf(
	BodyPart.HEAD to TrackerRole.HMD,
	BodyPart.UPPER_CHEST to TrackerRole.CHEST,
	BodyPart.LEFT_UPPER_ARM to TrackerRole.LEFT_ELBOW,
	BodyPart.RIGHT_UPPER_ARM to TrackerRole.RIGHT_ELBOW,
	BodyPart.HIP to TrackerRole.WAIST,
	BodyPart.LEFT_UPPER_LEG to TrackerRole.LEFT_KNEE,
	BodyPart.RIGHT_UPPER_LEG to TrackerRole.RIGHT_KNEE,
	BodyPart.LEFT_FOOT to TrackerRole.LEFT_FOOT,
	BodyPart.RIGHT_FOOT to TrackerRole.RIGHT_FOOT,
	BodyPart.LEFT_SHOULDER to TrackerRole.LEFT_SHOULDER,
	BodyPart.RIGHT_SHOULDER to TrackerRole.RIGHT_SHOULDER,
	BodyPart.LEFT_HAND to TrackerRole.LEFT_HAND,
	BodyPart.RIGHT_HAND to TrackerRole.RIGHT_HAND,
)

val roleToBodyPart = bodyPartToRole.entries.associate { (k, v) -> v to k }

/**
 * Bones the driver output can accept. Used by the routing module.
 * HEAD is the HMD coming in, we never send it.
 */
val DRIVER_SUPPORTED_BONES: Set<BodyPart> = bodyPartToRole.keys - BodyPart.HEAD
