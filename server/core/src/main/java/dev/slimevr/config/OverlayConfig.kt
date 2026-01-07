package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
class OverlayConfig {
	var isMirrored: Boolean = false
	var isVisible: Boolean = false
}
