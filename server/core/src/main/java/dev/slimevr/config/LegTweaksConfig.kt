package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
data class LegTweaksConfig(
	val correctionStrength: Float = 0.3f,
	val alwaysUseFloorclip: Boolean = false,
)
