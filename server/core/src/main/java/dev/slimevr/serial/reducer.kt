package dev.slimevr.serial

fun reduce(state: SerialServerState, action: SerialServerActions): SerialServerState = when (action) {
	is SerialServerActions.PortsChanged -> state.copy(ports = action.ports)
	is SerialServerActions.FlashingStarted -> state.copy(flashing = state.flashing + action.portLocation)
	is SerialServerActions.FlashingEnded -> state.copy(flashing = state.flashing - action.portLocation)
}
