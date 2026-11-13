package dev.slimevr.solarxr.rpc

import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import solarxr_protocol.rpc.InstalledInfoRequest
import solarxr_protocol.rpc.InstalledInfoResponse

class InstalledInfoBehaviour : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.onRpc<InstalledInfoRequest> { _, txId ->
			val udevRulesInstalled = receiver.appContext.featureFlags.udevRulesInstalled ?: return@onRpc
			receiver.sendRpc(InstalledInfoResponse(isUdevInstalled = udevRulesInstalled), txId)
		}.launchIn(receiver.context.scope)
	}
}
