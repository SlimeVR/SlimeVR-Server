package dev.slimevr.solarxr

import dev.slimevr.firmware.FirmwareJobStatus
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.DeviceIdTable
import solarxr_protocol.rpc.FirmwareUpdateRequest
import solarxr_protocol.rpc.FirmwareUpdateStatusResponse
import solarxr_protocol.rpc.FirmwareUpdateStopQueuesRequest
import solarxr_protocol.rpc.OTAFirmwareUpdate
import solarxr_protocol.rpc.SerialFirmwareUpdate

val FirmwareBehaviour = SolarXRConnectionBehaviour(
	observer = { conn ->
		val scope = conn.context.scope
		val firmwareManager = conn.serverContext.firmwareManager

		var prevJobs: Map<String, FirmwareJobStatus> = firmwareManager.context.state.value.jobs

		firmwareManager.context.state
			.map { it.jobs }
			.distinctUntilChanged()
			.onEach { jobs ->
				jobs.forEach { (portLocation, jobStatus) ->
					if (prevJobs[portLocation] != jobStatus) {
						conn.sendRpc(
							FirmwareUpdateStatusResponse(
								deviceId = jobStatus.firmwareDeviceId,
								status = jobStatus.status,
								progress = jobStatus.progress.toByte(),
							),
						)
					}
				}
				prevJobs = jobs
			}
			.launchIn(scope)

		conn.rpcDispatcher.on<FirmwareUpdateRequest> { req ->
			when (val method = req.method) {
				is SerialFirmwareUpdate -> {
					val portLocation = method.deviceId?.port ?: return@on
					val parts = method.firmwarePart ?: return@on
					firmwareManager.flash(
						portLocation,
						parts,
						method.needmanualreboot,
						method.ssid,
						method.password,
						conn.serverContext,
					)
				}

				is OTAFirmwareUpdate -> {
					val deviceId = method.deviceId ?: return@on
					val part = method.firmwarePart ?: return@on
					val device = conn.serverContext.getDevice(deviceId.id.toInt()) ?: return@on
					val deviceIp = device.context.state.value.address
					firmwareManager.otaFlash(deviceIp, DeviceIdTable(id = deviceId), part, conn.serverContext)
				}

				else -> return@on
			}
		}

		conn.rpcDispatcher.on<FirmwareUpdateStopQueuesRequest> {
			firmwareManager.cancelAll()
		}
	},
)
