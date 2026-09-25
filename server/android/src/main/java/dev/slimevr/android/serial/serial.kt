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
import dev.slimevr.logging.AppLogger
import dev.slimevr.serial.FlashingHandler
import dev.slimevr.serial.LineAssembler
import dev.slimevr.serial.SerialPortHandle
import dev.slimevr.serial.SerialPortInfo
import dev.slimevr.serial.SerialPortWatcher
import dev.slimevr.serial.SerialServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

private const val TAG = "SerialServer"

private const val ACTION_USB_SERIAL_PERMISSION = "dev.slimevr.android.USB_SERIAL_PERMISSION"

private const val BAUD_RATE = 115200
private const val WRITE_TIMEOUT_MS = 1000

private class AndroidSerialWatcher(
	private val context: Context,
	private val usbManager: UsbManager,
	override val changes: Flow<Unit>,
) : SerialPortWatcher {
	private val permissionRequested = mutableSetOf<String>()
	private val permissionIntent = PendingIntent.getBroadcast(
		context,
		0,
		Intent(ACTION_USB_SERIAL_PERMISSION).apply { setPackage(context.packageName) },
		PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
	)

	// Devices without permission are left out. Granting it sends a change and they show up then
	override suspend fun enumerate(): Map<String, SerialPortInfo> = withContext(Dispatchers.IO) {
		val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
		for (driver in drivers) {
			val name = driver.device.deviceName
			if (!usbManager.hasPermission(driver.device) && permissionRequested.add(name)) {
				AppLogger.serial.info("Requesting USB serial permission for $name")
				usbManager.requestPermission(driver.device, permissionIntent)
			}
		}
		permissionRequested.retainAll(drivers.map { it.device.deviceName }.toSet())

		drivers
			.filter { usbManager.hasPermission(it.device) }
			.associate { driver ->
				val location = driver.device.deviceName
				location to SerialPortInfo(
					portLocation = location,
					descriptivePortName = "${driver.device.productName ?: location} ($location)",
					vendorId = driver.device.vendorId,
					productId = driver.device.productId,
					serialNumber = runCatching { driver.device.serialNumber }.getOrNull(),
				)
			}
	}

	override suspend fun open(portLocation: String, onLine: (String) -> Unit, onClosed: () -> Unit): SerialPortHandle? = withContext(Dispatchers.IO) { openAndroidPort(portLocation, usbManager, onLine, onClosed) }

	override fun openForFlashing(): FlashingHandler = AndroidFlashingHandler(context, usbManager)
}

private fun openAndroidPort(
	portLocation: String,
	usbManager: UsbManager,
	onLine: (String) -> Unit,
	onClosed: () -> Unit,
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
		port.setParameters(BAUD_RATE, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
		port.dtr = false
		port.rts = false
	} catch (e: Exception) {
		Log.e(TAG, "Failed to open Android serial port $portLocation", e)
		try {
			connection.close()
		} catch (_: Exception) {}
		return null
	}

	val assembler = LineAssembler()
	val ioManager = SerialInputOutputManager(
		port,
		object : SerialInputOutputManager.Listener {
			override fun onNewData(data: ByteArray) = assembler.feed(data, data.size, onLine)

			override fun onRunError(e: Exception) = onClosed()
		},
	)
	ioManager.start()

	return SerialPortHandle(
		portLocation = portLocation,
		descriptivePortName = "${driver.device.productName ?: portLocation} ($portLocation)",
		writeCommand = { text ->
			withContext(Dispatchers.IO) {
				try {
					port.write("$text\n".toByteArray(), WRITE_TIMEOUT_MS)
				} catch (e: Exception) {
					Log.e(TAG, "Error writing to Android serial port $portLocation", e)
				}
			}
		},
		close = {
			withContext(Dispatchers.IO) {
				ioManager.stop()
				try {
					port.close()
				} catch (_: Exception) {}
			}
		},
	)
}

/** A change per USB attach, detach and permission result */
private fun createUsbChanges(context: Context): Flow<Unit> = callbackFlow {
	val receiver = object : BroadcastReceiver() {
		override fun onReceive(ctx: Context, intent: Intent) {
			trySend(Unit)
		}
	}
	val filter = IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED).apply {
		addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
		addAction(ACTION_USB_SERIAL_PERMISSION)
	}
	ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
	awaitClose { context.unregisterReceiver(receiver) }
}

fun createAndroidSerialServer(context: Context, scope: CoroutineScope): SerialServer {
	val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
	return SerialServer.create(AndroidSerialWatcher(context, usbManager, createUsbChanges(context)), scope)
}
