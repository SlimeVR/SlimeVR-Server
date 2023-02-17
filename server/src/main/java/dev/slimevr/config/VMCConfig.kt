package dev.slimevr.config

import java.nio.ByteBuffer

class VMCConfig  // Address of the VRM to be used
    : OSCConfig() {
    // Anchor the tracking at the hip?
	@JvmField
	var anchorHip = true

	var vrmJson: String? = null;
}
