package dev.slimevr.android.serial

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import dev.llelievr.espflashkotlin.Flasher
import dev.llelievr.espflashkotlin.FlasherSerialInterface
import dev.llelievr.espflashkotlin.FlashingProgressListener
import dev.slimevr.firmware.DownloadedFirmwarePart
import dev.slimevr.firmware.FirmwareFlasher
import dev.slimevr.logging.AppLogger
import dev.slimevr.serial.FlashingHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

private const val ACTION_USB_FLASH_PERMISSION = "dev.slimevr.android.USB_FLASH_PERMISSION"
private const val USB_PERMISSION_TIMEOUT_MS = 30_000L

class AndroidFlashingHandler(
	private val context: Context,
	private val usbManager: UsbManager,
) : FlashingHandler {
	private var port: UsbSerialPort? = null
	private var readTimeout: Int = 1000
	private var readPacketSize: Int = 64
	private val pendingBytes = ArrayDeque<Byte>()

	override fun openSerial(port: Any) {
		val portLocation = port as? String ?: error("expected port location string")
		val driver = UsbSerialProber.getDefaultProber()
			.findAllDrivers(usbManager)
			.find { driver -> driver.device.deviceName == portLocation }
			?: error("Unable to find serial port $portLocation")

		ensurePermission(driver.device)
		val connection = usbManager.openDevice(driver.device)
			?: error("Unable to open USB device for $portLocation")

		val usbPort = driver.ports[0]
		usbPort.open(connection)
		usbPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
		readPacketSize = usbPort.readEndpoint.maxPacketSize.coerceAtLeast(1)
		this.port = usbPort
	}

	private fun ensurePermission(device: UsbDevice) {
		if (usbManager.hasPermission(device)) return

		val latch = CountDownLatch(1)
		var granted = false
		val action = "$ACTION_USB_FLASH_PERMISSION.${device.deviceName.hashCode()}"
		val receiver = object : BroadcastReceiver() {
			override fun onReceive(ctx: Context, intent: Intent) {
				if (intent.action != action) return
				val permissionDevice = intent.getUsbDeviceExtra()
				if (permissionDevice?.deviceName != device.deviceName) return
				granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
				latch.countDown()
			}
		}

		ContextCompat.registerReceiver(
			context,
			receiver,
			IntentFilter(action),
			ContextCompat.RECEIVER_NOT_EXPORTED,
		)
		try {
			val permissionIntent = PendingIntent.getBroadcast(
				context,
				device.deviceId,
				Intent(action).apply { setPackage(context.packageName) },
				PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
			)
			usbManager.requestPermission(device, permissionIntent)
			if (!latch.await(USB_PERMISSION_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
				error("Timed out waiting for USB permission for ${device.deviceName}")
			}
			if (!granted || !usbManager.hasPermission(device)) {
				error("USB permission denied for ${device.deviceName}")
			}
		} finally {
			context.unregisterReceiver(receiver)
		}
	}

	override fun closeSerial() {
		try {
			port?.close()
		} catch (_: Exception) {
		}
		port = null
		pendingBytes.clear()
	}

	override fun write(data: ByteArray) {
		val p = port ?: error("no port to write")
		p.write(data, readTimeout)
	}

	override fun read(length: Int): ByteArray {
		val p = port ?: error("no port to read")
		val result = ByteArray(length)
		var offset = 0

		while (offset < length) {
			result[offset] = pendingBytes.removeFirstOrNull() ?: break
			offset++
		}

		if (offset < length) {
			val readBuffer = ByteArray(maxOf(readPacketSize, length - offset))
			val read = p.read(readBuffer, readTimeout)
			val copied = minOf(read, length - offset)
			readBuffer.copyInto(result, offset, 0, copied)
			offset += copied
			for (i in copied until read) {
				pendingBytes.addLast(readBuffer[i])
			}
		}

		return if (offset == 0) result else result.copyOf(offset)
	}

	override fun setDTR(value: Boolean) {
		val p = port ?: error("no port to set DTR")
		p.dtr = value
	}

	override fun setRTS(value: Boolean) {
		val p = port ?: error("no port to set RTS")
		p.rts = value
	}

	override fun changeBaud(baud: Int) {
		val p = port ?: error("no port to change baud")
		p.setParameters(baud, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
	}

	override fun setReadTimeout(timeout: Long) {
		readTimeout = timeout.toInt().coerceAtLeast(1)
	}

	override fun availableBytes(): Int {
		return pendingBytes.size
	}

	override fun flushIOBuffers() {
		val p = port ?: error("no port to flush")
		pendingBytes.clear()
		try {
			p.purgeHwBuffers(true, true)
		} catch (_: UnsupportedOperationException) {
			// CH340 does not support hardware purge in usb-serial-for-android.
		}
	}
}

private fun Intent.getUsbDeviceExtra(): UsbDevice? =
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
	} else {
		@Suppress("DEPRECATION")
		getParcelableExtra(UsbManager.EXTRA_DEVICE)
	}

object AndroidFirmwareFlasher : FirmwareFlasher {
	override suspend fun flash(
		portLocation: String,
		handler: FlashingHandler,
		parts: List<DownloadedFirmwarePart>,
		onProgress: (Int) -> Unit,
	) {
		val flasher = Flasher(
			object : FlasherSerialInterface {
				override fun openSerial(port: Any) = handler.openSerial(port)
				override fun closeSerial() = handler.closeSerial()
				override fun write(data: ByteArray) = handler.write(data)
				override fun read(length: Int) = handler.read(length)
				override fun setDTR(value: Boolean) = handler.setDTR(value)
				override fun setRTS(value: Boolean) = handler.setRTS(value)
				override fun changeBaud(baud: Int) = handler.changeBaud(baud)
				override fun setReadTimeout(timeout: Long) = handler.setReadTimeout(timeout)
				override fun availableBytes() = handler.availableBytes()
				override fun flushIOBuffers() = handler.flushIOBuffers()
			},
		)
		for (part in parts) {
			flasher.addBin(part.data, part.offset)
		}
		flasher.addProgressListener(
			object : FlashingProgressListener {
				override fun progress(progress: Float) {
					onProgress((progress * 100).toInt())
				}
			},
		)
		withContext(Dispatchers.IO) {
			runCatching {
				flasher.flash(portLocation)
			}.onFailure { e ->
				AppLogger.firmware.error(e, "Android serial flash failed on $portLocation")
			}.getOrThrow()
		}
	}
}
