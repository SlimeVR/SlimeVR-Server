package dev.slimevr

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class BaseBehaviour : VRServerBehaviour {
	override fun reduce(state: VRServerState, action: VRServerActions): VRServerState = when (action) {
		is VRServerActions.NewTracker -> state.copy(trackers = state.trackers + (action.trackerId to action.context))
		is VRServerActions.NewDevice -> state.copy(devices = state.devices + (action.deviceId to action.context))
		is VRServerActions.DriverConnected -> state.copy(drivers = state.drivers + (action.bridge.id to action.bridge))
		is VRServerActions.DriverDisconnected -> state.copy(drivers = state.drivers - action.bridgeId)
		is VRServerActions.SolarXRConnected -> state.copy(solarxr = state.solarxr + (action.connection.id to action.connection))
		is VRServerActions.SolarXRDisconnected -> state.copy(solarxr = state.solarxr - action.connectionId)
		is VRServerActions.NewDongle -> state.copy(dongles = state.dongles + (action.dongleId to action.context))
		is VRServerActions.RemoveDongle -> state.copy(dongles = state.dongles - action.dongleId)
	}

	override fun observe(receiver: VRServer) {
		receiver.context.state.map { it.trackers.size }.distinctUntilChanged().onEach {
			println("Tracker list size changed to $it")
		}.launchIn(receiver.context.scope)
	}
}
