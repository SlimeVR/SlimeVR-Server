package dev.slimevr

import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

object BaseBehaviour : VRServerBehaviour {
	override fun reduce(state: VRServerState, action: VRServerActions) = when (action) {
		is VRServerActions.NewTracker -> state.copy(trackers = state.trackers + (action.trackerId to action.context))
		is VRServerActions.NewDevice -> state.copy(devices = state.devices + (action.deviceId to action.context))
	}

	override fun observe(receiver: VRServer) {
		receiver.context.state.distinctUntilChangedBy { it.trackers.size }.onEach {
			println("tracker list size changed")
		}.launchIn(receiver.context.scope)
	}
}
