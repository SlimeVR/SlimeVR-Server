package dev.slimevr.config

import solarxr_protocol.rpc.KeybindId

class KeybindingsConfig {
	val keybinds: MutableMap<Int, KeybindData> = mutableMapOf()

	init {
		keybinds[KeybindId.FULL_RESET] =
		KeybindData(
			KeybindId.FULL_RESET,
			"full-reset",
			"CTRL+ALT+SHIFT+Y",
			0f
		)
		keybinds[KeybindId.YAW_RESET] =
			KeybindData(
				KeybindId.YAW_RESET,
				"yaw-reset",
				"CTRL+ALT+SHIFT+U",
				0f
			)
		keybinds[KeybindId.MOUNTING_RESET] =
			KeybindData(
				KeybindId.MOUNTING_RESET,
				"mounting-reset",
				"CTRL+ALT+SHIFT+I",
				0f
			)
		keybinds[KeybindId.FEET_MOUNTING_RESET] =
			KeybindData(
				KeybindId.FEET_MOUNTING_RESET,
				"feet-mounting-reset",
				"CTRL+ALT+SHIFT+P",
				0f
			)
		keybinds[KeybindId.PAUSE_TRACKING] =
			KeybindData(
				KeybindId.PAUSE_TRACKING,
				"pause-tracking",
				"CTRL+ALT+SHIFT+O",
				0f
			)
	}
}

data class KeybindData(
	var id: Int,
	var name: String,
	var binding: String,
	var delay: Float
)
