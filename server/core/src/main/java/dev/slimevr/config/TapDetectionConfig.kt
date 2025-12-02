package dev.slimevr.config

import com.jme3.math.FastMath

// handles the tap detection config
// this involves the number of taps, the delay, and whether or not the feature is enabled
// for each reset type
class TapDetectionConfig {
	var yawResetDelay = 0.2f
	var fullResetDelay = 1.0f
	var mountingResetDelay = 1.0f
	var yawResetEnabled = true
	var fullResetEnabled = true
	var mountingResetEnabled = true
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
