package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
data class HIDConfig(
	val trackersOverHID: Boolean = false,
)
