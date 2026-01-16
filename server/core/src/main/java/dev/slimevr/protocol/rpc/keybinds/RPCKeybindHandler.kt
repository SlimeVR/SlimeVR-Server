package dev.slimevr.protocol.rpc.keybinds

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.keybind.KeybindListener
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.protocol.ProtocolAPI
import solarxr_protocol.rpc.KeybindName
import solarxr_protocol.rpc.KeybindResponse
import solarxr_protocol.rpc.KeybindResponseT
import solarxr_protocol.rpc.KeybindT
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader

class RPCKeybindHandler(
	private var rpcHandler: RPCHandler,
	var api: ProtocolAPI
) : KeybindListener {

	init {
		api.server.keybindHandler.addListener(this)

		rpcHandler.registerPacketListener(RpcMessage.KeybindRequest, ::onKeybindRequest)
	}

	fun buildKeybindResponse(fbb: FlatBufferBuilder) : Int = KeybindResponse.pack(
		fbb,
		KeybindResponseT().apply {
			//keybinds = api.server.keybindHandler.keybinds.toTypedArray()
		}
	)


	private fun onKeybindRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val fbb = FlatBufferBuilder(32)
		val response = buildKeybindResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.KeybindResponse,
			response
		)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())

	}

	override fun onKeybindUpdate() {
		val fbb = FlatBufferBuilder(32)
		val response = buildKeybindResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.KeybindResponse,
			response
		)
		fbb.finish(outbound)
	}

	override fun sendKeybind() {

	}
}
