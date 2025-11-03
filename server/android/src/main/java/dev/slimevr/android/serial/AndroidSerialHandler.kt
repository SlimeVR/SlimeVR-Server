package dev.slimevr.android.serial

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import dev.slimevr.serial.SerialHandler
import dev.slimevr.serial.SerialListener
import io.eiren.util.logging.LogManager
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.concurrent.CopyOnWriteArrayList
import java.util.stream.Stream
import kotlin.streams.asSequence
import kotlin.streams.asStream
import dev.slimevr.serial.SerialPort as SlimeSerialPort

class SerialPortWrapper(val port: UsbSerialPort) : SlimeSerialPort() {
	override val portLocation: String
		get() = port.device.deviceName
	override val descriptivePortName: String
		get() = "${port.device.productName} (${port.device.deviceName})"

	override val vendorId: Int
		get() = port.device.vendorId

	override val productId: Int
		get() = port.device.productId
}

class AndroidSerialHandler(val activity: AppCompatActivity) :
	SerialHandler(),
	SerialInputOutputManager.Listener {

	private var usbIoManager: SerialInputOutputManager? = null

	private val listeners: MutableList<SerialListener> = CopyOnWriteArrayList()
	private var lastKnownPorts = setOf<SerialPortWrapper>()
	private val manager = activity.getSystemService(Context.USB_SERVICE) as UsbManager
	private var currentPort: SerialPortWrapper? = null
	private var requestingPermission: String = ""
	private var readBuffer: StringBuilder = StringBuilder(1024)

	override val isConnected: Boolean
		get() = currentPort?.port?.isOpen ?: false

	override val knownPorts: Stream<SerialPortWrapper>
		get() = getPorts()
			.asSequence()
			.map { SerialPortWrapper(it.ports[0]) }
			.filter { isKnownBoard(it) }
			.asStream()

	val usbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			when (intent.action) {
				UsbManager.ACTION_USB_DEVICE_ATTACHED, UsbManager.ACTION_USB_DEVICE_DETACHED -> {
					// Use device from `UsbManager.EXTRA_DEVICE` if this is a problem
					detectNewPorts()
				}

				ACTION_USB_PERMISSION -> {
					// TODO: We can probably receive this event in the server to avoid
					//  polling, but for now we can just ignore it. (Note: This event is
					//  not currently registered, so it will never fire.)
				}
			}
		}
	}

	init {
		val intentFilter = IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED)
		intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)

		// Listen for USB device attach/detach
		ContextCompat.registerReceiver(
			activity,
			usbReceiver,
			intentFilter,
			ContextCompat.RECEIVER_NOT_EXPORTED,
		)

		// Detect initial serial ports
		detectNewPorts()
	}

	private fun getPorts(): List<UsbSerialDriver> = UsbSerialProber.getDefaultProber().findAllDrivers(manager)

	private fun onNewDevice(port: SerialPortWrapper) {
		// If we missed clearing this on disconnect/close, clear it on discovery
		if (requestingPermission == port.portLocation) {
			requestingPermission = ""
		}

		LogManager.info("[SerialHandler] Device added: ${port.descriptivePortName}")
		listeners.forEach { it.onNewSerialDevice(port) }
	}

	private fun onDeviceDel(port: SerialPortWrapper) {
		// Remove permission request on disconnect so reconnecting re-requests
		if (requestingPermission == port.portLocation) {
			requestingPermission = ""
		}

		// If we're currently using this port, close it
		currentPort?.portLocation.let { currentPortLocation ->
			if (currentPortLocation == port.portLocation) {
				closeSerial()
			}
		}

		// If this port is still open for whatever reason, close it
		if (port.port.isOpen) {
			port.port.close()
		}

		LogManager.info("[SerialHandler] Device removed: ${port.descriptivePortName}")
		listeners.forEach { it.onSerialDeviceDeleted(port) }
	}

	private fun detectNewPorts() {
		val addDifferences = knownPorts.asSequence() - lastKnownPorts
		val delDifferences = lastKnownPorts - knownPorts.asSequence().toSet()
		lastKnownPorts = knownPorts.asSequence().toSet()
		addDifferences.forEach { onNewDevice(it) }
		delDifferences.forEach { onDeviceDel(it) }
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
		lastKnownPorts = knownPorts.asSequence().toSet()
		val newPort = lastKnownPorts.find {
			(!auto && it.portLocation == portLocation) || (auto && isKnownBoard(it))
		}

		if (newPort == null) {
			LogManager.info(
				"[SerialHandler] No serial ports found to connect to (${lastKnownPorts.size}) total ports",
			)
			return false
		}

		if (isConnected) {
			val port = currentPort!!
			if (newPort != port) {
				LogManager.info(
					"[SerialHandler] Closing current serial port " +
						port.descriptivePortName,
				)
				closeSerial()
			} else {
				LogManager.info("[SerialHandler] Reusing already open port")
				listeners.forEach { it.onSerialConnected(port) }
				return true
			}
		}

		LogManager.info(
			"[SerialHandler] Trying to connect to new serial port " +
				newPort.descriptivePortName,
		)

		if (!manager.hasPermission(newPort.port.device)) {
			val flags = PendingIntent.FLAG_IMMUTABLE
			val usbPermissionIntent = PendingIntent.getBroadcast(
				activity,
				0,
				Intent(ACTION_USB_PERMISSION),
				flags,
			)
			if (requestingPermission != newPort.portLocation) {
				LogManager.info("[SerialHandler] Requesting permission for ${newPort.portLocation}")
				manager.requestPermission(newPort.port.device, usbPermissionIntent)
				requestingPermission = newPort.portLocation
			} else {
				LogManager.info("[SerialHandler] Already requested permission for ${newPort.portLocation}, skipping")
			}
			LogManager.warning(
				"[SerialHandler] Can't open serial port ${newPort.descriptivePortName}, invalid permissions",
			)
			return false
		}

		// If we have permission, we aren't requesting anymore
		requestingPermission = ""

		val connection = manager.openDevice(newPort.port.device)
		if (connection == null) {
			LogManager.warning(
				"[SerialHandler] Can't open serial port ${newPort.descriptivePortName}, connection failed",
			)
			return false
		}
		newPort.port.open(connection)
		newPort.port.setParameters(115200, 8, 1, UsbSerialPort.PARITY_NONE)
		usbIoManager = SerialInputOutputManager(newPort.port, this).apply {
			start()
		}
		listeners.forEach { it.onSerialConnected(newPort) }
		currentPort = newPort
		LogManager.info("[SerialHandler] Serial port ${newPort.descriptivePortName} is open")
		return true
	}

	@Synchronized
	private fun writeSerial(serialText: String, print: Boolean = false) {
		try {
			currentPort?.port?.write("${serialText}\n".toByteArray(), 0)
			if (print) {
				addLog("-> $serialText\n")
			}
		} catch (e: IOException) {
			addLog("[!] Serial error: ${e.message}\n")
			LogManager.warning("[SerialHandler] Serial port write error", e)
		}
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

	override fun customCommandRequest(command: String) {
		writeSerial(command)
	}

	override fun closeSerial() {
		try {
			if (isConnected) {
				currentPort?.port?.close()
			}
			listeners.forEach { it.onSerialDisconnected() }
			LogManager.info(
				"[SerialHandler] Port ${currentPort?.descriptivePortName} closed okay",
			)
			usbIoManager?.stop()
			usbIoManager = null
			currentPort = null
			requestingPermission = ""
			readBuffer.clear()
		} catch (e: Exception) {
			LogManager.warning(
				"[SerialHandler] Error closing port ${currentPort?.descriptivePortName}",
				e,
			)
		}
	}

	override fun write(buff: ByteArray) {
		currentPort?.port?.write(buff, 0)
	}

	@Synchronized
	override fun setWifi(ssid: String, passwd: String) {
		writeSerial("SET WIFI \"${ssid}\" \"${passwd}\"")
		addLog("-> SET WIFI \"$ssid\" \"${passwd.replace(".".toRegex(), "*")}\"\n")
	}

	override fun getCurrentPort(): SlimeSerialPort? = this.currentPort

	private fun addLog(str: String, server: Boolean = true) {
		LogManager.info("[Serial] $str")
		listeners.forEach { it.onSerialLog(str, server) }
	}

	override fun onNewData(data: ByteArray?) {
		if (data != null) {
			// Collect serial in a buffer until newline (or character limit)
			// This is somewhat of a workaround for Android serial buffer being smaller
			//  than on desktop, so we don't read full lines and it causes parsing issues
			readBuffer.append(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(data)))

			if (readBuffer.contains('\n') || readBuffer.length >= 1024) {
				addLog(readBuffer.toString(), false)
				readBuffer.clear()
			}
		}
	}

	override fun onRunError(e: java.lang.Exception?) {}

	companion object {
		private const val ACTION_USB_PERMISSION = "dev.slimevr.USB_PERMISSION"
	}
}
