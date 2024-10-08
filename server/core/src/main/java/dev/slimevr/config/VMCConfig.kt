package dev.slimevr.config

class VMCConfig : OSCConfig() {

	// Anchor the tracking at the hip?
	var anchorHip = true

	// JSON part of the VRM to be used
	var vrmJson: String? = null

	// Mirror the tracking before sending it (turn left <=> turn right, left leg <=> right leg)
	var mirrorTracking = false
}
