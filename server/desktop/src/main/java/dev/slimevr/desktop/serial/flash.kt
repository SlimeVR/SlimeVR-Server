package dev.slimevr.desktop.serial

import dev.llelievr.espflashkotlin.Flasher
import dev.llelievr.espflashkotlin.FlasherSerialInterface
import dev.llelievr.espflashkotlin.FlashingProgressListener
import dev.slimevr.firmware.DownloadedFirmwarePart
import dev.slimevr.firmware.FirmwareFlasher
import dev.slimevr.serial.FlashingHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.fazecast.jSerialComm.SerialPort as JSerialPort

class DesktopFlashingHandler : FlashingHandler {
	private var port: JSerialPort? = null

	override fun openSerial(portObj: Any) {
		val portLocation = portObj as? String ?: error("expected port location string")
		val comPort = JSerialPort.getCommPorts().find { it.portLocation == portLocation }
			?: error("Unable to find port $portLocation")
		if (comPort.isOpen) {
			comPort.closePort()
		}
		if (!comPort.openPort(1000)) {
			error("Unable to open port $portLocation")
		}
		this.port = comPort
	}

	override fun closeSerial() {
		port?.closePort()
		port = null
	}

	override fun setDTR(value: Boolean) {
		val p = port ?: error("no port to set DTR")
		if (value) p.setDTR() else p.clearDTR()
	}

	override fun setRTS(value: Boolean) {
		val p = port ?: error("no port to set RTS")
		if (value) p.setRTS() else p.clearRTS()
	}

	override fun write(data: ByteArray) {
		val p = port ?: error("no port to write")
		p.writeBytes(data, data.size)
	}

	override fun read(length: Int): ByteArray {
		val p = port ?: error("no port to read")
		val data = ByteArray(length)
		p.readBytes(data, length)
		return data
	}

	override fun changeBaud(baud: Int) {
		val p = port ?: error("no port to set baud")
		if (!p.setBaudRate(baud)) error("Unable to change baudrate to $baud")
	}

	override fun setReadTimeout(timeout: Long) {
		val p = port ?: error("no port to set timeout")
		p.setComPortTimeouts(JSerialPort.TIMEOUT_READ_BLOCKING, timeout.toInt(), 0)
	}

	override fun availableBytes(): Int {
		val p = port ?: error("no port to check available bytes")
		return p.bytesAvailable()
	}

	override fun flushIOBuffers() {
		val p = port ?: error("no port to flush")
		p.flushIOBuffers()
	}
}

object DesktopFirmwareFlasher : FirmwareFlasher {
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
