package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
data class VRCOSCConfig(
	// Are the OSC receiver and sender enabled?
	val enabled: Boolean = false,

	// Port to receive OSC messages from
	val portIn: Int = 9001,

	// Port to send out OSC messages at
	val portOut: Int = 9000,

	// Address to send out OSC messages at
	val address: String = "127.0.0.1",

	val trackers: MutableMap<String, Boolean> = HashMap(),

	val oscqueryEnabled: Boolean = true,
)
