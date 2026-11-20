package dev.slimevr.desktop.trackingchecklist

import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.desktop.Driver
import dev.slimevr.desktop.getSteamVRDriversList
import dev.slimevr.logging.AppLogger
import dev.slimevr.trackingchecklist.TrackingChecklist
import dev.slimevr.trackingchecklist.TrackingChecklistActions
import dev.slimevr.trackingchecklist.TrackingChecklistBehaviourType
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.transformLatest
import solarxr_protocol.rpc.TrackingChecklistSteamVRDisconnected
import solarxr_protocol.rpc.TrackingChecklistStep
import solarxr_protocol.rpc.TrackingChecklistStepId
import solarxr_protocol.rpc.TrackingChecklistStepVisibility
import java.io.File
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds

private val PROCESS_POLL_INTERVAL = 3.seconds

// SteamVR's web server comes up some time after vrserver starts
private val STEAMVR_WEB_SERVER_STARTUP = 1.seconds

// Retry delay doubles from 1s up to 2^step seconds (16s)
private const val DRIVERS_RETRY_MAX_BACKOFF_STEP = 4L

private fun isProcessRunning(name: String): Boolean = ProcessHandle.allProcesses().anyMatch { proc ->
	proc.info().command().orElse(null)?.substringAfterLast(File.separatorChar) == name
}

// No OS event for process start/exit, so this polls
private fun processRunningFlow(name: String): Flow<Boolean> = flow {
	while (true) {
		emit(isProcessRunning(name))
		delay(PROCESS_POLL_INTERVAL)
	}
}.flowOn(Dispatchers.IO).distinctUntilChanged()

@OptIn(ExperimentalCoroutinesApi::class)
private fun steamVRReadyFlow(procName: String): Flow<Boolean> = processRunningFlow(procName).transformLatest { running ->
	if (running) delay(STEAMVR_WEB_SERVER_STARTUP)
	emit(running)
}

private fun steamVRDriversFlow(client: HttpClient): Flow<List<Driver>> = flow {
	emit(getSteamVRDriversList(client))
}.retryWhen { cause, attempt ->
	if (cause is CancellationException) return@retryWhen false
	AppLogger.checklist.warn(cause, "Failed to get SteamVR drivers list, retrying")
	delay(1.seconds * (1 shl attempt.coerceAtMost(DRIVERS_RETRY_MAX_BACKOFF_STEP).toInt()))
	true
}

private fun buildSteamVRDriverStep(driverEnabled: Boolean = true, connected: Boolean = false, installed: Boolean = true, blocked: Boolean = false, enabledInSteamVR: Boolean = true) = TrackingChecklistStep(
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
private fun buildStandableStep(supported: Boolean = true, installed: Boolean = false) = TrackingChecklistStep(
	valid = !installed,
	enabled = supported,
	ignorable = true,
	visibility = TrackingChecklistStepVisibility.WHEN_INVALID,
)

private fun buildSteamVRSteps(driverConnected: Boolean, drivers: List<Driver>?): Pair<TrackingChecklistStep, TrackingChecklistStep> {
	if (drivers == null) {
		return buildSteamVRDriverStep(connected = driverConnected) to buildStandableStep()
	}

	val driver = drivers.firstOrNull { it.manifest.name == "slimevr" }
	val standableDriver = drivers.firstOrNull { it.manifest.name == "standable" }

	return buildSteamVRDriverStep(
		connected = driverConnected,
		installed = driver != null,
		blocked = driver?.blockedBySafeMode ?: false,
		enabledInSteamVR = driver?.enabled ?: true,
	) to buildStandableStep(installed = standableDriver != null)
}

class SteamVRCheckBehaviour(private val server: VRServer, private val settings: Settings) : TrackingChecklistBehaviourType {
	private val steamVRProcName = when (CURRENT_PLATFORM) {
		Platform.WINDOWS -> "vrserver.exe"
		else -> "vrserver"
	}

	private data class DriverActivity(val anyDriver: Boolean, val steamVRDriver: Boolean)

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: TrackingChecklist) {
		val client = HttpClient(CIO)

		val driverActivity = server.context.state.map { it.solarxr }.distinctUntilChanged().flatMapLatest { solarXRBridges ->
			if (solarXRBridges.isEmpty()) {
				return@flatMapLatest flowOf(DriverActivity(anyDriver = false, steamVRDriver = false))
			}
			combine(solarXRBridges.values.map { it.context.state }) { states ->
				DriverActivity(
					anyDriver = states.any { it.driverName != null },
					steamVRDriver = states.any { it.driverName == "SteamVR" },
				)
			}
		}.distinctUntilChanged()

		settings.context.state.map { it.data.driverConfig.enabled }.distinctUntilChanged().flatMapLatest { enabled ->
			if (!enabled) {
				return@flatMapLatest flowOf(buildSteamVRDriverStep(driverEnabled = false) to buildStandableStep(supported = false))
			}

			combine(driverActivity, steamVRReadyFlow(steamVRProcName), ::Pair).transformLatest { (activity, steamVRReady) ->
				// if a non-SteamVR driver is connected, we shouldn't try to fetch the drivers list
				if (activity.anyDriver && !activity.steamVRDriver) {
					emit(buildSteamVRDriverStep(connected = true) to buildStandableStep(supported = false))
					return@transformLatest
				}

				emit(buildSteamVRSteps(activity.anyDriver, drivers = null))
				if (steamVRReady) {
					emitAll(steamVRDriversFlow(client).map { buildSteamVRSteps(activity.anyDriver, it) })
				}
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
			.onCompletion { client.close() }
			.launchIn(receiver.context.scope)
	}
}
