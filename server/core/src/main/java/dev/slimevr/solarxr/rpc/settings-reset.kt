package dev.slimevr.solarxr.rpc

import dev.slimevr.AppContextProvider
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.udp.forgetAllUdpDevices
import solarxr_protocol.rpc.SettingsResetRequest

class SettingsResetBehaviour(
	private val appContext: AppContextProvider,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<SettingsResetRequest> {
			forgetAllUdpDevices(appContext)
			appContext.config.reset()
		}.launchIn(receiver.context.scope)
	}
}
