package dev.slimevr.serial

import dev.slimevr.context.Context
import dev.slimevr.context.CustomBehaviour
import dev.slimevr.context.createContext
import kotlinx.coroutines.CoroutineScope

private const val MAX_LOG_LINES = 500

data class SerialPortHandle(
	val portLocation: String,
	val descriptivePortName: String,
	val writeCommand: (String) -> Unit,
	val close: () -> Unit,
)

data class SerialConnectionState(
	val portLocation: String,
	val descriptivePortName: String,
	val connected: Boolean,
	val logLines: List<String>,
)

sealed interface SerialConnectionActions {
	data class LogLine(val line: String) : SerialConnectionActions
	data object Disconnected : SerialConnectionActions
}

typealias SerialConnectionContext = Context<SerialConnectionState, SerialConnectionActions>
typealias SerialConnectionBehaviour =
	CustomBehaviour<SerialConnectionState, SerialConnectionActions, SerialConnection.Console>

sealed interface SerialConnection {
	data class Console(
		val context: SerialConnectionContext,
		val handle: SerialPortHandle,
	) : SerialConnection

	data object Flashing : SerialConnection
}

val SerialLogBehaviour = SerialConnectionBehaviour(
	reducer = { s, a ->
		when (a) {
			is SerialConnectionActions.LogLine -> {
				val lines = if (s.logLines.size >= MAX_LOG_LINES) s.logLines.drop(1) else s.logLines
				s.copy(logLines = lines + a.line)
			}

			SerialConnectionActions.Disconnected -> s.copy(connected = false)
		}
	},
	observer = null,
)

fun createSerialConnection(
	handle: SerialPortHandle,
	scope: CoroutineScope,
): SerialConnection.Console {
	val behaviours = listOf(SerialLogBehaviour)

	val context = createContext(
		initialState = SerialConnectionState(
			portLocation = handle.portLocation,
			descriptivePortName = handle.descriptivePortName,
			connected = true,
			logLines = listOf(),
		),
		reducers = behaviours.map { it.reducer },
		scope = scope,
	)

	val conn = SerialConnection.Console(context = context, handle = handle)
	behaviours.map { it.observer }.forEach { it?.invoke(conn) }
	return conn
}
