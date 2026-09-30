package dev.slimevr.solarxr.rpc

import dev.slimevr.config.AppConfig
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import solarxr_protocol.rpc.SettingsResetRequest

class SettingsResetBehaviour(
	private val config: AppConfig,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<SettingsResetRequest> { config.reset() }.launchIn(receiver.context.scope)
	}
}
