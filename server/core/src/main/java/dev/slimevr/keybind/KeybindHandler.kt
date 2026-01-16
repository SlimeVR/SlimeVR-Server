package dev.slimevr.keybind

import dev.slimevr.VRServer
import dev.slimevr.config.KeybindingsConfig
import solarxr_protocol.rpc.Keybind
import solarxr_protocol.rpc.KeybindName
import solarxr_protocol.rpc.KeybindT
import java.util.concurrent.CopyOnWriteArrayList


class KeybindHandler(private val vrServer: VRServer) {

	private val listeners = CopyOnWriteArrayList<KeybindListener>()

	fun sendKeybinds() {
		this.listeners.forEach { listener: KeybindListener -> listener.sendKeybind()}
	}

	fun addListener(listener: KeybindListener) {
		this.listeners.add(listener)
	}

	fun removeListener(listener: KeybindListener) {
		listeners.removeIf { listener == it }
	}

	/*
	private fun getKeybinds() {
		keybinds.add(
			KeybindT().apply {
				keybindName = KeybindName.FULL_RESET
				value = vrServer.configManager.vrConfig.keybindings.fullResetBinding
				delay = vrServer.configManager.vrConfig.keybindings.fullResetDelay
			}
		)

		keybinds.add(
			KeybindT().apply {
				keybindName = KeybindName.YAW_RESET
				value = vrServer.configManager.vrConfig.keybindings.yawResetBinding
				delay = vrServer.configManager.vrConfig.keybindings.yawResetDelay
			}
		)

		keybinds.add(
			KeybindT().apply {
				keybindName = KeybindName.MOUNTING_RESET
				value = vrServer.configManager.vrConfig.keybindings.mountingResetBinding
				delay = vrServer.configManager.vrConfig.keybindings.mountingResetDelay
			}
		)

		keybinds.add(
			KeybindT().apply {
				keybindName = KeybindName.PAUSE_TRACKING
				value = vrServer.configManager.vrConfig.keybindings.pauseTrackingBinding
				delay = vrServer.configManager.vrConfig.keybindings.pauseTrackingDelay
			}
		)
	}

	 */


}
