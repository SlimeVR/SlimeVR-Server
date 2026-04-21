package dev.slimevr.config

import dev.slimevr.Keybinds

data class KeybindData(
	var id: Int,
	var name: String,
	var binding: String,
	var delay: Float,
)

class KeybindingsConfig {
	val keybinds: MutableMap<Keybinds, KeybindData> = Keybinds.entries
		.associateWith { KeybindData(it.id, it.keybindName, it.keybind, 0f) }
		.toMutableMap()
}
