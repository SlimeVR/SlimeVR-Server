package dev.slimevr.config

import com.jme3.math.FastMath
import kotlinx.serialization.Serializable

// handles the tap detection config
// this involves the number of taps, the delay, and whether or not the feature is enabled
// for each reset type
@Serializable
data class TapDetectionConfig(
	val yawResetDelay: Float = 0.2f,
	val fullResetDelay: Float = 1.0f,
	val mountingResetDelay: Float = 1.0f,
	val yawResetEnabled: Boolean = true,
	val fullResetEnabled: Boolean = true,
	val setupMode: Boolean = false,
	val mountingResetEnabled: Boolean = true,
	val yawResetTaps: Int = 2,
	val fullResetTaps: Int = 3,
	val mountingResetTaps: Int = 3,
	val numberTrackersOverThreshold: Int = 1,
)
