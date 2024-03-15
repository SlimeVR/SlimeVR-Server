package dev.slimevr.protocol.rpc.setup

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.setup.HandshakeListener
import dev.slimevr.tracking.trackers.udp.UDPDevice
import solarxr_protocol.rpc.AddUnknownDeviceRequest
import solarxr_protocol.rpc.ForgetDeviceRequest
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import solarxr_protocol.rpc.UnknownDeviceHandshakeNotification

class RPCHandshakeHandler(
	private val rpcHandler: RPCHandler,
	val api: ProtocolAPI,
) : HandshakeListener {
	init {
		rpcHandler.registerPacketListener(
			RpcMessage.AddUnknownDeviceRequest,
			::onAddUnknownDevice,
		)

		rpcHandler.registerPacketListener(
			RpcMessage.ForgetDeviceRequest,
			::onForgetDevice,
		)

		this.api.server.handshakeHandler.addListener(this)
	}

	override fun onUnknownHandshake(macAddress: String) {
		val fbb = FlatBufferBuilder(32)
		val string = fbb.createString(macAddress)
		val update =
			UnknownDeviceHandshakeNotification.createUnknownDeviceHandshakeNotification(
				fbb,
				string,
			)
		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.UnknownDeviceHandshakeNotification,
			update,
		)
		fbb.finish(outbound)

		api.apiServers.forEach { apiServer ->
			apiServer.apiConnections.forEach {
				it.send(fbb.dataBuffer())
			}
		}
	}

	fun onAddUnknownDevice(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val req =
			messageHeader.message(AddUnknownDeviceRequest()) as AddUnknownDeviceRequest?
				?: return

		this.api.server.configManager.vrConfig.addKnownDevice(
			req.macAddress() ?: return,
		)
		this.api.server.configManager.saveConfig()
	}

	fun onForgetDevice(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val req = messageHeader.message(ForgetDeviceRequest()) as ForgetDeviceRequest?
			?: return

		this.api.server.configManager.vrConfig.forgetKnownDevice(
			req.macAddress() ?: return,
		)
		val device =
			this.api.server.deviceManager.devices.find { it.hardwareIdentifier == req.macAddress() }
		if (device != null && device is UDPDevice) {
			this.api.server.trackersServer.disconnectDevice(device)
		}
		this.api.server.configManager.saveConfig()
	}
}
