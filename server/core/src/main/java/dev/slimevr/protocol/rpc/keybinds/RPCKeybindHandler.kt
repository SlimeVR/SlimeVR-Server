package dev.slimevr.protocol.rpc.keybinds

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.config.KeybindData
import dev.slimevr.keybind.KeybindListener
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.protocol.ProtocolAPI
import jdk.internal.joptsimple.internal.Messages.message
import solarxr_protocol.rpc.ChangeKeybindRequest
import solarxr_protocol.rpc.KeybindId
import solarxr_protocol.rpc.KeybindRequest
import solarxr_protocol.rpc.KeybindResponse
import solarxr_protocol.rpc.KeybindResponseT
import solarxr_protocol.rpc.KeybindT
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader

class RPCKeybindHandler(
	var rpcHandler: RPCHandler,
	var api: ProtocolAPI
) : KeybindListener {

	val keybindingConfig = api.server.configManager.vrConfig.keybindings

	init {
		this.api.server.keybindHandler.addListener(this)

		rpcHandler.registerPacketListener(RpcMessage.KeybindRequest, ::onKeybindRequest)
		rpcHandler.registerPacketListener(RpcMessage.ChangeKeybindRequest, ::onChangeKeybindRequest)
	}

	//TODO: Figure out a way to "refresh" the keybind array here.
	private fun buildKeybindResponse(fbb: FlatBufferBuilder) : Int = KeybindResponse.pack(
		fbb,
		KeybindResponseT().apply {
			keybind = api.server.keybindHandler.keybinds.toTypedArray()
			defaultKeybinds = api.server.keybindHandler.defaultKeybinds.toTypedArray()
		}
	)


	private fun onKeybindRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		println("Received KeybindsRequest")
		val fbb = FlatBufferBuilder(32)
		val response = buildKeybindResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.KeybindResponse,
			response,
		)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	private fun onChangeKeybindRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		println("Received Keybinds Change request")
		val req = (messageHeader.message(ChangeKeybindRequest()) as ChangeKeybindRequest).unpack()

		keybindingConfig.keybinds[req.keybind.keybindId] = KeybindData(req.keybind.keybindId, req.keybind.keybindNameId, req.keybind.keybindValue, req.keybind.keybindDelay)

		api.server.configManager.saveConfig()
		api.server.keybindHandler.updateKeybinds()
	}

	override fun onKeybindUpdate() {
		/*
		val fbb = FlatBufferBuilder(32)
		val response = buildKeybindResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.KeybindResponse,
			response
		)
		fbb.finish(outbound)

		 */
	}



	override fun sendKeybind() {

	}
}
