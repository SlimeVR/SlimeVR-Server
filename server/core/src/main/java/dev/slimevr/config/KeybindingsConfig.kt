package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
data class KeybindingsConfig(
	val fullResetBinding: String = "CTRL+ALT+SHIFT+Y",
	val yawResetBinding: String = "CTRL+ALT+SHIFT+U",
	val mountingResetBinding: String = "CTRL+ALT+SHIFT+I",
	val pauseTrackingBinding: String = "CTRL+ALT+SHIFT+O",
	val fullResetDelay: Long = 0L,
	val yawResetDelay: Long = 0L,
	val mountingResetDelay: Long = 0L,
	val pauseTrackingDelay: Long = 0L,
)
