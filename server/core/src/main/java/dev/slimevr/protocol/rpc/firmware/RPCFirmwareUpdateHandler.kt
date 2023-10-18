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
import solarxr_protocol.rpc.FirmwareDeviceId
import solarxr_protocol.rpc.FirmwareDeviceIdUnion
import solarxr_protocol.rpc.FirmwareUpdateRequest
import solarxr_protocol.rpc.FirmwareUpdateRequestT
import solarxr_protocol.rpc.FirmwareUpdateStatusResponse
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import solarxr_protocol.rpc.SerialDeviceIdT

class RPCFirmwareUpdateHandler(
	private val rpcHandler: RPCHandler,
	var api: ProtocolAPI,
) : FirmwareUpdateListener {

	init {
		api.server.firmwareUpdateHandler.addListener(this)
		rpcHandler.registerPacketListener(
			RpcMessage.FirmwareUpdateRequest,
			this::onFirmwareUpdateRequest
		)
		rpcHandler.registerPacketListener(
			RpcMessage.FirmwareUpdateStopQueuesRequest,
			this::onFirmwareUpdateStopQueuesRequest
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

		val method = FirmwareUpdateMethod.getById(req.flashingMethod) ?: return
		val updateDeviceId = buildUpdateDeviceId(req) ?: return

		api.server.firmwareUpdateHandler.queueFirmwareUpdate(
			req.firmwarePart,
			method,
			updateDeviceId,
			req.ssid,
			req.password
		)
	}

	override fun onUpdateStatusChange(event: UpdateStatusEvent<*>) {
		val fbb = FlatBufferBuilder(32)

		val dataUnion = FirmwareDeviceIdUnion()
		dataUnion.type = event.deviceId.type.id.toByte()
		dataUnion.value = createUpdateDeviceId(event.deviceId)

		val deviceIdOffset = FirmwareDeviceIdUnion.pack(fbb, dataUnion)

		FirmwareUpdateStatusResponse.startFirmwareUpdateStatusResponse(fbb)
		FirmwareUpdateStatusResponse.addStatus(fbb, event.status.id)
		FirmwareUpdateStatusResponse.addDeviceIdType(fbb, dataUnion.type)
		FirmwareUpdateStatusResponse.addDeviceId(fbb, deviceIdOffset)
		FirmwareUpdateStatusResponse.addProgress(fbb, event.progress.toByte())

		val update = FirmwareUpdateStatusResponse.endFirmwareUpdateStatusResponse(fbb)
		val outbound = rpcHandler.createRPCMessage(
			fbb,
			RpcMessage.FirmwareUpdateStatusResponse,
			update
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
		when (req.deviceId.type) {
			FirmwareDeviceId.solarxr_protocol_datatypes_DeviceIdTable -> {
				return UpdateDeviceId(
					FirmwareUpdateMethod.OTA,
					req.deviceId.assolarxr_protocol_datatypes_DeviceIdTable().id.id
				)
			}

			FirmwareDeviceId.SerialDeviceId -> {
				return UpdateDeviceId(
					FirmwareUpdateMethod.SERIAL,
					req.deviceId.asSerialDeviceId().port
				)
			}
		}
		return null
	}

	private fun createUpdateDeviceId(data: UpdateDeviceId<*>): Any {
		return when (data.type) {
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
				SerialDeviceIdT().apply {
					port = data.id
				}
			}
		}
	}
}
