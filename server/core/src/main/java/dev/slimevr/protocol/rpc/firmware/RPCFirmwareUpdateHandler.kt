package dev.slimevr.protocol.rpc.firmware

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.firmware.FirmwareUpdateListener
import dev.slimevr.firmware.FirmwareUpdateMethod
import dev.slimevr.firmware.UpdateDeviceId
import dev.slimevr.firmware.UpdateStatusEvent
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.RPCHandler
import solarxr_protocol.datatypes.DeviceIdT
import solarxr_protocol.datatypes.DeviceIdTableT
import solarxr_protocol.rpc.FirmwareUpdateDeviceId
import solarxr_protocol.rpc.FirmwareUpdateDeviceIdUnion
import solarxr_protocol.rpc.FirmwareUpdateRequest
import solarxr_protocol.rpc.FirmwareUpdateRequestT
import solarxr_protocol.rpc.FirmwareUpdateStatusResponse
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import solarxr_protocol.rpc.SerialDevicePortT

class RPCFirmwareUpdateHandler(
	private val rpcHandler: RPCHandler,
	var api: ProtocolAPI,
) : FirmwareUpdateListener {

	init {
		api.server.firmwareUpdateHandler.addListener(this)
		rpcHandler.registerPacketListener(
			RpcMessage.FirmwareUpdateRequest,
			this::onFirmwareUpdateRequest,
		)
		rpcHandler.registerPacketListener(
			RpcMessage.FirmwareUpdateStopQueuesRequest,
			this::onFirmwareUpdateStopQueuesRequest,
		)
	}

	private fun onFirmwareUpdateStopQueuesRequest(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		api.server.firmwareUpdateHandler.cancelUpdates()
	}

	private fun onFirmwareUpdateRequest(
		conn: GenericConnection,
		messageHeader: RpcMessageHeader,
	) {
		val req =
			(messageHeader.message(FirmwareUpdateRequest()) as FirmwareUpdateRequest).unpack()
		val updateDeviceId = buildUpdateDeviceId(req) ?: return

		api.server.firmwareUpdateHandler.queueFirmwareUpdate(
			req,
			updateDeviceId,
		)
	}

	override fun onUpdateStatusChange(event: UpdateStatusEvent<*>) {
		val fbb = FlatBufferBuilder(32)

		val dataUnion = FirmwareUpdateDeviceIdUnion()
		dataUnion.type = event.deviceId.type.id
		dataUnion.value = createUpdateDeviceId(event.deviceId)

		val deviceIdOffset = FirmwareUpdateDeviceIdUnion.pack(fbb, dataUnion)

		FirmwareUpdateStatusResponse.startFirmwareUpdateStatusResponse(fbb)
		FirmwareUpdateStatusResponse.addStatus(fbb, event.status.id)
		FirmwareUpdateStatusResponse.addDeviceIdType(fbb, dataUnion.type)
		FirmwareUpdateStatusResponse.addDeviceId(fbb, deviceIdOffset)
		FirmwareUpdateStatusResponse.addProgress(fbb, event.progress.toByte())

		val update = FirmwareUpdateStatusResponse.endFirmwareUpdateStatusResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.FirmwareUpdateStatusResponse,
			update,
		)
		fbb.finish(outbound)

		api
			.apiServers.forEach { server ->
				server.apiConnections.forEach { conn ->
					conn.send(fbb.dataBuffer())
				}
			}
	}

	private fun buildUpdateDeviceId(req: FirmwareUpdateRequestT): UpdateDeviceId<Any>? {
		when (req.method.type) {
			FirmwareUpdateDeviceId.solarxr_protocol_datatypes_DeviceIdTable -> {
				return UpdateDeviceId(
					FirmwareUpdateMethod.OTA,
					req.method.asOTAFirmwareUpdate().deviceId.id,
				)
			}

			FirmwareUpdateDeviceId.SerialDevicePort -> {
				return UpdateDeviceId(
					FirmwareUpdateMethod.SERIAL,
					req.method.asSerialFirmwareUpdate().deviceId.port,
				)
			}
		}
		return null
	}

	private fun createUpdateDeviceId(data: UpdateDeviceId<*>): Any = when (data.type) {
		FirmwareUpdateMethod.NONE -> error("Unsupported method")

		FirmwareUpdateMethod.OTA -> {
			if (data.id !is Int) {
				error("Invalid state, the id type should be Int")
			}
			DeviceIdTableT().apply {
				id = DeviceIdT().apply {
					id = data.id
				}
			}
		}

		FirmwareUpdateMethod.SERIAL -> {
			if (data.id !is String) {
				error("Invalid state, the id type should be String")
			}
			SerialDevicePortT().apply {
				port = data.id
			}
		}
	}
}
