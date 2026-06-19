package dev.slimevr.android.serial

import android.hardware.usb.UsbManager
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import dev.llelievr.espflashkotlin.Flasher
import dev.llelievr.espflashkotlin.FlasherSerialInterface
import dev.llelievr.espflashkotlin.FlashingProgressListener
import dev.slimevr.firmware.DownloadedFirmwarePart
import dev.slimevr.firmware.FirmwareFlasher
import dev.slimevr.serial.FlashingHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidFlashingHandler(private val usbManager: UsbManager) : FlashingHandler {
	private var port: UsbSerialPort? = null
	private var readTimeout: Int = 1000
	private val pendingBytes = ArrayDeque<Byte>()

	override fun openSerial(portObj: Any) {
		val portLocation = portObj as? String ?: error("expected port location string")
		val driver = UsbSerialProber.getDefaultProber()
			.findAllDrivers(usbManager)
			.find { driver -> driver.device.deviceName == portLocation }
			?: error("Unable to find serial port $portLocation")

		val connection = usbManager.openDevice(driver.device)
			?: error("Unable to open USB device for $portLocation")

		val usbPort = driver.ports[0]
		usbPort.open(connection)
		usbPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
		this.port = usbPort
	}

	override fun closeSerial() {
		port?.close()
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

		while (offset < length && pendingBytes.isNotEmpty()) {
			result[offset++] = pendingBytes.removeFirst()
		}

		if (offset < length) {
			val remaining = ByteArray(length - offset)
			val read = p.read(remaining, readTimeout)
			remaining.copyInto(result, offset, 0, read)
			offset += read
		}

		return result.copyOf(offset)
	}

	override fun setDTR(value: Boolean) {
		val p = port ?: error("no port to set DTR")
		p.setDTR(value)
	}

	override fun setRTS(value: Boolean) {
		val p = port ?: error("no port to set RTS")
		p.setRTS(value)
	}

	override fun changeBaud(baud: Int) {
		val p = port ?: error("no port to change baud")
		p.setParameters(baud, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
	}

	override fun setReadTimeout(timeout: Long) {
		readTimeout = timeout.toInt().coerceAtLeast(1)
	}

	override fun availableBytes(): Int {
		val p = port ?: return 0
		val peekBuffer = ByteArray(256)
		val count = try {
			p.read(peekBuffer, 0)
		} catch (_: Exception) {
			0
		}
		for (i in 0 until count) pendingBytes.addLast(peekBuffer[i])
		return pendingBytes.size
	}

	override fun flushIOBuffers() {
		val p = port ?: error("no port to flush")
		pendingBytes.clear()
		p.purgeHwBuffers(true, true)
	}
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
		withContext(Dispatchers.IO) { flasher.flash(portLocation) }
	}
}
