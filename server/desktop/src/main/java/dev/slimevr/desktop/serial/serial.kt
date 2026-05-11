package dev.slimevr.desktop.serial

import com.fazecast.jSerialComm.SerialPortEvent
import com.fazecast.jSerialComm.SerialPortMessageListener
import dev.slimevr.AppLogger
import dev.slimevr.serial.SerialPortHandle
import dev.slimevr.serial.SerialPortInfo
import dev.slimevr.serial.SerialServer
import dev.slimevr.util.safeLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import com.fazecast.jSerialComm.SerialPort as JSerialPort

private val SUPPORTED_BOARDS: Set<Pair<Int, Int>> = setOf(
	Pair(0x1A86, 0x7522), // CH340
	Pair(0x1A86, 0x7523), // CH340
	Pair(0x1A86, 0x5523), // CH341
	Pair(0x1A86, 0x55D3), // CH343
	Pair(0x1A86, 0x55D4), // CH9102x
	Pair(0x10C4, 0xEA60), // CP210x
	Pair(0x303A, 0x1001), // ESP32-S3
	Pair(0x303A, 0x0002), // ESP32
	Pair(0x0403, 0x6001), // FTDI FT232
)

private fun isKnownBoard(vid: Int, pid: Int) = SUPPORTED_BOARDS.contains(vid to pid)

private fun openPort(
	portLocation: String,
	scope: CoroutineScope,
	onDataReceived: suspend (String, String) -> Unit,
	onPortDisconnected: suspend (String) -> Unit,
): SerialPortHandle? {
	val port = JSerialPort.getCommPorts().find { it.portLocation == portLocation } ?: return null
	port.baudRate = 115200
	port.clearRTS()
	port.clearDTR()
	if (!port.openPort(1000)) return null

	// Anonymous object is required by the jSerialComm API
	port.addDataListener(object : SerialPortMessageListener {
		override fun getListeningEvents() = JSerialPort.LISTENING_EVENT_DATA_RECEIVED or JSerialPort.LISTENING_EVENT_PORT_DISCONNECTED

		override fun getMessageDelimiter() = byteArrayOf(0x0A)
		override fun delimiterIndicatesEndOfMessage() = true

		override fun serialEvent(event: SerialPortEvent) {
			when (event.eventType) {
				JSerialPort.LISTENING_EVENT_DATA_RECEIVED -> {
					val line = event.receivedData.toString(Charsets.UTF_8).trimEnd()
					scope.safeLaunch { onDataReceived(portLocation, line) }
				}

				JSerialPort.LISTENING_EVENT_PORT_DISCONNECTED ->
					scope.safeLaunch { onPortDisconnected(portLocation) }
			}
		}
	})

	return SerialPortHandle(
		portLocation = portLocation,
		descriptivePortName = port.descriptivePortName,
		writeCommand = { text ->
			OutputStreamWriter(port.outputStream).append(text).append("\n").flush()
		},
		close = {
			port.removeDataListener()
			port.closePort()
		},
	)
}

private suspend fun runSerialPoller(server: SerialServer) {
	var lastKnown: Set<String> = emptySet()

	while (true) {
		try {
			val current = withContext(Dispatchers.IO) {
				JSerialPort.getCommPorts()
					.filter { isKnownBoard(it.vendorID, it.productID) }
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
	scope.safeLaunch { runSerialPoller(server) }
	return server
}
