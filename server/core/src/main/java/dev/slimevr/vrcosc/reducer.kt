package dev.slimevr.vrcosc

fun reduce(state: VRCOSCState, action: VRCOSCActions): VRCOSCState = when (action) {
	is VRCOSCActions.SetInput -> state.copy(
		status = state.status.copy(
			inputState = action.state,
			inputPort = action.port,
			inputError = action.error,
		),
	)

	is VRCOSCActions.SetLastReceivedInput -> state.copy(
		status = state.status.copy(lastReceivedInputMillis = action.millis),
	)

	is VRCOSCActions.SetOscQuery -> state.copy(
		status = state.status.copy(
			oscQueryState = action.state,
			oscQueryAdvertisedPort = action.advertisedPort,
			oscQueryError = action.error,
		),
	)

	is VRCOSCActions.SetDiscoveredTargets -> state.copy(
		status = state.status.copy(discoveredTargets = action.targets),
	)

	is VRCOSCActions.SetOutput -> state.copy(
		status = state.status.copy(
			outputState = action.state,
			targetAddress = action.targetAddress,
			targetPort = action.targetPort,
			targetSource = action.targetSource,
			outputError = action.error,
		),
	)

	is VRCOSCActions.SetLastFrameSent -> state.copy(
		status = state.status.copy(lastFrameSentMillis = action.millis),
	)
}
