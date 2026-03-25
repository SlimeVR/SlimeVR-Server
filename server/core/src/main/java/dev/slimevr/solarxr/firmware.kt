package dev.slimevr.solarxr

import dev.slimevr.firmware.FirmwareJobStatus
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.FirmwareUpdateRequest
import solarxr_protocol.rpc.FirmwareUpdateStatusResponse
import solarxr_protocol.rpc.FirmwareUpdateStopQueuesRequest
import solarxr_protocol.rpc.SerialDevicePort
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
								deviceId = SerialDevicePort(port = portLocation),
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
			val method = req.method as? SerialFirmwareUpdate ?: return@on
			val portLocation = method.deviceId?.port ?: return@on
			val parts = method.firmwarePart ?: return@on
			firmwareManager.flash(
				portLocation,
				parts,
				method.needmanualreboot,
				method.ssid,
				method.password,
				conn.serverContext
			)
		}

		conn.rpcDispatcher.on<FirmwareUpdateStopQueuesRequest> {
			firmwareManager.cancelAll()
		}
	},
)
