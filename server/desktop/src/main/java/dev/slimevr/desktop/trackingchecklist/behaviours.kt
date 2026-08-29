package dev.slimevr.desktop.trackingchecklist

import com.sun.jna.Native
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.Kernel32Util
import com.sun.jna.platform.win32.Tlhelp32
import com.sun.jna.platform.win32.WinBase
import com.sun.jna.platform.win32.WinDef
import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.desktop.Driver
import dev.slimevr.desktop.getSteamVRDriversList
import dev.slimevr.driver.DriverBridgeSource
import dev.slimevr.logging.AppLogger
import dev.slimevr.trackingchecklist.TrackingChecklist
import dev.slimevr.trackingchecklist.TrackingChecklistActions
import dev.slimevr.trackingchecklist.TrackingChecklistBehaviourType
import dev.slimevr.util.timeSource
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import solarxr_protocol.rpc.TrackingChecklistSteamVRDisconnected
import solarxr_protocol.rpc.TrackingChecklistStep
import solarxr_protocol.rpc.TrackingChecklistStepId
import solarxr_protocol.rpc.TrackingChecklistStepVisibility
import java.io.IOException
import java.net.ConnectException
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark

data class Process(val pid: ULong, val name: String)

private suspend fun getRunningProcesses(): List<Process> = when (CURRENT_PLATFORM) {
	Platform.LINUX -> buildList {
		val psProc = try {
			ProcessBuilder("ps", "-eo", "pid,comm").redirectErrorStream(true).start()
		} catch (_: IOException) {
			return@buildList
		}

		val lines = withContext(Dispatchers.IO) { psProc.inputStream.bufferedReader().readLines() }
		// skip the header
		for (line in lines.slice(1 until lines.size)) {
			val data = line.trimStart().split(' ')
			add(Process(data[0].toULong(), data[1]))
		}
	}

	Platform.WINDOWS -> buildList {
		val k32 = Kernel32.INSTANCE
		val snapshot = k32.CreateToolhelp32Snapshot(
			Tlhelp32.TH32CS_SNAPPROCESS,
			WinDef.DWORD(0),
		)
		if (WinBase.INVALID_HANDLE_VALUE.equals(snapshot)) {
			return@buildList
		}

		try {
			val entry = Tlhelp32.PROCESSENTRY32()
			if (!k32.Process32First(snapshot, entry)) {
				val err = k32.GetLastError()
				AppLogger.checklist.warn("Failed to retrieve process information: ${Kernel32Util.formatMessage(err)} (code $err)")
				return@buildList
			}

			do {
				add(Process(entry.th32ProcessID.toLong().toULong(), Native.toString(entry.szExeFile)))
			} while (k32.Process32Next(snapshot, entry))
		} finally {
			k32.CloseHandle(snapshot)
		}
	}

	else -> emptyList()
}

// Enumerating every process is orders of magnitude costlier than the loopback request it guards, so it
// only runs to disambiguate a refused connection, and only this often.
private val PROCESS_SCAN_INTERVAL = 10.seconds

private fun Throwable.isConnectionRefused(): Boolean = generateSequence(this, Throwable::cause).any { it is ConnectException }

private inline fun buildSteamVRDriverStep(driverEnabled: Boolean = true, connected: Boolean = false, installed: Boolean = true, blocked: Boolean = false, enabledInSteamVR: Boolean = true) = TrackingChecklistStep(
	valid = connected,
	enabled = driverEnabled,
	ignorable = true,
	extraData = if (!connected) {
		TrackingChecklistSteamVRDisconnected(
			driverInstalled = installed,
			driverBlockedBySafeMode = blocked,
			driverEnabled = enabledInSteamVR,
		)
	} else {
		null
	},
)
private inline fun buildStandableStep(supported: Boolean = true, installed: Boolean = false) = TrackingChecklistStep(valid = !installed, enabled = supported, visibility = TrackingChecklistStepVisibility.WHEN_INVALID)

class SteamVRCheckBehaviour(private val server: VRServer, private val settings: Settings) : TrackingChecklistBehaviourType {
	private val client = HttpClient(CIO)
	private val steamVRProcName = when (CURRENT_PLATFORM) {
		Platform.WINDOWS -> "vrserver.exe"
		else -> "vrserver"
	}

	@JvmInline
	private value class SteamVRState(val running: Boolean, val drivers: List<Driver>?)

	override fun observe(receiver: TrackingChecklist) {
		settings.context.state.map { it.data.driverConfig.enabled }.flatMapLatest { enabled ->
			if (!enabled) {
				return@flatMapLatest flowOf(buildSteamVRDriverStep(driverEnabled = false) to buildStandableStep(supported = false))
			}

			val steamVRRunning = flow {
				while (true) {
					val running = getRunningProcesses().any { proc ->
						proc.name == steamVRProcName
					}
					emit(running)
					delay(3000)
				}
			}
			val steamVRState = steamVRRunning.distinctUntilChanged().map { running ->
				if (!running) {
					return@map SteamVRState(false, null)
				}

				delay(500)
				val drivers = try {
					getSteamVRDriversList(client)
				} catch (e: Exception) {
					AppLogger.checklist.warn(
						e,
						"Failed to get SteamVR drivers list",
					)
					null
				}

				return@map SteamVRState(true, drivers)
			}

			val protoDriverActiveFlow = server.context.state.map { state ->
				state.drivers.values.any { it.source == DriverBridgeSource.DRIVER }
			}
			val solarXRDriverActiveFlow = server.context.state.flatMapLatest { state ->
				combine(state.solarxr.values.map { it.context.state }) { states ->
					states.any { it.driverName != null } to states.any { it.driverName == "SteamVR" }
				}
			}

			combine(protoDriverActiveFlow, solarXRDriverActiveFlow, steamVRState) { protoDriverActive, (anySolarXRDriverActive, steamVRSolarXRDriverActive), steamVRState ->
				// if a non-SteamVR driver is connected, we shouldn't try to fetch the drivers list
				if (anySolarXRDriverActive && !steamVRSolarXRDriverActive) {
					return@combine buildSteamVRDriverStep(connected = true) to buildStandableStep(supported = false)
				}
				// for output state
				val anyDriverActive = protoDriverActive || anySolarXRDriverActive

				if (steamVRState.drivers == null) {
					return@combine buildSteamVRDriverStep(connected = anyDriverActive) to buildStandableStep()
				}

				val driver = steamVRState.drivers.firstOrNull {
					it.manifest.name == "slimevr"
				}
				val standableDriver = steamVRState.drivers.firstOrNull {
					it.manifest.name == "standable"
				}

				buildSteamVRDriverStep(connected = anyDriverActive, installed = !steamVRState.running || driver != null, blocked = driver?.blockedBySafeMode ?: false, enabledInSteamVR = driver?.enabled ?: true) to buildStandableStep(installed = standableDriver != null)
			}
		}
			.distinctUntilChanged()
			.onEach { (steamVRDriverStep, standableStep) ->
				receiver.context.dispatchAll(
					listOf(
						TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.STEAMVR_DISCONNECTED, steamVRDriverStep),
						TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.STANDABLE_INSTALLED, standableStep),
					),
				)
			}
			.launchIn(receiver.context.scope)
	}
}
