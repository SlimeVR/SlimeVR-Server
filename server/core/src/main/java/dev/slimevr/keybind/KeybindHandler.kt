package dev.slimevr.keybind

import dev.slimevr.VRServer
import dev.slimevr.config.KeybindingsConfig
import solarxr_protocol.rpc.Keybind
import solarxr_protocol.rpc.KeybindName
import solarxr_protocol.rpc.KeybindT
import java.util.concurrent.CopyOnWriteArrayList


class KeybindHandler(val vrServer: VRServer) {
	private val listeners: MutableList<KeybindListener> = CopyOnWriteArrayList()
	var keybinds: MutableList<KeybindT> = mutableListOf()

	init {
		createKeybinds()
	}

	fun sendKeybinds(KeybindName: String) {
		this.listeners.forEach { it.sendKeybind()}
	}

	fun addListener(listener: KeybindListener) {
		this.listeners.add(listener)
	}

	fun removeListener(listener: KeybindListener) {
		listeners.removeIf { listener == it }
	}

	private fun createKeybinds() {
		keybinds.clear()
		keybinds.add(
			KeybindT().apply {
				keybindName = KeybindName.FULL_RESET
				keybindValue = vrServer.configManager.vrConfig.keybindings.fullResetBinding
				keybindDelay = vrServer.configManager.vrConfig.keybindings.fullResetDelay
			},
		)
		keybinds.add(
			KeybindT().apply {
				keybindName = KeybindName.YAW_RESET
				keybindValue = vrServer.configManager.vrConfig.keybindings.yawResetBinding
				keybindDelay = vrServer.configManager.vrConfig.keybindings.yawResetDelay
			},
		)
		keybinds.add(
			KeybindT().apply {
				keybindName = KeybindName.MOUNTING_RESET
				keybindValue = vrServer.configManager.vrConfig.keybindings.mountingResetBinding
				keybindDelay = vrServer.configManager.vrConfig.keybindings.mountingResetDelay
			},
		)
		keybinds.add(
			KeybindT().apply {
				keybindName = KeybindName.PAUSE_TRACKING
				keybindValue = vrServer.configManager.vrConfig.keybindings.pauseTrackingBinding
				keybindDelay = vrServer.configManager.vrConfig.keybindings.pauseTrackingDelay
			},
		)
		keybinds.add(
			KeybindT().apply {
				keybindName = KeybindName.PAUSE_TRACKING
				keybindValue = vrServer.configManager.vrConfig.keybindings.feetMountingResetBinding
				keybindDelay = vrServer.configManager.vrConfig.keybindings.feetMountingResetDelay
			}
		)
	}
	//TODO: Maybe recreating all the keybinds isn't the best idea?
	fun updateKeybinds() {
		createKeybinds()
	}
}
