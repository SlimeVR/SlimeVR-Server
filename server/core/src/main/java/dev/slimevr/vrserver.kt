package dev.slimevr

import dev.slimevr.context.Context
import dev.slimevr.context.CustomBehaviour
import dev.slimevr.context.createContext
import dev.slimevr.device.Device
import dev.slimevr.firmware.FirmwareManager
import dev.slimevr.serial.SerialServer
import dev.slimevr.tracker.Tracker
import dev.slimevr.vrchat.VRCConfigManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

data class VRServerState(
	val trackers: Map<Int, Tracker>,
	val devices: Map<Int, Device>,
)

sealed interface VRServerActions {
	data class NewTracker(val trackerId: Int, val context: Tracker) : VRServerActions
	data class NewDevice(val deviceId: Int, val context: Device) : VRServerActions
}

typealias VRServerContext = Context<VRServerState, VRServerActions>
typealias VRServerBehaviour = CustomBehaviour<VRServerState, VRServerActions, VRServer>

val BaseBehaviour = VRServerBehaviour(
	reducer = { s, a ->
		when (a) {
			is VRServerActions.NewTracker -> s.copy(trackers = s.trackers + (a.trackerId to a.context))
			is VRServerActions.NewDevice -> s.copy(devices = s.devices + (a.deviceId to a.context))
		}
	},
	observer = { context ->
		context.context.state.distinctUntilChangedBy { state -> state.trackers.size }.onEach {
			println("tracker list size changed")
		}.launchIn(context.context.scope)

		context.serialServer.context.state.distinctUntilChangedBy { state -> state.availablePorts.size }.onEach {
			println("Avalable ports $it")
		}.launchIn(context.context.scope)
	},
)

@OptIn(ExperimentalAtomicApi::class)
data class VRServer(
	val context: VRServerContext,
	val serialServer: SerialServer,
	val firmwareManager: FirmwareManager,
	val vrcConfigManager: VRCConfigManager,

	// Moved this outside of the context to make this faster and safer to use
	private val handleCounter: AtomicInt,
) {
	fun nextHandle() = handleCounter.incrementAndFetch()
	fun getTracker(id: Int) = context.state.value.trackers[id]
	fun getDevice(id: Int) = context.state.value.devices[id]

	companion object {
		fun create(
			scope: CoroutineScope,
			serialServer: SerialServer,
			firmwareManager: FirmwareManager,
			vrcConfigManager: VRCConfigManager,
		): VRServer {
			val state = VRServerState(
				trackers = mapOf(),
				devices = mapOf(),
			)

			val behaviours = listOf(BaseBehaviour)

			val context = createContext(
				initialState = state,
				reducers = behaviours.map { it.reducer },
				scope = scope,
			)

			val server = VRServer(
				context = context,
				serialServer = serialServer,
				firmwareManager = firmwareManager,
				vrcConfigManager = vrcConfigManager,
				handleCounter = AtomicInt(0),
			)

			behaviours.map { it.observer }.forEach { it?.invoke(server) }

			return server
		}
	}
}