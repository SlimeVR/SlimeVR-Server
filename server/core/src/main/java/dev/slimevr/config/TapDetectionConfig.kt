package dev.slimevr.config

class TapDetectionConfig {
	var yawResetDelay = 0.2f
	var fullResetDelay = 1.0f
	var mountingResetDelay = 1.0f
	var yawResetEnabled = true
	var yawResetDesignation: String? = null
	var fullResetEnabled = true
	var fullResetDesignation: String? = null
	var mountingResetEnabled = true
	var mountingResetDesignation: String? = null
	var setupMode = false
	var yawResetTaps = 2
		set(yawResetTaps) {
			field = yawResetTaps.coerceIn(2, 10)
		}
	var fullResetTaps = 3
		set(fullResetTaps) {
			field = fullResetTaps.coerceIn(2, 10)
		}
	var mountingResetTaps = 3
		set(mountingResetTaps) {
			field = mountingResetTaps.coerceIn(2, 10)
		}
	var numberTrackersOverThreshold = 1
}
