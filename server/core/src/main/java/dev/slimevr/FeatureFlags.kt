package dev.slimevr

data class FeatureFlags(
	var steam: Boolean = false,
	var steamArgs: String = "",
	var skipCheckUdev: Boolean = false,
)
