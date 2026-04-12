package dev.slimevr.protocol.rpc.hid

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.ProtocolAPIServer
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.hid.HIDListener
import dev.slimevr.tracking.trackers.hid.HIDCommon
import solarxr_protocol.rpc.UnknownHIDDeviceHandshakeNotification
import solarxr_protocol.rpc.HIDAllReceiversCommandRequest
import solarxr_protocol.rpc.HIDTrackerCommandRequest
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import java.util.function.Consumer

class RPCHIDHandler(var rpcHandler: RPCHandler, var api: ProtocolAPI) : HIDListener {
	init {
		rpcHandler.registerPacketListener(RpcMessage.HIDTrackerCommandRequest, ::onTrackerRequest)
	}

	fun onTrackerRequest(conn: GenericConnection, messageHeader: RpcMessageHeader) {
		val req = messageHeader.message(HIDTrackerCommandRequest()) as? HIDTrackerCommandRequest ?: return
		val hwid = req.deviceAddress()
		val command = req.command()
		api.server.hidTrackersServer!!.parseCommand(hwid, command)
	}

}
