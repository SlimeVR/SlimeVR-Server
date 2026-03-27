package dev.slimevr.serial

internal const val MAX_LOG_LINES = 500

object SerialServerBaseBehaviour : SerialServerBehaviour {
	override fun reduce(state: SerialServerState, action: SerialServerActions) = when (action) {
		is SerialServerActions.PortDetected ->
			state.copy(availablePorts = state.availablePorts + (action.info.portLocation to action.info))

		is SerialServerActions.PortLost ->
			state.copy(availablePorts = state.availablePorts - action.portLocation)

		is SerialServerActions.RegisterConnection ->
			state.copy(connections = state.connections + (action.portLocation to action.connection))

		is SerialServerActions.RemoveConnection ->
			state.copy(connections = state.connections - action.portLocation)
	}
}

object SerialLogBehaviour : SerialConnectionBehaviour {
	override fun reduce(state: SerialConnectionState, action: SerialConnectionActions) = when (action) {
		is SerialConnectionActions.LogLine -> {
			val lines = if (state.logLines.size >= MAX_LOG_LINES) state.logLines.drop(1) else state.logLines
			state.copy(logLines = lines + action.line)
		}

		SerialConnectionActions.Disconnected -> state.copy(connected = false)
	}
}
