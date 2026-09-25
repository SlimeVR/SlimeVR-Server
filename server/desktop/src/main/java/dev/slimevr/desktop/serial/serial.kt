package dev.slimevr.desktop.serial

import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import dev.slimevr.logging.AppLogger
import dev.slimevr.serial.FlashingHandler
import dev.slimevr.serial.LineAssembler
import dev.slimevr.serial.SerialPortHandle
import dev.slimevr.serial.SerialPortInfo
import dev.slimevr.serial.SerialPortWatcher
import dev.slimevr.serial.SerialServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import com.fazecast.jSerialComm.SerialPort as JSerialPort

private const val BAUD_RATE = 115200
private const val OPEN_TIMEOUT_MS = 1000

/** jSerialComm reports this for a port with no USB identity, like a built-in UART */
private const val NO_VENDOR_ID = -1

private class DesktopSerialWatcher(override val changes: Flow<Unit>) : SerialPortWatcher {
	override suspend fun enumerate(): Map<String, SerialPortInfo> = withContext(Dispatchers.IO) {
		JSerialPort.getCommPorts()
			.filter { it.vendorID != NO_VENDOR_ID }
			.associate { port ->
				port.portLocation to SerialPortInfo(
					portLocation = port.portLocation,
					descriptivePortName = port.descriptivePortName,
					vendorId = port.vendorID,
					productId = port.productID,
					serialNumber = port.serialNumber?.takeIf { it.isNotBlank() && it != "Unknown" },
				)
			}
	}

	override suspend fun open(portLocation: String, onLine: (String) -> Unit, onClosed: () -> Unit): SerialPortHandle? = withContext(Dispatchers.IO) { openPort(portLocation, onLine, onClosed) }

	override fun openForFlashing(): FlashingHandler = DesktopFlashingHandler()
}

private suspend fun openPort(portLocation: String, onLine: (String) -> Unit, onClosed: () -> Unit): SerialPortHandle? {
	val port = JSerialPort.getCommPorts().find { it.portLocation == portLocation } ?: return null

	try {
		port.baudRate = BAUD_RATE
		port.clearRTS()
		port.clearDTR()
		if (!port.openPort(OPEN_TIMEOUT_MS)) return null
	} catch (e: Exception) {
		AppLogger.serial.error(e, "Failed to open serial port: $portLocation")
		return null
	}

	val assembler = LineAssembler()
	try {
		// Anonymous object is required by the jSerialComm API
		port.addDataListener(object : SerialPortDataListener {
			override fun getListeningEvents() = JSerialPort.LISTENING_EVENT_DATA_AVAILABLE or JSerialPort.LISTENING_EVENT_PORT_DISCONNECTED

			override fun serialEvent(event: SerialPortEvent) {
				when (event.eventType) {
					JSerialPort.LISTENING_EVENT_DATA_AVAILABLE -> {
						val available = port.bytesAvailable()
						if (available <= 0) return
						val data = ByteArray(available)
						val read = port.readBytes(data, available)
						if (read > 0) assembler.feed(data, read, onLine)
					}

					JSerialPort.LISTENING_EVENT_PORT_DISCONNECTED -> onClosed()
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

private fun createPortChanges(): Flow<Unit> = when (CURRENT_PLATFORM) {
	Platform.LINUX -> createLinuxPortChanges()
	Platform.WINDOWS -> createWindowsPortChanges()
	Platform.OSX -> createMacOsPortChanges()
	Platform.UNKNOWN -> flow { error("No serial hotplug source for this platform") }
}

fun createDesktopSerialServer(scope: CoroutineScope): SerialServer = SerialServer.create(DesktopSerialWatcher(createPortChanges()), scope)
