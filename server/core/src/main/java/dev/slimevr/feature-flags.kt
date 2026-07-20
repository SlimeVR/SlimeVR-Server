package dev.slimevr

import solarxr_protocol.rpc.KeybindSupport

data class FeatureFlags(
	var steam: Boolean = false,
	var skipCheckUdev: Boolean = false,
	var udevRulesInstalled: Boolean? = null,
	var keybindSupport: KeybindSupport = KeybindSupport.UNSUPPORTED,
)
