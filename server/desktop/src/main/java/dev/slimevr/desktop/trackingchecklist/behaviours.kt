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
import dev.slimevr.desktop.getSteamVRDriversList
import dev.slimevr.logging.AppLogger
import dev.slimevr.timeSource
import dev.slimevr.trackingchecklist.TrackingChecklist
import dev.slimevr.trackingchecklist.TrackingChecklistActions
import dev.slimevr.trackingchecklist.TrackingChecklistBehaviourType
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
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

data class SteamVRDriverState(val connected: Boolean = false, val installed: Boolean = true, val blocked: Boolean = false, val enabled: Boolean = true)

class SteamVRCheckBehaviour(private val server: VRServer) : TrackingChecklistBehaviourType {
	private val steamVRProcName = when (CURRENT_PLATFORM) {
		Platform.WINDOWS -> "vrserver.exe"
		else -> "vrserver"
	}
	private val driverState = MutableStateFlow(SteamVRDriverState())
	private val standableState = MutableStateFlow(false)

	override fun observe(receiver: TrackingChecklist) {
		receiver.context.scope.launch {
			HttpClient(CIO).use { client ->
				var scannedRunning = false
				var lastScan: TimeMark? = null

				while (isActive) {
					val connected = server.context.state.value.drivers.isNotEmpty()

					val drivers = try {
						getSteamVRDriversList(client)
					} catch (e: Exception) {
						if (!e.isConnectionRefused()) {
							AppLogger.checklist.warn(e, "Failed to get SteamVR drivers list")
						}
						null
					}

					val scan = lastScan
					if (drivers != null) {
						scannedRunning = false
						lastScan = null
					} else if (scan == null || scan.elapsedNow() >= PROCESS_SCAN_INTERVAL) {
						scannedRunning = getRunningProcesses().any { proc ->
							proc.name == steamVRProcName
						}
						lastScan = timeSource.markNow()
					}

					val running = connected || drivers != null || scannedRunning

					val driver = drivers?.firstOrNull {
						it.manifest.name == "slimevr"
					}
					val standableDriver = drivers?.firstOrNull {
						it.manifest.name == "standable"
					}

					driverState.update {
						SteamVRDriverState(connected = connected, installed = !running || driver != null, driver?.blockedBySafeMode ?: false, enabled = driver?.enabled ?: true)
					}
					standableState.update {
						standableDriver != null
					}
					delay(3000)
				}
			}
		}

		driverState
			.map { state ->
				TrackingChecklistStep(
					valid = state.connected,
					enabled = true,
					ignorable = true,
					extraData = if (!state.connected) {
						TrackingChecklistSteamVRDisconnected(
							driverInstalled = state.installed,
							driverBlockedBySafeMode = state.blocked,
							driverEnabled = state.enabled,
						)
					} else {
						null
					},
				)
			}
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.STEAMVR_DISCONNECTED, step)) }
			.launchIn(receiver.context.scope)
		standableState
			.map { installed ->
				TrackingChecklistStep(valid = !installed, enabled = true, visibility = TrackingChecklistStepVisibility.WHEN_INVALID)
			}
			.distinctUntilChanged()
			.onEach { step -> receiver.context.dispatch(TrackingChecklistActions.UpdateStep(TrackingChecklistStepId.STANDABLE_INSTALLED, step)) }
			.launchIn(receiver.context.scope)
	}
}
