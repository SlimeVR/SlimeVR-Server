package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
class VMCConfig : OSCConfig() {

	// Anchor the tracking at the hip?
	var anchorHip = true

	// JSON part of the VRM to be used
	var vrmJson: String? = null

	// Mirror the tracking before sending it (turn left <=> turn right, left leg <=> right leg)
	var mirrorTracking = false

	init {
		portIn = 39540
		portOut = 39539
	}
}
