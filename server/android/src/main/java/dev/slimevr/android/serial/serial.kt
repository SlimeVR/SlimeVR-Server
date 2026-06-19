package dev.slimevr.android.serial

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import dev.slimevr.AppLogger
import dev.slimevr.serial.SerialPortHandle
import dev.slimevr.serial.SerialPortInfo
import dev.slimevr.serial.SerialServer
import dev.slimevr.util.safeLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

private const val TAG = "SerialServer"

private const val ACTION_USB_SERIAL_PERMISSION = "dev.slimevr.android.USB_SERIAL_PERMISSION"
private const val POLL_INTERVAL_MS = 3000L

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

private fun openAndroidPort(
	portLocation: String,
	usbManager: UsbManager,
	scope: CoroutineScope,
	onDataReceived: suspend (String, String) -> Unit,
	onPortDisconnected: suspend (String) -> Unit,
): SerialPortHandle? {
	val driver = UsbSerialProber.getDefaultProber()
		.findAllDrivers(usbManager)
		.find { driver -> driver.device.deviceName == portLocation }
		?: return null

	if (!usbManager.hasPermission(driver.device)) return null

	val connection = usbManager.openDevice(driver.device) ?: return null
	val port = driver.ports[0]

	try {
		port.open(connection)
		port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
		port.dtr = false
		port.rts = false
	} catch (e: Exception) {
		Log.e(TAG, "Failed to open Android serial port $portLocation", e)
		try {
			connection.close()
		} catch (_: Exception) {}
		return null
	}

	val readBuffer = StringBuilder(1024)

	val ioManager = SerialInputOutputManager(
		port,
		object : SerialInputOutputManager.Listener {
			override fun onNewData(data: ByteArray) {
				readBuffer.append(data.toString(Charsets.UTF_8))
				var newlineIdx = readBuffer.indexOf("\n")
				while (newlineIdx >= 0) {
					val line = readBuffer.substring(0, newlineIdx).trimEnd()
					readBuffer.delete(0, newlineIdx + 1)
					scope.safeLaunch { onDataReceived(portLocation, line) }
					newlineIdx = readBuffer.indexOf("\n")
				}
				if (readBuffer.length >= 1024) readBuffer.clear()
			}

			override fun onRunError(e: Exception) {
				scope.safeLaunch { onPortDisconnected(portLocation) }
			}
		},
	)
	ioManager.start()

	return SerialPortHandle(
		portLocation = portLocation,
		descriptivePortName = "${driver.device.productName ?: portLocation} ($portLocation)",
		writeCommand = { text -> port.write("$text\n".toByteArray(), 0) },
		close = {
			ioManager.stop()
			try {
				port.close()
			} catch (_: Exception) {}
		},
	)
}

private suspend fun runAndroidSerialPoller(
	context: Context,
	usbManager: UsbManager,
	server: SerialServer,
	wakeSignal: Channel<Unit>,
) {
	val permissionRequested = mutableSetOf<String>()
	var lastKnown: Set<String> = emptySet()
	val permissionIntent = PendingIntent.getBroadcast(
		context,
		0,
		Intent(ACTION_USB_SERIAL_PERMISSION).apply { setPackage(context.packageName) },
		PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
	)

	while (true) {
		try {
			val current = withContext(Dispatchers.IO) {
				UsbSerialProber.getDefaultProber()
					.findAllDrivers(usbManager)
					.filter { driver -> isKnownBoard(driver.device.vendorId, driver.device.productId) }
					.associateBy { driver -> driver.device.deviceName }
			}

			for ((deviceName, driver) in current) {
				if (!usbManager.hasPermission(driver.device) && deviceName !in permissionRequested) {
					AppLogger.serial.info("Requesting USB serial permission for $deviceName")
					usbManager.requestPermission(driver.device, permissionIntent)
					permissionRequested.add(deviceName)
				}
			}

			val authorized = current.filter { (_, driver) -> usbManager.hasPermission(driver.device) }

			val added = authorized.keys - lastKnown
			val removed = lastKnown - authorized.keys

			for (loc in added) {
				val driver = authorized.getValue(loc)
				permissionRequested.remove(loc)
				server.onPortDetected(
					SerialPortInfo(
						portLocation = loc,
						descriptivePortName = "${driver.device.productName ?: loc} ($loc)",
						vendorId = driver.device.vendorId,
						productId = driver.device.productId,
					),
				)
			}
			for (loc in removed) {
				server.onPortLost(loc)
			}

			lastKnown = authorized.keys
			permissionRequested.retainAll(current.keys)
		} catch (e: Exception) {
			AppLogger.serial.error(e, "Error polling Android serial ports")
		}

		withTimeoutOrNull(POLL_INTERVAL_MS) { wakeSignal.receive() }
	}
}

fun createAndroidSerialServer(context: Context, scope: CoroutineScope): SerialServer {
	val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
	val wakeSignal = Channel<Unit>(Channel.CONFLATED)

	val usbReceiver = object : BroadcastReceiver() {
		override fun onReceive(ctx: Context, intent: Intent) {
			wakeSignal.trySend(Unit)
		}
	}

	val intentFilter = IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED).apply {
		addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
		addAction(ACTION_USB_SERIAL_PERMISSION)
	}
	ContextCompat.registerReceiver(context, usbReceiver, intentFilter, ContextCompat.RECEIVER_NOT_EXPORTED)

	val server = SerialServer.create(
		openPort = { portLocation, onDataReceived, onPortDisconnected ->
			openAndroidPort(portLocation, usbManager, scope, onDataReceived, onPortDisconnected)
		},
		openFlashingPort = { AndroidFlashingHandler(usbManager) },
		scope = scope,
	)

	scope.safeLaunch { runAndroidSerialPoller(context, usbManager, server, wakeSignal) }

	return server
}
