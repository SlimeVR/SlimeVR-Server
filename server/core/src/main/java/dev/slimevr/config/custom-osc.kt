package dev.slimevr.config

import kotlinx.serialization.Serializable
import solarxr_protocol.datatypes.BodyPart

enum class OscAxisSource {
	POSITION_X,
	POSITION_Y,
	POSITION_Z,
	ROTATION_PITCH,
	ROTATION_YAW,
	ROTATION_ROLL,
	QUAT_X,
	QUAT_Y,
	QUAT_Z,
	QUAT_W,
}

@Serializable
data class CustomOscParamMapping(
	val axis: OscAxisSource,
	val address: String,
)

@Serializable
data class CustomOscTrackerMapping(
	@Serializable(with = BodyPartSerializer::class)
	val bodyPart: BodyPart? = null,
	val params: List<CustomOscParamMapping> = emptyList(),
)

@Serializable
data class CustomOscProfile(
	val id: String,
	val name: String,
	val enabled: Boolean = true,
	val address: String = "127.0.0.1",
	val port: Int = 9000,
	val trackers: List<CustomOscTrackerMapping> = emptyList(),
)

@Serializable
data class CustomOscConfig(
	val enabled: Boolean = false,
	val profiles: List<CustomOscProfile> = emptyList(),
)
