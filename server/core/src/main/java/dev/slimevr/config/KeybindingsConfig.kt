package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
class KeybindingsConfig {
	val fullResetBinding: String = "CTRL+ALT+SHIFT+Y"

	val yawResetBinding: String = "CTRL+ALT+SHIFT+U"

	val mountingResetBinding: String = "CTRL+ALT+SHIFT+I"

	val pauseTrackingBinding: String = "CTRL+ALT+SHIFT+O"

	var fullResetDelay: Long = 0L

	var yawResetDelay: Long = 0L

	var mountingResetDelay: Long = 0L

	var pauseTrackingDelay: Long = 0L
}
