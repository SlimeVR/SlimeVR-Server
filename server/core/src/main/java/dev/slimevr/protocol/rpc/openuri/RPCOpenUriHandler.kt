package dev.slimevr.protocol.rpc.openuri

import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.RPCHandler
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import dev.hannah.portals.PortalManager
import dev.slimevr.SLIMEVR_IDENTIFIER

class RPCOpenUriHandler(var rpcHandler: RPCHandler, var api: ProtocolAPI) {

	init {
		rpcHandler.registerPacketListener(RpcMessage.OpenUriRequest, ::onOpenUriRequest)
	}

	fun onOpenUriRequest(conn: GenericConnection, messageHeader: RpcMessageHeader?) {
		val portalManager = PortalManager(SLIMEVR_IDENTIFIER)
		portalManager.openGlobalShortcutsSettings()
	}
}
