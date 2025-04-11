package dev.slimevr.protocol.rpc.heightconfig

import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.RPCHandler
import solarxr_protocol.rpc.RpcMessageHeader

class RPCHeightConfig(
	rpcHandler: RPCHandler,
	val api: ProtocolAPI,
) {
	init {
		rpcHandler.registerPacketListener(
			0,
			this::onStartHeightConfig,
		)
		rpcHandler.registerPacketListener(
			0,
			this::onStopHeightConfig,
		)
		rpcHandler.registerPacketListener(
			0,
			this::onRequestHeightConfigStatus,
		)
	}

	private fun onStartHeightConfig(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		// Start process for both hmd height or floor height, this will run in the
		//  background and report the status
	}

	private fun onStopHeightConfig(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		// Stop height config process? May automatically report this, or just have this
		//  to cancel it
	}

	private fun onRequestHeightConfigStatus(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		// Return a status packet with info about the current measurements and status,
		//  this will be the same packet that we send during measurement?
	}
}
