package dev.slimevr.protocol.rpc.setup

import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.reset.ResetListener

class RPCTapSetupHandler(val rpcHandler: RPCHandler, val api: ProtocolAPI) : ResetListener {
	override fun onStarted(resetType: Int) {
		TODO("Not yet implemented")
	}

}
