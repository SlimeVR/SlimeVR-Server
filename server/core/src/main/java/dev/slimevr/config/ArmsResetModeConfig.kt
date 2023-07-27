package dev.slimevr.config

import dev.slimevr.VRServer

enum class ArmsResetModes {
	// Upper arm going back and forearm going forward
	BACK,

	// Arms going to the side
	TPOSE,

	// Arms going forward
	FORWARD,
}

class ArmsResetModeConfig {

	// Reset mode used for the arms
	var mode = ArmsResetModes.BACK

	fun updateTrackersArmsResetMode() {
		for (t in VRServer.instance.allTrackers) {
			if (t.needsReset) {
				t.resetsHandler.readArmsResetModeConfig(this)
			}
		}
	}
}
