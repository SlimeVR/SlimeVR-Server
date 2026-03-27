package dev.slimevr.serial

import dev.llelievr.espflashkotlin.FlasherSerialInterface
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
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
typealias SerialServerBehaviour = Behaviour<SerialServerState, SerialServerActions, SerialServerContext>

class SerialServer(
	val context: SerialServerContext,
	private val scope: CoroutineScope,
	private val openPortFactory: (
		portLocation: String,
		onDataReceived: (portLocation: String, line: String) -> Unit,
		onPortDisconnected: (portLocation: String) -> Unit,
	) -> SerialPortHandle?,
	private val openFlashingPortFactory: () -> FlashingHandler,
) {
	fun onPortDetected(info: SerialPortInfo) {
		context.dispatch(SerialServerActions.PortDetected(info))
	}

	fun onPortLost(portLocation: String) {
		val conn = context.state.value.connections[portLocation]
		if (conn is SerialConnection.Console) {
			conn.handle.close()
		}
		context.dispatch(SerialServerActions.RemoveConnection(portLocation))
		context.dispatch(SerialServerActions.PortLost(portLocation))
	}

	fun onDataReceived(portLocation: String, line: String) {
		val conn = context.state.value.connections[portLocation]
		if (conn is SerialConnection.Console) conn.context.dispatch(SerialConnectionActions.LogLine(line))
	}

	fun onPortDisconnected(portLocation: String) {
		val conn = context.state.value.connections[portLocation]
		if (conn !is SerialConnection.Console) return
		conn.context.dispatch(SerialConnectionActions.Disconnected)
		conn.handle.close()
		context.dispatch(SerialServerActions.RemoveConnection(portLocation))
	}

	fun openConnection(portLocation: String) {
		val state = context.state.value
		if (!state.availablePorts.containsKey(portLocation) || state.connections.containsKey(portLocation)) return
		val handle = openPortFactory(portLocation, ::onDataReceived, ::onPortDisconnected) ?: return
		context.dispatch(SerialServerActions.RegisterConnection(portLocation, SerialConnection.Console.create(handle, scope)))
	}

	fun closeConnection(portLocation: String) {
		val conn = context.state.value.connections[portLocation]
		if (conn !is SerialConnection.Console) return
		conn.context.dispatch(SerialConnectionActions.Disconnected)
		conn.handle.close()
		context.dispatch(SerialServerActions.RemoveConnection(portLocation))
	}

	fun openForFlashing(portLocation: String): FlashingHandler? {
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
			openPort: (portLocation: String, onDataReceived: (String, String) -> Unit, onPortDisconnected: (String) -> Unit) -> SerialPortHandle?,
			openFlashingPort: () -> FlashingHandler,
			scope: CoroutineScope,
		): SerialServer {
			val behaviours = listOf(SerialServerBaseBehaviour)
			val context = Context.create(
				initialState = SerialServerState(availablePorts = mapOf(), connections = mapOf()),
				scope = scope,
				behaviours = behaviours,
			)
			val server = SerialServer(
				context = context,
				scope = scope,
				openPortFactory = openPort,
				openFlashingPortFactory = openFlashingPort,
			)
			behaviours.forEach { it.observe(context) }
			return server
		}
	}
}
