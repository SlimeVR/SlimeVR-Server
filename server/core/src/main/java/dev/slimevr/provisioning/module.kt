package dev.slimevr.provisioning

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.serial.SerialConnection
import dev.slimevr.serial.SerialServer
import dev.slimevr.serial.isKnownSerialBoard
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeoutOrNull
import solarxr_protocol.rpc.SerialDeviceType
import solarxr_protocol.rpc.TrackerProvisioningStatus
import solarxr_protocol.rpc.WifiNetwork
import solarxr_protocol.rpc.WifiScanStatus

data class ScanState(
	val status: WifiScanStatus,
	val portLocation: String? = null,
	val networks: List<WifiNetwork> = emptyList(),
)

data class TrackerProvisioningState(
	val portLocation: String,
	val status: TrackerProvisioningStatus,
	val macAddress: String? = null,
)

data class ProvisioningManagerState(
	val scan: ScanState = ScanState(status = WifiScanStatus.NONE),
	val trackers: Map<String, TrackerProvisioningState> = emptyMap(), // keyed by portLocation
)

sealed interface ProvisioningActions {
	data class ScanPortSelected(val portLocation: String) : ProvisioningActions
	data class ScanStatusChanged(val status: WifiScanStatus) : ProvisioningActions
	data class ScanResults(val networks: List<WifiNetwork>) : ProvisioningActions
	data object ScanClear : ProvisioningActions

	data class TrackerStatusChanged(val portLocation: String, val status: TrackerProvisioningStatus) : ProvisioningActions
	data class TrackerMacAddressObtained(val portLocation: String, val mac: String) : ProvisioningActions
	data class TrackerRemoved(val portLocation: String) : ProvisioningActions
	data object TrackersClear : ProvisioningActions
}

typealias ProvisioningManagerContext = Context<ProvisioningManagerState, ProvisioningActions>
typealias ProvisioningManagerBehaviour = Behaviour<ProvisioningManagerState, ProvisioningActions, ProvisioningManager>

data class ProvisioningManager(
	val context: ProvisioningManagerContext,
	private val serialServer: SerialServer,
	private val settings: Settings,
	private val scope: CoroutineScope,
) {
	fun startObserving() = context.observeAll(this)

	// Jobs cannot be held into a state / mutable flow
	// as we cannot guarantee immutability
	private var scanJob: Job? = null
	private var provisioningWatcherJob: Job? = null

	suspend fun stopProvisioning() {
		provisioningWatcherJob?.cancelAndJoin()
		provisioningWatcherJob = null
		context.dispatch(ProvisioningActions.TrackersClear)
	}

	suspend fun stopWifiScan() {
		scanJob?.cancelAndJoin()
		scanJob = null
		context.dispatch(ProvisioningActions.ScanClear)
	}

	suspend fun startProvisioning(server: VRServer, ssid: String, password: String?) {
		provisioningWatcherJob?.cancelAndJoin()
		context.dispatch(ProvisioningActions.TrackersClear)

		provisioningWatcherJob = scope.launch {
			supervisorScope {
				val trackerJobs = mutableMapOf<String, Job>()
				val startedPorts = mutableSetOf<String>()

				serialServer.context.state.map { it.availablePorts }.collect { ports ->
					ports.entries
						.filter { (portLocation, info) ->
							portLocation !in startedPorts &&
								isKnownSerialBoard(info.vendorId, info.productId) &&
								info.toSerialDevice().type == SerialDeviceType.ESP_TRACKER
						}
						.forEach { (portLocation, _) ->
							startedPorts += portLocation
							trackerJobs[portLocation] = launch {
								try {
									runProvisioningForPort(context, server, settings, serialServer, portLocation, ssid, password)
								} catch (e: CancellationException) {
									throw e
								} catch (e: Exception) {
									context.dispatch(
										ProvisioningActions.TrackerStatusChanged(portLocation, TrackerProvisioningStatus.CONNECTION_ERROR),
									)
								} finally {
									trackerJobs.remove(portLocation)
								}
							}
						}

					(startedPorts - ports.keys).forEach { gone ->
						trackerJobs.remove(gone)?.cancel()
						startedPorts -= gone
						context.dispatch(ProvisioningActions.TrackerRemoved(gone))
					}
				}
			}
		}
	}

	suspend fun startWifiScan() {
		val currentJob = scanJob
		if (currentJob != null) {
			currentJob.cancelAndJoin()
			context.dispatch(ProvisioningActions.ScanClear)
		}
		scanJob = scope.launch {
			while (isActive) {
				if (!selectAndOpenPort(context, serialServer)) continue
				val portLocation = context.state.value.scan.portLocation ?: return@launch

				val serialConn = withTimeoutOrNull(3_000) {
					serialServer.context.state
						.mapNotNull { it.connections[portLocation] as? SerialConnection.Console }
						.first()
				}

				if (serialConn == null) {
					context.dispatch(ProvisioningActions.ScanStatusChanged(WifiScanStatus.NO_SERIAL_DEVICE_FOUND))
					return@launch
				}

				coroutineScope {
					val work = async {
						scanWifiNetworks(context, serialConn)
						awaitCancellation()
					}
					val disconnect = async {
						serialServer.context.state.map { it.availablePorts }.first { portLocation !in it }
					}
					select {
						work.onAwait {}
						disconnect.onAwait {}
					}
					work.cancel()
					disconnect.cancel()
				}

				context.dispatchAll(
					listOf(
						ProvisioningActions.ScanClear,
						ProvisioningActions.TrackerRemoved(portLocation),
					),
				)
			}
		}
	}

	companion object {
		val INITIAL_STATE = ProvisioningManagerState()

		fun create(ctx: Phase1ContextProvider, scope: CoroutineScope): ProvisioningManager {
			val behaviours = listOf(ProvisioningManagerBaseBehaviour())
			val context = Context.create(
				initialState = INITIAL_STATE,
				scope = scope,
				behaviours = behaviours,
				name = "ProvisioningManager",
			)
			return ProvisioningManager(
				context = context,
				serialServer = ctx.serialServer,
				settings = ctx.config.settings,
				scope = scope,
			)
		}
	}
}
