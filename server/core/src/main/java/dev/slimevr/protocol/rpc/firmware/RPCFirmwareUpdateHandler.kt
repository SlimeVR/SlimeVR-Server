package dev.slimevr.protocol.rpc.firmware

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.firmware.FirmwareUpdateListener
import dev.slimevr.firmware.FirmwareUpdateMethod
import dev.slimevr.firmware.UpdateDeviceId
import dev.slimevr.firmware.UpdateStatusEvent
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.RPCHandler
import solarxr_protocol.datatypes.DeviceId
import solarxr_protocol.datatypes.DeviceIdTable
import solarxr_protocol.rpc.FirmwareUpdateDeviceId
import solarxr_protocol.rpc.FirmwareUpdateRequest
import solarxr_protocol.rpc.FirmwareUpdateStatusResponse
import solarxr_protocol.rpc.OTAFirmwareUpdate
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import solarxr_protocol.rpc.SerialDevicePort
import solarxr_protocol.rpc.SerialFirmwareUpdate

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
		val req = messageHeader.message(FirmwareUpdateRequest()) as FirmwareUpdateRequest
		val updateDeviceId = buildUpdateDeviceId(req) ?: return

		api.server.firmwareUpdateHandler.queueFirmwareUpdate(
			req,
			updateDeviceId,
		)
	}

	override fun onUpdateStatusChange(event: UpdateStatusEvent<*>) {
		val fbb = FlatBufferBuilder(32)

		FirmwareUpdateStatusResponse.startFirmwareUpdateStatusResponse(fbb)
		FirmwareUpdateStatusResponse.addStatus(fbb, event.status.id)
		FirmwareUpdateStatusResponse.addDeviceIdType(fbb, event.deviceId.type.id)
		FirmwareUpdateStatusResponse.addDeviceId(fbb, createUpdateDeviceId(fbb, event.deviceId))
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

	private fun buildUpdateDeviceId(req: FirmwareUpdateRequest): UpdateDeviceId<Any>? {
		when (req.methodType) {
			FirmwareUpdateDeviceId.solarxr_protocol_datatypes_DeviceIdTable -> {
				return UpdateDeviceId(
					FirmwareUpdateMethod.OTA,
					(req.method(OTAFirmwareUpdate()) as OTAFirmwareUpdate).deviceId?.id?.toInt() ?: error("whar"),
				)
			}

			FirmwareUpdateDeviceId.SerialDevicePort -> {
				return UpdateDeviceId(
					FirmwareUpdateMethod.SERIAL,
					(req.method(SerialFirmwareUpdate()) as SerialFirmwareUpdate).deviceId?.port ?: error("whar"),
				)
			}
		}
		return null
	}

	private fun createUpdateDeviceId(fbb: FlatBufferBuilder, data: UpdateDeviceId<*>): Int = when (data.type) {
		FirmwareUpdateMethod.NONE -> error("Unsupported method")

		FirmwareUpdateMethod.OTA -> {
			if (data.id !is Int) {
				error("Invalid state, the id type should be Int")
			}
			DeviceIdTable.startDeviceIdTable(fbb)
			DeviceIdTable.addId(fbb, DeviceId.createDeviceId(fbb, data.id.toUByte()))
			DeviceIdTable.endDeviceIdTable(fbb)
		}

		FirmwareUpdateMethod.SERIAL -> {
			if (data.id !is String) {
				error("Invalid state, the id type should be String")
			}

			val portOffset = fbb.createString(data.id)
			SerialDevicePort.createSerialDevicePort(fbb, portOffset)
		}
	}
}
