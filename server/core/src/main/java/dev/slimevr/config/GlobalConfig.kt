package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
data class GlobalConfig(
	val userProfile: String = "default",
	val settingsProfile: String = "default",
	val version: String = CONFIG_VERSION.toString(),
) {
	companion object {
		const val CONFIG_VERSION = 1
	}
}
