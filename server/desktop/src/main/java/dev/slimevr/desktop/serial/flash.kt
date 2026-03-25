package dev.slimevr.desktop.serial

import dev.slimevr.serial.FlashingHandler
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
