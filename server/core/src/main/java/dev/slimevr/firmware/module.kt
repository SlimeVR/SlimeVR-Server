package dev.slimevr.firmware

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.serial.SerialServer
import dev.slimevr.util.safeLaunch
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
		val jobStatus: FirmwareJobStatus,
	) : FirmwareManagerActions

	data class RemoveJob(val portLocation: String) : FirmwareManagerActions

	data object ClearJobs : FirmwareManagerActions
}

typealias FirmwareManagerContext = Context<FirmwareManagerState, FirmwareManagerActions>
typealias FirmwareManagerBehaviour = Behaviour<FirmwareManagerState, FirmwareManagerActions, FirmwareManager>

class FirmwareManager(
	val context: FirmwareManagerContext,
	private val serialServer: SerialServer,
	private val settings: Settings,
	private val flasher: FirmwareFlasher,
	private val scope: CoroutineScope,
) {
	fun startObserving() = context.observeAll(this)

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
				settings = settings,
				server = server,
				flasher = flasher,
				onStatus = { status, progress ->
					context.dispatch(
						FirmwareManagerActions.UpdateJob(
							FirmwareJobStatus(
								portLocation = portLocation,
								firmwareDeviceId = SerialDevicePort(port = portLocation),
								status = status,
								progress = progress,
							),
						),
					)
				},
				scope = scope,
			)
			context.dispatch(FirmwareManagerActions.RemoveJob(portLocation))
		}
	}

	suspend fun otaFlash(
		deviceIp: String,
		firmwareDeviceId: FirmwareUpdateDeviceId,
		part: FirmwarePart,
		server: VRServer,
	) {
		runningJobs[deviceIp]?.cancelAndJoin()
		runningJobs[deviceIp] = scope.safeLaunch {
			doOtaFlash(
				deviceIp = deviceIp,
				deviceId = (firmwareDeviceId as? DeviceIdTable)?.id ?: error("device id should exist"),
				part = part,
				server = server,
				onStatus = { status, progress ->
					context.dispatch(
						FirmwareManagerActions.UpdateJob(
							FirmwareJobStatus(
								portLocation = deviceIp,
								firmwareDeviceId = firmwareDeviceId,
								status = status,
								progress = progress,
							),
						),
					)
				},
			)
			context.dispatch(FirmwareManagerActions.RemoveJob(deviceIp))
		}
	}

	suspend fun cancelAll() {
		context.dispatch(FirmwareManagerActions.ClearJobs)
		runningJobs.values.forEach { it.cancelAndJoin() }
		runningJobs.clear()
	}

	companion object {
		fun create(ctx: Phase1ContextProvider, scope: CoroutineScope, flasher: FirmwareFlasher): FirmwareManager {
			val behaviours = listOf(FirmwareManagerBaseBehaviour)
			val context = Context.create(
				initialState = FirmwareManagerState(jobs = mapOf()),
				scope = scope,
				behaviours = behaviours,
				name = "FirmwareManager",
			)
			return FirmwareManager(
				context = context,
				serialServer = ctx.serialServer,
				settings = ctx.config.settings,
				flasher = flasher,
				scope = scope,
			)
		}
	}
}
