package dev.slimevr.protocol.rpc.serial

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.ProtocolAPIServer
import dev.slimevr.protocol.rpc.RPCHandler
import dev.slimevr.serial.ProvisioningListener
import dev.slimevr.serial.ProvisioningStatus
import dev.slimevr.serial.SerialPort
import solarxr_protocol.rpc.*
import java.util.function.Consumer

class RPCProvisioningHandler(var rpcHandler: RPCHandler, var api: ProtocolAPI) : ProvisioningListener {
	init {
		rpcHandler.registerPacketListener(RpcMessage.StartWifiProvisioningRequest, ::onStartWifiProvisioningRequest)
		rpcHandler.registerPacketListener(RpcMessage.StopWifiProvisioningRequest, ::onStopWifiProvisioningRequest)
		this.api.server.provisioningHandler.addListener(this)
	}

	fun onStartWifiProvisioningRequest(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val req = messageHeader
			.message(StartWifiProvisioningRequest()) as StartWifiProvisioningRequest?
		if (req == null) return
		this.api.server.provisioningHandler.start(req.ssid(), req.password(), req.port())
		conn.context.useProvisioning = true
	}

	fun onStopWifiProvisioningRequest(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val req = messageHeader
			.message(StopWifiProvisioningRequest()) as StopWifiProvisioningRequest?
		if (req == null) return
		conn.context.useProvisioning = false
		this.api.server.provisioningHandler.stop()
	}

	override fun onProvisioningStatusChange(status: ProvisioningStatus, port: SerialPort?) {
		val fbb = FlatBufferBuilder(32)

		WifiProvisioningStatusResponse.startWifiProvisioningStatusResponse(fbb)
		WifiProvisioningStatusResponse.addStatus(fbb, status.id)
		val update = WifiProvisioningStatusResponse.endWifiProvisioningStatusResponse(fbb)
		val outbound = rpcHandler
			.createRPCMessage(fbb, RpcMessage.WifiProvisioningStatusResponse, update)
		fbb.finish(outbound)

		this.forAllListeners(Consumer { conn: GenericConnection -> conn.send(fbb.dataBuffer()) })
	}

	private fun forAllListeners(action: Consumer<in GenericConnection?>?) {
		this.api
			.apiServers
			.forEach(
				Consumer { server: ProtocolAPIServer ->
					server
						.apiConnections
						.filter { conn: GenericConnection -> conn.context.useProvisioning }
						.forEach(action)
				},
			)
	}
}
