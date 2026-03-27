package dev.slimevr.keybind

import dev.slimevr.VRServer
import solarxr_protocol.rpc.KeybindT
import java.util.concurrent.CopyOnWriteArrayList

class KeybindHandler(val vrServer: VRServer) {
	private val listeners: MutableList<KeybindListener> = CopyOnWriteArrayList()
	var keybinds: MutableList<KeybindT> = mutableListOf()

	init {
		createKeybinds()
	}

	fun sendKeybinds(KeybindName: String) {
		this.listeners.forEach { it.sendKeybind() }
	}

	fun addListener(listener: KeybindListener) {
		this.listeners.add(listener)
	}

	fun removeListener(listener: KeybindListener) {
		listeners.removeIf { listener == it }
	}

	private fun createKeybinds() {
		keybinds.clear()
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
	}

	// TODO: Maybe recreating all the keybinds isn't the best idea?
	fun updateKeybinds() {
		createKeybinds()
	}
}
