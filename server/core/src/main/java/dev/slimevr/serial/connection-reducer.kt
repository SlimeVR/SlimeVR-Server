package dev.slimevr.serial

internal const val MAX_LOG_LINES = 500

fun reduce(state: SerialConnectionState, action: SerialConnectionActions): SerialConnectionState = when (action) {
	is SerialConnectionActions.LogLine -> {
		val lines = if (state.logLines.size >= MAX_LOG_LINES) state.logLines.drop(1) else state.logLines
		state.copy(logLines = lines + action.line)
	}

	is SerialConnectionActions.ClearLogs -> state.copy(logLines = listOf())

	is SerialConnectionActions.Disconnected -> state.copy(connected = false)
}
