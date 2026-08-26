package dev.slimevr.serial

fun reduce(state: SerialServerState, action: SerialServerActions): SerialServerState = when (action) {
	is SerialServerActions.PortDetected ->
		state.copy(availablePorts = state.availablePorts + (action.info.portLocation to action.info))

	is SerialServerActions.PortLost ->
		state.copy(availablePorts = state.availablePorts - action.portLocation)

	is SerialServerActions.RegisterConnection ->
		state.copy(connections = state.connections + (action.portLocation to action.connection))

	is SerialServerActions.RemoveConnection ->
		state.copy(connections = state.connections - action.portLocation)
}
