package dev.slimevr.solarxr

import solarxr_protocol.rpc.ServerInfosRequest
import solarxr_protocol.rpc.ServerInfosResponse

class ServerInfos(private val resolveLocalIp: () -> String) {
	val localIp: String get() = resolveLocalIp()
}

class ServerInfosBehaviour(
	private val serverInfos: ServerInfos,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<ServerInfosRequest> {
			receiver.sendRpc(ServerInfosResponse(localIp = serverInfos.localIp))
		}.launchIn(receiver.context.scope)
	}
}
