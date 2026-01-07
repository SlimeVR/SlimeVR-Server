package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
class VRCConfig {
	// List of fields ignored in vrc warnings - @see VRCConfigValidity
	val mutedWarnings: MutableList<String> = mutableListOf()
}
