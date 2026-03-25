package dev.slimevr.serial

import dev.llelievr.espflashkotlin.FlasherSerialInterface
import dev.slimevr.context.BasicBehaviour
import dev.slimevr.context.Context
import dev.slimevr.context.createContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import solarxr_protocol.rpc.SerialDevice

typealias FlashingHandler = FlasherSerialInterface

data class SerialPortInfo(
	val portLocation: String,
	val descriptivePortName: String,
	val vendorId: Int,
	val productId: Int,
) {
	fun toSerialDevice() = SerialDevice(
		port = portLocation,
		name = descriptivePortName,
	)
}

data class SerialServerState(
	val availablePorts: Map<String, SerialPortInfo>,
	val connections: Map<String, SerialConnection>,
)

sealed interface SerialServerActions {
	data class PortDetected(val info: SerialPortInfo) : SerialServerActions
	data class PortLost(val portLocation: String) : SerialServerActions
	data class RegisterConnection(val portLocation: String, val connection: SerialConnection) : SerialServerActions
	data class RemoveConnection(val portLocation: String) : SerialServerActions
}

typealias SerialServerContext = Context<SerialServerState, SerialServerActions>
typealias SerialServerBehaviour = BasicBehaviour<SerialServerState, SerialServerActions>

val SerialServerBaseBehaviour = SerialServerBehaviour(
	reducer = { s, a ->
		when (a) {
			is SerialServerActions.PortDetected ->
				s.copy(availablePorts = s.availablePorts + (a.info.portLocation to a.info))

			is SerialServerActions.PortLost ->
				s.copy(availablePorts = s.availablePorts - a.portLocation)

			is SerialServerActions.RegisterConnection ->
				s.copy(connections = s.connections + (a.portLocation to a.connection))

			is SerialServerActions.RemoveConnection ->
				s.copy(connections = s.connections - a.portLocation)
		}
	},
	observer = null,
)

class SerialServer(
	val context: SerialServerContext,
	private val scope: CoroutineScope,
	private val openPortFactory: (
		portLocation: String,
		scope: CoroutineScope,
		onDataReceived: suspend (portLocation: String, line: String) -> Unit,
		onPortDisconnected: suspend (portLocation: String) -> Unit,
	) -> SerialPortHandle?,
	private val openFlashingPortFactory: () -> FlashingHandler,
) {
	suspend fun onPortDetected(info: SerialPortInfo) {
		context.dispatch(SerialServerActions.PortDetected(info))
	}

	suspend fun onPortLost(portLocation: String) {
		val conn = context.state.value.connections[portLocation]
		if (conn is SerialConnection.Console) {
			conn.handle.close()
		}
		context.dispatch(SerialServerActions.RemoveConnection(portLocation))
		context.dispatch(SerialServerActions.PortLost(portLocation))
	}

	suspend fun onDataReceived(portLocation: String, line: String) {
		val conn = context.state.value.connections[portLocation]
		if (conn is SerialConnection.Console) conn.context.dispatch(SerialConnectionActions.LogLine(line))
	}

	suspend fun onPortDisconnected(portLocation: String) {
		val conn = context.state.value.connections[portLocation]
		if (conn !is SerialConnection.Console) return
		conn.context.dispatch(SerialConnectionActions.Disconnected)
		conn.handle.close()
		context.dispatch(SerialServerActions.RemoveConnection(portLocation))
	}

	suspend fun openConnection(portLocation: String) {
		val state = context.state.value
		if (!state.availablePorts.containsKey(portLocation) || state.connections.containsKey(portLocation)) return
		val handle = openPortFactory(portLocation, scope, ::onDataReceived, ::onPortDisconnected) ?: return
		context.dispatch(SerialServerActions.RegisterConnection(portLocation, createSerialConnection(handle, scope)))
	}

	suspend fun closeConnection(portLocation: String) {
		val conn = context.state.value.connections[portLocation]
		if (conn !is SerialConnection.Console) return
		conn.context.dispatch(SerialConnectionActions.Disconnected)
		conn.handle.close()
		context.dispatch(SerialServerActions.RemoveConnection(portLocation))
	}

	suspend fun openForFlashing(portLocation: String): FlashingHandler? {
		val state = context.state.value
		if (!state.availablePorts.containsKey(portLocation) || state.connections.containsKey(portLocation)) return null
		closeConnection(portLocation)
		val handler = openFlashingPortFactory()
		context.dispatch(SerialServerActions.RegisterConnection(portLocation, SerialConnection.Flashing))
		return object : FlashingHandler by handler {
			override fun closeSerial() {
				handler.closeSerial()
				scope.launch { context.dispatch(SerialServerActions.RemoveConnection(portLocation)) }
			}
		}
	}

	companion object {
		fun create(
			openPort: (
				portLocation: String,
				scope: CoroutineScope,
				onDataReceived: suspend (portLocation: String, line: String) -> Unit,
				onPortDisconnected: suspend (portLocation: String) -> Unit,
			) -> SerialPortHandle?,
			openFlashingPort: () -> FlashingHandler,
			scope: CoroutineScope,
		): SerialServer {
			val behaviours = listOf(SerialServerBaseBehaviour)
			val context = createContext(
				initialState = SerialServerState(availablePorts = mapOf(), connections = mapOf()),
				reducers = behaviours.map { it.reducer },
				scope = scope,
			)
			val server = SerialServer(
				context = context,
				scope = scope,
				openPortFactory = openPort,
				openFlashingPortFactory = openFlashingPort,
			)
			behaviours.map { it.observer }.forEach { it?.invoke(context) }
			return server
		}
	}
}
