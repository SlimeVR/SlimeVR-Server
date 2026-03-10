package dev.slimevr

data class FeatureFlags(
	var steam: Boolean = false,
	var steamArgs: String = "",
	var installer: Boolean = false,
	var installerArgs: String = "",
	var noUdev: Boolean = false,
)
