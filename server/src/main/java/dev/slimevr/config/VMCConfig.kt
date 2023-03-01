package dev.slimevr.config

class VMCConfig : // Address of the VRM to be used
	OSCConfig {
	// Anchor the tracking at the hip?

	constructor() : super() {
		super.setPortIn(39540)
		super.setPortOut(39539)
	}

	var anchorHip = true

	var vrmJson: String? = null
}
