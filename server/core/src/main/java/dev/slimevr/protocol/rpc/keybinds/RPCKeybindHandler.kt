package dev.slimevr.protocol.rpc.keybinds

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.Keybinding
import dev.slimevr.keybind.KeybindListener
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.protocol.ProtocolAPI
import jdk.internal.joptsimple.internal.Messages.message
import solarxr_protocol.rpc.ChangeKeybindRequest
import solarxr_protocol.rpc.KeybindName
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
		}
	)


	private fun onKeybindRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
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
		val req = (messageHeader.message(ChangeKeybindRequest()) as ChangeKeybindRequest).unpack()

		keybindingConfig.fullResetBinding = req.keybind[KeybindName.FULL_RESET].keybindValue
		keybindingConfig.fullResetDelay = req.keybind[KeybindName.FULL_RESET].keybindDelay

		keybindingConfig.yawResetBinding = req.keybind[KeybindName.YAW_RESET].keybindValue
		keybindingConfig.yawResetDelay = req.keybind[KeybindName.YAW_RESET].keybindDelay

		keybindingConfig.mountingResetBinding = req.keybind[KeybindName.MOUNTING_RESET].keybindValue
		keybindingConfig.mountingResetDelay = req.keybind[KeybindName.MOUNTING_RESET].keybindDelay

		keybindingConfig.pauseTrackingBinding = req.keybind[KeybindName.PAUSE_TRACKING].keybindValue
		keybindingConfig.pauseTrackingDelay = req.keybind[KeybindName.PAUSE_TRACKING].keybindDelay

		keybindingConfig.feetMountingResetBinding = req.keybind[KeybindName.FEET_MOUNTING_RESET].keybindValue
		keybindingConfig.feetMountingResetDelay = req.keybind[KeybindName.FEET_MOUNTING_RESET].keybindDelay

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

	companion object {
		const val FULL_RESET = KeybindName.FULL_RESET
		const val YAW_RESET = KeybindName.YAW_RESET
		const val MOUNTING_RESET = KeybindName.MOUNTING_RESET
		const val PAUSE_TRACKING = KeybindName.PAUSE_TRACKING
		const val FEET_MOUNTING_RESET = KeybindName.FEET_MOUNTING_RESET
	}
}
