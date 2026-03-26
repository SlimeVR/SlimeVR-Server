package dev.slimevr.firmware

import dev.slimevr.VRServer
import dev.slimevr.context.BasicBehaviour
import dev.slimevr.context.Context
import dev.slimevr.context.createContext
import dev.slimevr.serial.SerialServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.DeviceIdTable
import solarxr_protocol.rpc.FirmwarePart
import solarxr_protocol.rpc.FirmwareUpdateDeviceId
import solarxr_protocol.rpc.FirmwareUpdateStatus
import solarxr_protocol.rpc.SerialDevicePort

data class FirmwareJobStatus(
	val portLocation: String,
	val firmwareDeviceId: FirmwareUpdateDeviceId,
	val status: FirmwareUpdateStatus,
	val progress: Int = 0,
)

data class FirmwareManagerState(
	val jobs: Map<String, FirmwareJobStatus>,
)

sealed interface FirmwareManagerActions {
	data class UpdateJob(
		val portLocation: String,
		val firmwareDeviceId: FirmwareUpdateDeviceId,
		val status: FirmwareUpdateStatus,
		val progress: Int = 0,
	) : FirmwareManagerActions

	data class RemoveJob(val portLocation: String) : FirmwareManagerActions
}

typealias FirmwareManagerContext = Context<FirmwareManagerState, FirmwareManagerActions>
typealias FirmwareManagerBehaviour = BasicBehaviour<FirmwareManagerState, FirmwareManagerActions>

val FirmwareManagerBaseBehaviour = FirmwareManagerBehaviour(
	reducer = { s, a ->
		when (a) {
			is FirmwareManagerActions.UpdateJob -> s.copy(
				jobs = s.jobs +
					(
						a.portLocation to FirmwareJobStatus(
							portLocation = a.portLocation,
							firmwareDeviceId = a.firmwareDeviceId,
							status = a.status,
							progress = a.progress,
						)
						),
			)

			is FirmwareManagerActions.RemoveJob -> s.copy(jobs = s.jobs - a.portLocation)
		}
	},
	observer = null,
)

data class FirmwareManager(
	val context: FirmwareManagerContext,
	val flash: suspend (portLocation: String, parts: List<FirmwarePart>, needManualReboot: Boolean, ssid: String?, password: String?, server: VRServer) -> Unit,
	val otaFlash: suspend (deviceIp: String, firmwareDeviceId: FirmwareUpdateDeviceId, part: FirmwarePart, VRServer) -> Unit,
	val cancelAll: suspend () -> Unit,
)

fun createFirmwareManager(
	serialServer: SerialServer,
	scope: CoroutineScope,
): FirmwareManager {
	val behaviours = listOf(FirmwareManagerBaseBehaviour)

	val context = createContext(
		initialState = FirmwareManagerState(jobs = mapOf()),
		reducers = behaviours.map { it.reducer },
		scope = scope,
	)

	val runningJobs = mutableMapOf<String, Job>()

	val flash: suspend (String, List<FirmwarePart>, Boolean, String?, String?, VRServer) -> Unit = { portLocation, parts, needManualReboot, ssid, password, server ->
		runningJobs[portLocation]?.cancelAndJoin()
		runningJobs[portLocation] = scope.launch {
			doSerialFlash(
				portLocation = portLocation,
				parts = parts,
				needManualReboot = needManualReboot,
				ssid = ssid,
				password = password,
				serialServer = serialServer,
				server = server,
				onStatus = { status, progress ->
					context.dispatch(
						FirmwareManagerActions.UpdateJob(
							portLocation = portLocation,
							firmwareDeviceId = SerialDevicePort(port = portLocation),
							status = status,
							progress = progress,
						),
					)
				},
				scope = scope,
			)
		}
	}

	val otaFlash: suspend (String, FirmwareUpdateDeviceId, FirmwarePart, VRServer) -> Unit = { deviceIp, firmwareDeviceId, part, server ->
		runningJobs[deviceIp]?.cancelAndJoin()
		runningJobs[deviceIp] = scope.launch {
			doOtaFlash(
				deviceIp = deviceIp,
				deviceId = (firmwareDeviceId as? DeviceIdTable)?.id ?: error("device id should exist"),
				part = part,
				server = server,
				onStatus = { status, progress ->
					context.dispatch(
						FirmwareManagerActions.UpdateJob(
							portLocation = deviceIp,
							firmwareDeviceId = firmwareDeviceId,
							status = status,
							progress = progress,
						),
					)
				},
			)
		}
	}

	val cancelAll: suspend () -> Unit = {
		runningJobs.values.forEach { it.cancelAndJoin() }
		runningJobs.clear()
	}

	val manager = FirmwareManager(
		context = context,
		flash = flash,
		otaFlash = otaFlash,
		cancelAll = cancelAll,
	)

	behaviours.map { it.observer }.forEach { it?.invoke(context) }
	return manager
}
