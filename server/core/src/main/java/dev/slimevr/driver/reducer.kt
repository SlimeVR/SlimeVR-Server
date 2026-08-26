package dev.slimevr.driver

fun reduce(
	state: DriverBridgeState,
	action: DriverBridgeActions,
): DriverBridgeState = when (action) {
	is DriverBridgeActions.AddTracker -> state.copy(trackers = state.trackers + (action.id to action.trackerId))
	is DriverBridgeActions.UpdateProtocolVersion -> state.copy(protocolVersion = action.version)
}
