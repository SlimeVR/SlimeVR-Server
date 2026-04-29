package dev.slimevr.provisioning

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.serial.SerialConnection
import dev.slimevr.serial.SerialServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import solarxr_protocol.rpc.WifiProvisioningStatus

data class ProvisioningManagerState(
	val status: WifiProvisioningStatus,
	val portLocation: String?,
	val macAddress: String?,
)

sealed interface ProvisioningActions {
	data class PortSelected(val portLocation: String) : ProvisioningActions
	data class StatusChanged(val status: WifiProvisioningStatus) : ProvisioningActions
	data class MacAddressObtained(val mac: String) : ProvisioningActions
	data object Clear : ProvisioningActions
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
	private var provisioningJob: Job? = null

	suspend fun stopProvisioning() {
		provisioningJob?.cancelAndJoin()
		context.dispatch(ProvisioningActions.Clear)
	}

	suspend fun startProvisioning(
		server: VRServer,
		ssid: String,
		password: String?,
		port: String?,
	) {
		val currentJob = provisioningJob
		if (currentJob != null) {
			currentJob.cancelAndJoin()
			context.dispatch(ProvisioningActions.Clear)
		}
		provisioningJob = scope.launch {
			// TODO handle port field, currently ignored
			while (isActive) {
				if (!selectAndOpenPort(context, serialServer)) continue
				val portLocation = context.state.value.portLocation ?: continue

				val serialConn =
					serialServer.context.state.value.connections[portLocation] as? SerialConnection.Console

				if (serialConn == null) {
					context.dispatch(ProvisioningActions.StatusChanged(WifiProvisioningStatus.NO_SERIAL_DEVICE_FOUND))
				} else {
					provisionPort(context, server, settings, serialConn, ssid, password)
				}

				// Any outcome (success or failure) waits for the port to disconnect
				// before looking for the next tracker
				serialServer.context.state.filter { portLocation !in it.availablePorts }.first()
				context.dispatch(ProvisioningActions.Clear)
			}
		}
	}

	companion object {
		val INITIAL_STATE = ProvisioningManagerState(
			status = WifiProvisioningStatus.NONE,
			portLocation = null,
			macAddress = null,
		)

		fun create(ctx: Phase1ContextProvider, scope: CoroutineScope): ProvisioningManager {
			val behaviours = listOf(ProvisioningManagerBaseBehaviour)
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
