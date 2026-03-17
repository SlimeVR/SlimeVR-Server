package dev.slimevr

import dev.slimevr.context.Context
import dev.slimevr.context.BasicModule
import dev.slimevr.context.createContext
import dev.slimevr.tracker.TrackerContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class VRServerState(
	val trackers: Map<Int, TrackerContext>,
)

sealed interface VRServerActions {
	data class NewTracker(val trackerId: Int, val context: TrackerContext) : VRServerActions
}

typealias VRServerContext = Context<VRServerState, VRServerActions>
typealias VRServerModule = BasicModule<VRServerState, VRServerActions>

val TestModule = VRServerModule(
	reducer = { s, a ->
		when (a) {
			is VRServerActions.NewTracker -> s.copy(
				trackers = s.trackers + (a.trackerId to a.context),
			)
		}
	},
	observer = { context ->
		context.state.distinctUntilChangedBy { state -> state.trackers.size }.onEach {
			println("tracker list size changed")
		}.launchIn(context.scope)
	},
)

fun createVRServer(scope: CoroutineScope): VRServerContext {
	val server = VRServerState(
		trackers = mapOf(),
	)

	val modules = listOf(TestModule)

	val context = createContext(
		initialState = server,
		reducers = modules.map { it.reducer },
		scope = scope,
	)

	modules.map { it.observer }.forEach { it?.invoke(context) }

	return context
}
