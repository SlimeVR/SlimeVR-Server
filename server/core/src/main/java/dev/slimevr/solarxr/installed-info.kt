package dev.slimevr.solarxr

import solarxr_protocol.rpc.InstalledInfoRequest
import solarxr_protocol.rpc.InstalledInfoResponse

class InstalledInfoBehaviour : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<InstalledInfoRequest> {
			val udevRulesInstalled = receiver.appContext.featureFlags.udevRulesInstalled ?: return@on
			receiver.sendRpc(InstalledInfoResponse(isudevinstalled = udevRulesInstalled))
		}
	}
}
