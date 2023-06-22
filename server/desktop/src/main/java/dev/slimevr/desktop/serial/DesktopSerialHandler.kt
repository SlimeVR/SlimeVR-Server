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
}

class DesktopSerialHandler : SerialHandler(), SerialPortMessageListener {
	private val listeners: MutableList<SerialListener> = CopyOnWriteArrayList()
	private val getDevicesTimer = Timer("GetDevicesTimer")
	private var currentPort: SerialPort? = null
	private var watchingNewDevices = false
	private var lastKnownPorts = setOf<SerialPort>()

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
						t
					)
					getDevicesTimer.cancel()
				}
			},
			0,
			3000
		)
	}

	fun stopWatchingNewDevices() {
		if (!watchingNewDevices) return
		watchingNewDevices = false
		getDevicesTimer.cancel()
		getDevicesTimer.purge()
	}

	fun onNewDevice(port: SerialPort) {
		listeners.forEach { it.onNewSerialDevice(SerialPortWrapper(port)) }
	}

	override fun addListener(channel: SerialListener) {
		listeners.add(channel)
	}

	override fun removeListener(l: SerialListener) {
		listeners.removeIf { l === it }
	}

	@Synchronized
	override fun openSerial(portLocation: String?, auto: Boolean): Boolean {
		LogManager.info("[SerialHandler] Trying to open: $portLocation, auto: $auto")
		val ports = SerialPort.getCommPorts()
		lastKnownPorts = ports.toSet()
		val newPort: SerialPort? = ports.find {
			return (!auto && it.portLocation == portLocation) ||
				(auto && isKnownBoard(it.descriptivePortName))
		}
		if (newPort == null) {
			LogManager.info(
				"[SerialHandler] No serial ports found to connect to (${ports.size}) total ports"
			)
			return false
		}
		if (isConnected) {
			if (newPort.portLocation != currentPort!!.portLocation ||
				newPort.descriptivePortName != currentPort!!.descriptivePortName
			) {
				LogManager.info(
					"[SerialHandler] Closing current serial port " +
						currentPort!!.descriptivePortName
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
				currentPort!!.descriptivePortName
		)
		currentPort!!.setBaudRate(115200)
		currentPort!!.clearRTS()
		currentPort!!.clearDTR()
		if (!currentPort!!.openPort(1000)) {
			LogManager.warning(
				"[SerialHandler] Can't open serial port ${currentPort!!.descriptivePortName}, last error: " +
					currentPort!!.lastErrorCode
			)
			currentPort = null
			return false
		}
		currentPort!!.addDataListener(this)
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

	@Synchronized
	override fun closeSerial() {
		try {
			if (currentPort != null) currentPort!!.closePort()
			listeners.forEach { it.onSerialDisconnected() }
			LogManager.info(
				"[SerialHandler] Port ${currentPort?.descriptivePortName} closed okay"
			)
			currentPort = null
		} catch (e: Exception) {
			LogManager.warning(
				"[SerialHandler] Error closing port ${currentPort?.descriptivePortName}",
				e
			)
		}
	}

	@Synchronized
	private fun writeSerial(serialText: String) {
		if (currentPort == null) return
		val os = currentPort!!.outputStream
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

	@Synchronized
	override fun setWifi(ssid: String, passwd: String) {
		if (currentPort == null) return
		val os = currentPort!!.outputStream
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

	override fun getListeningEvents(): Int {
		return (
			SerialPort.LISTENING_EVENT_PORT_DISCONNECTED
				or SerialPort.LISTENING_EVENT_DATA_RECEIVED
			)
	}

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

	override fun getMessageDelimiter(): ByteArray {
		return byteArrayOf(0x0A.toByte())
	}

	override fun delimiterIndicatesEndOfMessage(): Boolean {
		return true
	}

	override val knownPorts: Stream<SerialPortWrapper>
		get() = SerialPort.getCommPorts()
			.asSequence()
			.filter { isKnownBoard(it.descriptivePortName) }
			.map { SerialPortWrapper(it) }
			.asStream()

	private fun isKnownBoard(com: String): Boolean {
		val lowerCom = com.lowercase(Locale.getDefault())
		return (
			lowerCom.contains("ch340") ||
				lowerCom.contains("cp21") ||
				lowerCom.contains("ch910") ||
				(
					lowerCom.contains("usb") &&
						lowerCom.contains("seri")
					)
			)
	}

	private fun detectNewPorts() {
		try {
			val differences = knownPorts.asSequence().map { it.port } - lastKnownPorts
			lastKnownPorts = SerialPort.getCommPorts().toSet()
			differences.forEach { onNewDevice(it) }
		} catch (e: Throwable) {
			LogManager
				.severe("[SerialHandler] Using serial ports is not supported on this platform", e)
			throw RuntimeException("Serial unsupported")
		}
	}
}
