package dev.slimevr.protocol.rpc.installinfo

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.RPCHandler
import io.eiren.util.logging.LogManager
import solarxr_protocol.rpc.InstalledInfoResponse.createInstalledInfoResponse
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import java.io.IOException

class RPCInstallInfoHandler(var rpcHandler: RPCHandler, var api: ProtocolAPI){
	init {
		rpcHandler.registerPacketListener(RpcMessage.InstalledInfoRequest, ::onInstalledInfoRequest)
	}

	fun onInstalledInfoRequest(conn: GenericConnection, messageHeader: RpcMessageHeader?) {
		val udevResponse = executeShellCommand("udevadm", "cat")
		var response = false
		if (udevResponse.contains("slime")) {
			response = true
		}
		else {
			response = false
		}

		val fbb = FlatBufferBuilder(1024)
		val outbound = this.rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.InstalledInfoResponse,
			createInstalledInfoResponse(fbb, response),
		)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}



	private fun executeShellCommand(vararg command: String): String = try {
		val process = ProcessBuilder(*command)
			.redirectErrorStream(true)
			.start()
		process.inputStream.bufferedReader().readText().also {
			process.waitFor()
		}
	} catch (e: IOException) {
		LogManager.warning("Error executing shell command: ${e.message}")
		"Error executing shell command: ${e.message}"
	}
}
