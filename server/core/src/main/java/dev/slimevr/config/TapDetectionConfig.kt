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
			field = FastMath.clamp(yawResetTaps.toFloat(), 2f, 10f).toInt()
		}
	var fullResetTaps = 3
		set(fullResetTaps) {
			field = FastMath.clamp(fullResetTaps.toFloat(), 2f, 10f).toInt()
		}
	var mountingResetTaps = 3
		set(mountingResetTaps) {
			field = FastMath.clamp(mountingResetTaps.toFloat(), 2f, 10f).toInt()
		}
	var numberTrackersOverThreshold = 1
}
