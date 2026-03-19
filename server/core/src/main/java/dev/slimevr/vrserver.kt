package dev.slimevr

import dev.slimevr.context.BasicModule
import dev.slimevr.context.Context
import dev.slimevr.context.createContext
import dev.slimevr.tracker.Device
import dev.slimevr.tracker.Tracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class VRServerState(
	val handleId: Int,
	val trackers: Map<Int, Tracker>,
	val devices: Map<Int, Device>,
)

sealed interface VRServerActions {
	data class NewTracker(val trackerId: Int, val context: Tracker) : VRServerActions
	data class NewDevice(val deviceId: Int, val context: Device) : VRServerActions
}

typealias VRServerContext = Context<VRServerState, VRServerActions>
typealias VRServerModule = BasicModule<VRServerState, VRServerActions>

val BaseModule = VRServerModule(
	reducer = { s, a ->
		when (a) {
			is VRServerActions.NewTracker -> s.copy(
				trackers = s.trackers + (a.trackerId to a.context),
				handleId = a.trackerId,
			)

			is VRServerActions.NewDevice -> s.copy(
				devices = s.devices + (a.deviceId to a.context),
				handleId = a.deviceId,
			)
		}
	},
	observer = { context ->
		context.state.distinctUntilChangedBy { state -> state.trackers.size }.onEach {
			println("tracker list size changed")
		}.launchIn(context.scope)
	},
)

data class VRServer(
	val context: VRServerContext,
) {
	fun nextHandle() = context.state.value.handleId + 1
	fun getTracker(id: Int) = context.state.value.trackers[id]
	fun getDevice(id: Int) = context.state.value.devices[id]

	companion object {
		fun create(scope: CoroutineScope): VRServer {
			val server = VRServerState(
				handleId = 0,
				trackers = mapOf(),
				devices = mapOf(),
			)

			val modules = listOf(BaseModule)

			val context = createContext(
				initialState = server,
				reducers = modules.map { it.reducer },
				scope = scope,
			)

			modules.map { it.observer }.forEach { it?.invoke(context) }

			return VRServer(
				context = context,
			)
		}
	}
}
