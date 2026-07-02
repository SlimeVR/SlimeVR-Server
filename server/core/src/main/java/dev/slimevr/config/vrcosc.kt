package dev.slimevr.config

import kotlinx.serialization.Serializable

internal const val DEFAULT_VRC_OSC_PORT_IN: Int = 9001
internal const val DEFAULT_VRC_OSC_PORT_OUT: Int = 9000
internal const val DEFAULT_VRC_OSC_ADDRESS: String = "127.0.0.1"

@Serializable
data class VRCOSCTrackers(
	val head: Boolean = false,
	val chest: Boolean = false,
	val waist: Boolean = true,
	val knees: Boolean = false,
	val feet: Boolean = true,
	val elbows: Boolean = false,
	val hands: Boolean = false,
)

@Serializable
data class VRCOSCManualNetworkConfig(
	val portIn: Int,
	val portOut: Int,
	val address: String,
)

@Serializable
data class VRCOSCConfig(
	val enabled: Boolean = false,
	val manualNetwork: VRCOSCManualNetworkConfig? = null,
	val trackers: VRCOSCTrackers = VRCOSCTrackers(),
)
