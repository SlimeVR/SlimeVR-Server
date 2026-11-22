package dev.slimevr.solarxr.rpc

import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import solarxr_protocol.rpc.ServerInfosRequest
import solarxr_protocol.rpc.ServerInfosResponse

class ServerInfos(private val resolveLocalIp: () -> String) {
	val localIp: String get() = resolveLocalIp()
}

class ServerInfosBehaviour(
	private val serverInfos: ServerInfos,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.onRpc<ServerInfosRequest> { _, txId ->
			receiver.sendRpc(ServerInfosResponse(localIp = serverInfos.localIp), txId)
		}.launchIn(receiver.context.scope)
	}
}
