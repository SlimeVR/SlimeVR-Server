package dev.slimevr.desktop.serial

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortEvent
import com.fazecast.jSerialComm.SerialPortMessageListener
import dev.slimevr.serial.SerialHandler
import dev.slimevr.serial.SerialListener
import io.eiren.util.logging.LogManager
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.stream.Stream
import kotlin.concurrent.timerTask
import kotlin.streams.asSequence
import kotlin.streams.asStream
import dev.slimevr.serial.SerialPort as SlimeSerialPort

class SerialPortWrapper(val port: SerialPort) : SlimeSerialPort() {
	override val portLocation: String
		get() = port.portLocation
	override val descriptivePortName: String
		get() = port.descriptivePortName

	override val vendorId: Int
		get() = port.vendorID

	override val productId: Int
		get() = port.productID
}

class DesktopSerialHandler :
	SerialHandler(),
	SerialPortMessageListener {
	private val listeners: MutableList<SerialListener> = CopyOnWriteArrayList()
	private val getDevicesTimer = Timer("GetDevicesTimer")
	private var currentPort: SerialPort? = null
	private var watchingNewDevices = false
	private var lastKnownPorts = setOf<SerialPortWrapper>()

	init {
		startWatchingNewDevices()
	}

	fun startWatchingNewDevices() {
		if (watchingNewDevices) return
		watchingNewDevices = true
		getDevicesTimer.scheduleAtFixedRate(
			timerTask {
				try {
					detectNewPorts()
				} catch (t: Throwable) {
					LogManager.severe(
						"[SerialHandler] Error while watching for new devices, cancelling the \"getDevicesTimer\".",
						t,
					)
					getDevicesTimer.cancel()
				}
			},
			0,
			3000,
		)
	}

	fun stopWatchingNewDevices() {
		if (!watchingNewDevices) return
		watchingNewDevices = false
		getDevicesTimer.cancel()
		getDevicesTimer.purge()
	}

	private fun onNewDevice(port: SerialPort) {
		listeners.forEach { it.onNewSerialDevice(SerialPortWrapper(port)) }
	}

	private fun onDeviceDel(port: SerialPort) {
		listeners.forEach { it.onSerialDeviceDeleted(SerialPortWrapper(port)) }
	}

	override fun addListener(channel: SerialListener) {
		listeners.add(channel)
	}

	override fun removeListener(channel: SerialListener) {
		listeners.removeIf { channel === it }
	}

	@Synchronized
	override fun openSerial(portLocation: String?, auto: Boolean): Boolean {
		LogManager.info("[SerialHandler] Trying to open: $portLocation, auto: $auto")
		val ports = SerialPort.getCommPorts()
		lastKnownPorts = ports.map { SerialPortWrapper(it) }.toSet()
		val newPort: SerialPort? = ports.find {
			(!auto && it.portLocation == portLocation) ||
				(auto && isKnownBoard(SerialPortWrapper(it)))
		}
		if (newPort == null) {
			LogManager.info(
				"[SerialHandler] No serial ports found to connect to (${ports.size}) total ports",
			)
			return false
		}
		if (isConnected) {
			if (SerialPortWrapper(newPort) != currentPort?.let { SerialPortWrapper(it) }) {
				LogManager.info(
					"[SerialHandler] Closing current serial port " +
						currentPort!!.descriptivePortName,
				)
				currentPort!!.removeDataListener()
				currentPort!!.closePort()
			} else {
				LogManager.info("[SerialHandler] Reusing already open port")
				listeners.forEach { it.onSerialConnected(SerialPortWrapper(currentPort!!)) }
				return true
			}
		}
		currentPort = newPort
		LogManager.info(
			"[SerialHandler] Trying to connect to new serial port " +
				currentPort!!.descriptivePortName,
		)
		currentPort?.setBaudRate(115200)
		currentPort?.clearRTS()
		currentPort?.clearDTR()
		if (currentPort?.openPort(1000) == false) {
			LogManager.warning(
				"[SerialHandler] Can't open serial port ${currentPort?.descriptivePortName}, last error: ${currentPort?.lastErrorCode}",

			)
			currentPort = null
			return false
		}
		currentPort?.addDataListener(this)
		listeners.forEach { it.onSerialConnected(SerialPortWrapper(currentPort!!)) }
		LogManager.info("[SerialHandler] Serial port ${newPort.descriptivePortName} is open")
		return true
	}

	override fun rebootRequest() {
		writeSerial("REBOOT")
	}

	override fun factoryResetRequest() {
		writeSerial("FRST")
	}

	override fun infoRequest() {
		writeSerial("GET INFO")
	}

	override fun wifiScanRequest() {
		writeSerial("GET WIFISCAN")
	}

	@Synchronized
	override fun closeSerial() {
		try {
			currentPort?.closePort()
			listeners.forEach { it.onSerialDisconnected() }
			LogManager.info(
				"[SerialHandler] Port ${currentPort?.descriptivePortName} closed okay",
			)
			currentPort = null
		} catch (e: Exception) {
			LogManager.warning(
				"[SerialHandler] Error closing port ${currentPort?.descriptivePortName}",
				e,
			)
		}
	}

	@Synchronized
	private fun writeSerial(serialText: String) {
		val os = currentPort?.outputStream ?: return
		val writer = OutputStreamWriter(os)
		try {
			writer.append(serialText).append("\n")
			writer.flush()
			addLog("-> $serialText\n")
		} catch (e: IOException) {
			addLog("[!] Serial error: ${e.message}\n")
			LogManager.warning("[SerialHandler] Serial port write error", e)
		}
	}

	override fun write(buff: ByteArray) {
		LogManager.info("[SerialHandler] WRITING $buff")
		currentPort?.outputStream?.write(buff)
	}

	@Synchronized
	override fun setWifi(ssid: String, passwd: String) {
		val os = currentPort?.outputStream ?: return
		val writer = OutputStreamWriter(os)
		try {
			writer.append("SET WIFI \"").append(ssid).append("\" \"").append(passwd).append("\"\n")
			writer.flush()
			addLog("-> SET WIFI \"$ssid\" \"${passwd.replace(".".toRegex(), "*")}\"\n")
		} catch (e: IOException) {
			addLog("$e\n")
			LogManager.warning("[SerialHandler] Serial port write error", e)
		}
	}

	fun addLog(str: String) {
		LogManager.info("[Serial] $str")
		listeners.forEach { it.onSerialLog(str) }
	}

	override fun getListeningEvents(): Int = (
		SerialPort.LISTENING_EVENT_PORT_DISCONNECTED
			or SerialPort.LISTENING_EVENT_DATA_RECEIVED
		)

	override fun serialEvent(event: SerialPortEvent) {
		when (event.eventType) {
			SerialPort.LISTENING_EVENT_DATA_RECEIVED -> {
				val newData = event.receivedData
				val s = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(newData)).toString()
				addLog(s)
			}

			SerialPort.LISTENING_EVENT_PORT_DISCONNECTED -> {
				closeSerial()
			}
		}
	}

	@get:Synchronized
	override val isConnected: Boolean
		get() = currentPort?.isOpen ?: false

	override fun getMessageDelimiter(): ByteArray = byteArrayOf(0x0A.toByte())

	override fun delimiterIndicatesEndOfMessage(): Boolean = true

	override val knownPorts: Stream<SerialPortWrapper>
		get() = SerialPort.getCommPorts()
			.asSequence()
			.map { SerialPortWrapper(it) }
			.filter { isKnownBoard(it) }
			.asStream()

	private fun detectNewPorts() {
		try {
			val addDifferences = knownPorts.asSequence() - lastKnownPorts
			val delDifferences = lastKnownPorts - knownPorts.asSequence().toSet()
			lastKnownPorts = SerialPort.getCommPorts().map { SerialPortWrapper(it) }.toSet()
			addDifferences.forEach { onNewDevice(it.port) }
			delDifferences.forEach { onDeviceDel(it.port) }
		} catch (e: Throwable) {
			LogManager
				.severe("[SerialHandler] Using serial ports is not supported on this platform", e)
			throw RuntimeException("Serial unsupported")
		}
	}

	override fun getCurrentPort(): dev.slimevr.serial.SerialPort? {
		val port = this.currentPort ?: return null
		return SerialPortWrapper(port)
	}
}
