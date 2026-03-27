package dev.slimevr.serial

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import kotlinx.coroutines.CoroutineScope

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
typealias SerialConnectionBehaviour = Behaviour<SerialConnectionState, SerialConnectionActions, SerialConnection.Console>

sealed interface SerialConnection {
	class Console(
		val context: SerialConnectionContext,
		val handle: SerialPortHandle,
	) : SerialConnection {
		companion object {
			fun create(handle: SerialPortHandle, scope: CoroutineScope): Console {
				val behaviours = listOf(SerialLogBehaviour)
				val context = Context.create(
					initialState = SerialConnectionState(
						portLocation = handle.portLocation,
						descriptivePortName = handle.descriptivePortName,
						connected = true,
						logLines = listOf(),
					),
					scope = scope,
					behaviours = behaviours,
				)
				val conn = Console(context = context, handle = handle)
				behaviours.forEach { it.observe(conn) }
				return conn
			}
		}
	}

	data object Flashing : SerialConnection
}
