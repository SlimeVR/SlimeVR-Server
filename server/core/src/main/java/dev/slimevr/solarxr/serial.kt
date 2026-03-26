package dev.slimevr.solarxr

import dev.slimevr.serial.SerialConnection
import dev.slimevr.serial.SerialPortInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import solarxr_protocol.rpc.CloseSerialRequest
import solarxr_protocol.rpc.NewSerialDeviceResponse
import solarxr_protocol.rpc.OpenSerialRequest
import solarxr_protocol.rpc.SerialDevice
import solarxr_protocol.rpc.SerialDevicesRequest
import solarxr_protocol.rpc.SerialDevicesResponse
import solarxr_protocol.rpc.SerialTrackerCustomCommandRequest
import solarxr_protocol.rpc.SerialTrackerFactoryResetRequest
import solarxr_protocol.rpc.SerialTrackerGetInfoRequest
import solarxr_protocol.rpc.SerialTrackerGetWifiScanRequest
import solarxr_protocol.rpc.SerialTrackerRebootRequest
import solarxr_protocol.rpc.SerialUpdateResponse

val SerialConsoleBehaviour = SolarXRConnectionBehaviour(
	observer = { conn ->
		val scope = conn.context.scope
		val serialServer = conn.serverContext.serialServer

		// We assume that you can only subscribe to one serial console
		// at a time
		var logSubscription: Job? = null
		var activePortLocation: String? = null

		// Notify client of new serial devices as they are detected.
		// Existing devices at connection time are not sent here. the client
		// should send SerialDevicesRequest to get the current list.
		var prevPortKeys = serialServer.context.state.value.availablePorts.keys.toSet()
		serialServer.context.state
			.map { it.availablePorts }
			.distinctUntilChanged()
			.onEach { ports ->
				(ports.keys - prevPortKeys).forEach { key ->
					conn.sendRpc(NewSerialDeviceResponse(device = ports[key]!!.toSerialDevice()))
				}
				prevPortKeys = ports.keys.toSet()
			}
			.launchIn(scope)

		conn.rpcDispatcher.on<SerialDevicesRequest> {
			conn.sendRpc(
				SerialDevicesResponse(
					devices = serialServer.context.state.value.availablePorts.values
						.map { it.toSerialDevice() },
				),
			)
		}

		conn.rpcDispatcher.on<OpenSerialRequest> { req ->
			val portLocation = if (req.auto == true) {
				serialServer.context.state.value.availablePorts.keys.firstOrNull()
			} else {
				req.port
			} ?: return@on

			logSubscription?.cancel()
			logSubscription = null
			activePortLocation = null

			serialServer.openConnection(portLocation)

			val connection = serialServer.context.state.value.connections[portLocation]
			if (connection !is SerialConnection.Console) return@on

			activePortLocation = portLocation
			var lastSentCount = 0

			logSubscription = scope.launch {
				var disconnected = false
				connection.context.state.collect { connState ->
					if (disconnected) return@collect

					connState.logLines.drop(lastSentCount).forEach { line ->
						conn.sendRpc(SerialUpdateResponse(log = line + "\n"))
					}
					lastSentCount = connState.logLines.size

					if (!connState.connected) {
						disconnected = true
						activePortLocation = null
						conn.sendRpc(SerialUpdateResponse(closed = true))
					}
				}
			}
		}

		conn.rpcDispatcher.on<CloseSerialRequest> {
			logSubscription?.cancel()
			logSubscription = null
			activePortLocation = null
		}

		conn.rpcDispatcher.on<SerialTrackerRebootRequest> {
			val portLocation = activePortLocation ?: return@on
			val c = serialServer.context.state.value.connections[portLocation]
			if (c is SerialConnection.Console) c.handle.writeCommand("REBOOT")
		}

		conn.rpcDispatcher.on<SerialTrackerGetInfoRequest> {
			val portLocation = activePortLocation ?: return@on
			val c = serialServer.context.state.value.connections[portLocation]
			if (c is SerialConnection.Console) c.handle.writeCommand("GET INFO")
		}

		conn.rpcDispatcher.on<SerialTrackerFactoryResetRequest> {
			val portLocation = activePortLocation ?: return@on
			val c = serialServer.context.state.value.connections[portLocation]
			if (c is SerialConnection.Console) c.handle.writeCommand("FRST")
		}

		conn.rpcDispatcher.on<SerialTrackerGetWifiScanRequest> {
			val portLocation = activePortLocation ?: return@on
			val c = serialServer.context.state.value.connections[portLocation]
			if (c is SerialConnection.Console) c.handle.writeCommand("GET WIFISCAN")
		}

		conn.rpcDispatcher.on<SerialTrackerCustomCommandRequest> { req ->
			val portLocation = activePortLocation ?: return@on
			val command = req.command ?: return@on
			val c = serialServer.context.state.value.connections[portLocation]
			if (c is SerialConnection.Console) c.handle.writeCommand(command)
		}
	},
)
