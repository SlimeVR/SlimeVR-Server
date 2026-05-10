package dev.slimevr.solarxr

import dev.slimevr.VRServer
import dev.slimevr.firmware.FirmwareJobStatus
import dev.slimevr.firmware.FirmwareManager
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

class FirmwareBehaviour(private val server: VRServer, private val firmwareManager: FirmwareManager) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		val scope = receiver.context.scope

		var prevJobs: Map<String, FirmwareJobStatus> = firmwareManager.context.state.value.jobs

		firmwareManager.context.state
			.map { it.jobs }
			.distinctUntilChanged()
			.onEach { jobs ->
				jobs.forEach { (portLocation, jobStatus) ->
					if (prevJobs[portLocation] != jobStatus) {
						receiver.sendRpc(
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

		receiver.rpcDispatcher.on<FirmwareUpdateRequest> { req ->
			when (val method = req.method) {
				is SerialFirmwareUpdate -> {
					val portLocation = method.deviceId?.port ?: return@on
					val parts = method.firmwarePart ?: return@on
					firmwareManager.flash(
						portLocation,
						parts,
						method.needmanualreboot == true,
						method.ssid,
						method.password,
						server,
					)
				}

				is OTAFirmwareUpdate -> {
					val deviceId = method.deviceId ?: return@on
					val part = method.firmwarePart ?: return@on
					val device = server.getDevice(deviceId.id.toInt()) ?: return@on
					val deviceIp = device.context.state.value.address
					firmwareManager.otaFlash(deviceIp, DeviceIdTable(id = deviceId), part, server)
				}

				else -> return@on
			}
		}

		receiver.rpcDispatcher.on<FirmwareUpdateStopQueuesRequest> {
			firmwareManager.cancelAll()
		}
	}
}
