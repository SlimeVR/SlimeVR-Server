package dev.slimevr.firmware

import dev.slimevr.VRServer
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
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
typealias FirmwareManagerBehaviour = Behaviour<FirmwareManagerState, FirmwareManagerActions, FirmwareManagerContext>

class FirmwareManager(
	val context: FirmwareManagerContext,
	private val serialServer: SerialServer,
	private val scope: CoroutineScope,
) {
	private val runningJobs = mutableMapOf<String, Job>()

	suspend fun flash(
		portLocation: String,
		parts: List<FirmwarePart>,
		needManualReboot: Boolean,
		ssid: String?,
		password: String?,
		server: VRServer,
	) {
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

	suspend fun otaFlash(
		deviceIp: String,
		firmwareDeviceId: FirmwareUpdateDeviceId,
		part: FirmwarePart,
		server: VRServer,
	) {
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

	suspend fun cancelAll() {
		runningJobs.values.forEach { it.cancelAndJoin() }
		runningJobs.clear()
	}

	companion object {
		fun create(serialServer: SerialServer, scope: CoroutineScope): FirmwareManager {
			val behaviours = listOf(FirmwareManagerBaseBehaviour)
			val context = Context.create(
				initialState = FirmwareManagerState(jobs = mapOf()),
				scope = scope,
				behaviours = behaviours,
			)
			val manager = FirmwareManager(context = context, serialServer = serialServer, scope = scope)
			behaviours.forEach { it.observe(context) }
			return manager
		}
	}
}
