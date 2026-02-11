package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
data class OverlayConfig(
	val isMirrored: Boolean = false,
	val isVisible: Boolean = false,
)
