package dev.slimevr

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.device.Device
import dev.slimevr.tracker.Tracker
import kotlinx.coroutines.CoroutineScope
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
typealias VRServerBehaviour = Behaviour<VRServerState, VRServerActions, VRServer>

@OptIn(ExperimentalAtomicApi::class)
class VRServer(
	val context: VRServerContext,
) {
	private val handleCounter: AtomicInt = AtomicInt(0)

	fun nextHandle() = handleCounter.incrementAndFetch()
	fun getTracker(id: Int) = context.state.value.trackers[id]
	fun getDevice(id: Int) = context.state.value.devices[id]

	companion object {
		fun create(scope: CoroutineScope): VRServer {
			val behaviours = listOf(BaseBehaviour)
			val context = Context.create(
				initialState = VRServerState(trackers = mapOf(), devices = mapOf()),
				scope = scope,
				behaviours = behaviours,
			)
			val server = VRServer(context = context)
			behaviours.forEach { it.observe(server) }
			return server
		}
	}
}
