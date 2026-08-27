package dev.slimevr.vmc

fun reduce(state: VMCState, action: VMCActions): VMCState = when (action) {
	is VMCActions.SetInput -> state.copy(
		status = state.status.copy(
			inputState = action.state,
			inputPort = action.port,
			inputError = action.error,
		),
	)

	is VMCActions.SetLastReceivedInput -> state.copy(
		status = state.status.copy(lastReceivedInputMillis = action.millis),
	)

	is VMCActions.SetOutput -> state.copy(
		status = state.status.copy(
			outputState = action.state,
			targetAddress = action.targetAddress,
			targetPort = action.targetPort,
			outputError = action.error,
		),
	)

	is VMCActions.SetLastFrameSent -> state.copy(
		status = state.status.copy(lastFrameSentMillis = action.millis),
	)

	is VMCActions.SetVrm -> state.copy(
		status = state.status.copy(vrmState = action.state, vrmError = action.error),
	)
}
