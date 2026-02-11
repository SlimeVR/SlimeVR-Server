package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
data class VMCConfig(
	// Are the OSC receiver and sender enabled?
	val enabled: Boolean = false,

	// Port to receive OSC messages from
	val portIn: Int = 39540,

	// Port to send out OSC messages at
	val portOut: Int = 39539,

	// Address to send out OSC messages at
	val address: String = "127.0.0.1",

	// Anchor the tracking at the hip?
	val anchorHip: Boolean = true,

	// JSON part of the VRM to be used
	val vrmJson: String? = null,

	// Mirror the tracking before sending it (turn left <=> turn right, left leg <=> right leg)
	val mirrorTracking: Boolean = false,
)
