package dev.slimevr.config

import dev.slimevr.VRServer

enum class ArmsResetModes(val id: Int) {
	// Upper arm going back and forearm going forward
	BACK(0),

	// Arms going forward
	FORWARD(1),

	// Arms going up to the sides into a tpose
	TPOSE_UP(2),

	// Arms going down to the sides from a tpose
	TPOSE_DOWN(3),
	;

	companion object {
		val values = entries.toTypedArray()

		@JvmStatic
		fun fromId(id: Int): ArmsResetModes? {
			for (filter in values) {
				if (filter.id == id) return filter
			}
			return null
		}
	}
}

class ResetsConfig {

	// Enable mounting reset for feet?
	var resetMountingFeet = false

	// Reset mode used for the arms
	var mode = ArmsResetModes.BACK

	fun updateTrackersResetsSettings() {
		for (t in VRServer.instance.allTrackers) {
			if (t.needsReset) {
				t.resetsHandler.readArmsResetModeConfig(this)
			}
		}
	}
}
