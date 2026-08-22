package dev.slimevr.desktop.serial

import com.fazecast.jSerialComm.SerialPortEvent
import com.fazecast.jSerialComm.SerialPortMessageListener
import dev.slimevr.logging.AppLogger
import dev.slimevr.serial.SerialPortHandle
import dev.slimevr.serial.SerialPortInfo
import dev.slimevr.serial.SerialServer
import dev.slimevr.serial.isKnownSerialBoard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import com.fazecast.jSerialComm.SerialPort as JSerialPort

private suspend fun openPort(
	portLocation: String,
	scope: CoroutineScope,
	onDataReceived: suspend (String, String) -> Unit,
	onPortDisconnected: suspend (String) -> Unit,
): SerialPortHandle? {
	val port = JSerialPort.getCommPorts().find { it.portLocation == portLocation } ?: return null

	try {
		port.baudRate = 115200
		port.clearRTS()
		port.clearDTR()
		if (!port.openPort(1000)) return null
	} catch (e: Exception) {
		AppLogger.serial.error(e, "Failed to open serial port: $portLocation")
		return null
	}

	try {
		// Anonymous object is required by the jSerialComm API
		port.addDataListener(object : SerialPortMessageListener {
			override fun getListeningEvents() = JSerialPort.LISTENING_EVENT_DATA_RECEIVED or JSerialPort.LISTENING_EVENT_PORT_DISCONNECTED

			override fun getMessageDelimiter() = byteArrayOf(0x0A)
			override fun delimiterIndicatesEndOfMessage() = true

			override fun serialEvent(event: SerialPortEvent) {
				when (event.eventType) {
					JSerialPort.LISTENING_EVENT_DATA_RECEIVED -> {
						try {
							val data = event.receivedData
							if (data != null) {
								val line = data.toString(Charsets.UTF_8).trimEnd()
								scope.launch { onDataReceived(portLocation, line) }
							}
						} catch (e: Exception) {
							scope.launch { AppLogger.serial.error(e, "Error reading serial data from $portLocation") }
						}
					}

					JSerialPort.LISTENING_EVENT_PORT_DISCONNECTED ->
						scope.launch { onPortDisconnected(portLocation) }
				}
			}
		})
	} catch (e: Exception) {
		AppLogger.serial.error(e, "Failed to add serial listener for $portLocation")
		try {
			port.closePort()
		} catch (_: Exception) {}
		return null
	}

	return SerialPortHandle(
		portLocation = portLocation,
		descriptivePortName = port.descriptivePortName,
		writeCommand = { text ->
			try {
				OutputStreamWriter(port.outputStream).append(text).append("\n").flush()
			} catch (e: Exception) {
				AppLogger.serial.error(e, "Error writing to serial port $portLocation")
			}
		},
		close = {
			try {
				port.removeDataListener()
				port.closePort()
			} catch (e: Exception) {
				AppLogger.serial.error(e, "Error closing serial port $portLocation")
			}
		},
	)
}

private suspend fun runSerialPoller(server: SerialServer) {
	var lastKnown: Set<String> = emptySet()

	while (true) {
		try {
			val current = withContext(Dispatchers.IO) {
				JSerialPort.getCommPorts()
					.filter { isKnownSerialBoard(it.vendorID, it.productID) }
					.associate { port ->
						port.portLocation to SerialPortInfo(
							portLocation = port.portLocation,
							descriptivePortName = port.descriptivePortName,
							vendorId = port.vendorID,
							productId = port.productID,
						)
					}
			}

			val added = current.keys - lastKnown
			val removed = lastKnown - current.keys

			added.forEach { loc -> server.onPortDetected(current.getValue(loc)) }
			removed.forEach { loc -> server.onPortLost(loc) }

			lastKnown = current.keys
		} catch (e: Exception) {
			AppLogger.serial.error(e, "Error polling serial ports")
		}
		delay(3000)
	}
}

fun createDesktopSerialServer(scope: CoroutineScope): SerialServer {
	val server = SerialServer.create(
		openPort = { portLocation, onDataReceived, onPortDisconnected -> openPort(portLocation, scope, onDataReceived, onPortDisconnected) },
		openFlashingPort = { DesktopFlashingHandler() },
		scope = scope,
	)
	scope.launch { runSerialPoller(server) }
	return server
}
