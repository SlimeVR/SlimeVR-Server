package dev.slimevr

import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

object BaseBehaviour : VRServerBehaviour {
	override fun reduce(state: VRServerState, action: VRServerActions): VRServerState = when (action) {
		is VRServerActions.NewTracker -> state.copy(trackers = state.trackers + (action.trackerId to action.context))
		is VRServerActions.NewDevice -> state.copy(devices = state.devices + (action.deviceId to action.context))
		is VRServerActions.DriverConnected -> state.copy(drivers = state.drivers + (action.bridge.id to action.bridge))
		is VRServerActions.DriverDisconnected -> state.copy(drivers = state.drivers - action.bridgeId)
		is VRServerActions.FeederConnected -> state.copy(feeders = state.feeders + (action.bridge.id to action.bridge))
		is VRServerActions.FeederDisconnected -> state.copy(feeders = state.feeders - action.bridgeId)
		is VRServerActions.SolarXRConnected -> state.copy(solarxr = state.solarxr + (action.connection.id to action.connection))
		is VRServerActions.SolarXRDisconnected -> state.copy(solarxr = state.solarxr - action.connectionId)
	}

	override fun observe(receiver: VRServer) {
		receiver.context.state.distinctUntilChangedBy { it.trackers.size }.onEach {
			println("tracker list size changed")
		}.launchIn(receiver.context.scope)
	}
}
