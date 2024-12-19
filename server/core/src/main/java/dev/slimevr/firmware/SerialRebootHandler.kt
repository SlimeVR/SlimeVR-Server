package dev.slimevr.firmware

import dev.slimevr.VRServer
import dev.slimevr.serial.SerialListener
import dev.slimevr.serial.SerialPort
import java.util.concurrent.CopyOnWriteArrayList

interface SerialRebootListener {
	fun onSerialDeviceReconnect(deviceHandle: Pair<UpdateDeviceId<*>, () -> Unit>)
}

/**
 * This class watch for a serial device to disconnect then reconnect.
 * This is used to watch the user progress through the firmware update process
 */
class SerialRebootHandler(
	private val watchRestartQueue: MutableList<Pair<UpdateDeviceId<*>, () -> Unit>>,
	private val server: VRServer,
	// Could be moved to a list of listeners later
	private val serialRebootListener: SerialRebootListener,
) : SerialListener {

	private var currentPort: SerialPort? = null
	private val disconnectedDevices: MutableList<SerialPort> = CopyOnWriteArrayList()

	override fun onSerialConnected(port: SerialPort) {
		currentPort = port
	}

	override fun onSerialDisconnected() {
		currentPort = null
	}

	override fun onSerialLog(str: String) {
		if (str.contains("starting up...")) {
			val foundPort = watchRestartQueue.find { it.first.id == currentPort?.portLocation }
			if (foundPort != null) {
				disconnectedDevices.remove(currentPort)
				serialRebootListener.onSerialDeviceReconnect(foundPort)
				// once the restart detected we close the connection
				if (server.serialHandler.isConnected) {
					server.serialHandler.closeSerial()
				}
			}
		}
	}

	override fun onNewSerialDevice(port: SerialPort) {
		val foundPort = watchRestartQueue.find { it.first.id == port.portLocation }
		if (foundPort != null && disconnectedDevices.contains(port)) {
			disconnectedDevices.remove(port)
			serialRebootListener.onSerialDeviceReconnect(foundPort)
			// once the restart detected we close the connection
			if (server.serialHandler.isConnected) {
				server.serialHandler.closeSerial()
			}
		}
	}

	override fun onSerialDeviceDeleted(port: SerialPort) {
		val foundPort = watchRestartQueue.find { it.first.id == port.portLocation }
		if (foundPort != null) {
			disconnectedDevices.add(port)
		}
	}
}
