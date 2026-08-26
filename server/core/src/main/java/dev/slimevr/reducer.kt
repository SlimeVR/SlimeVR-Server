package dev.slimevr

fun reduce(state: VRServerState, action: VRServerActions): VRServerState = when (action) {
	is VRServerActions.NewTracker -> state.copy(trackers = state.trackers + (action.trackerId to action.context))
	is VRServerActions.NewDevice -> state.copy(devices = state.devices + (action.deviceId to action.context))
	is VRServerActions.DriverConnected -> state.copy(drivers = state.drivers + (action.bridge.id to action.bridge))
	is VRServerActions.DriverDisconnected -> state.copy(drivers = state.drivers - action.bridgeId)
	is VRServerActions.SolarXRConnected -> state.copy(solarxr = state.solarxr + (action.connection.id to action.connection))
	is VRServerActions.SolarXRDisconnected -> state.copy(solarxr = state.solarxr - action.connectionId)
	is VRServerActions.NewDongle -> state.copy(dongles = state.dongles + (action.dongleId to action.context))
}
