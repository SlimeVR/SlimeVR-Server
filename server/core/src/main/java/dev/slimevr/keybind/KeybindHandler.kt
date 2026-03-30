package dev.slimevr.keybind

import dev.slimevr.VRServer
import dev.slimevr.config.KeybindingsConfig
import solarxr_protocol.rpc.KeybindT
import java.util.concurrent.CopyOnWriteArrayList

class KeybindHandler(val vrServer: VRServer) {
	private val listeners: MutableList<KeybindListener> = CopyOnWriteArrayList()
	var keybinds: MutableList<KeybindT> = mutableListOf()
	var defaultKeybinds: MutableList<KeybindT> = mutableListOf()

	init {
		createKeybinds()
	}

	fun addListener(listener: KeybindListener) {
		this.listeners.add(listener)
	}

	fun removeListener(listener: KeybindListener) {
		listeners.removeIf { listener == it }
	}

	private fun createKeybinds() {
		keybinds.clear()
		defaultKeybinds.clear()
		vrServer.configManager.vrConfig.keybindings.keybinds.forEach { (i, keybind) ->
			keybinds.add(
				KeybindT().apply {
					keybindId = keybind.id
					keybindNameId = keybind.name
					keybindValue = keybind.binding
					keybindDelay = keybind.delay
				},
			)
		}

		val binds = KeybindingsConfig().keybinds
		binds.forEach { (i, keybind) ->
			defaultKeybinds.add(
				KeybindT().apply {
					keybindId = keybind.id
					keybindNameId = keybind.name
					keybindValue = keybind.binding
					keybindDelay = keybind.delay
				},
			)
		}
	}

	fun updateKeybinds() {
		createKeybinds()
	}
}
