package dev.slimevr.desktop.firmware

import com.fazecast.jSerialComm.SerialPort
import dev.slimevr.firmware.SerialFlashingHandler
import io.eiren.util.logging.LogManager
import dev.slimevr.serial.SerialPort as SerialPortWrapper

class DesktopSerialFlashingHandler : SerialFlashingHandler {
	private var port: SerialPort? = null

	override fun openSerial(port: Any) {
		if (port !is SerialPortWrapper) {
			error("Not a serial port")
		}
		val ports = SerialPort.getCommPorts()
		val comPort = ports.find { it.portLocation == port.portLocation }
			?: error("Unable to find port ${port.portLocation}")
		if (comPort.isOpen) {
			comPort.closePort()
		}
		if (!comPort.openPort(1000)) {
			error("unable to open port")
		}
		this.port = comPort
	}

	override fun closeSerial() {
		val p = port ?: error("no port to close")
		try {
			p.closePort()
			LogManager.info("Port closed")
		} catch (e: Exception) {
			error("unable to close port")
		}
	}

	override fun setDTR(value: Boolean) {
		val p = port ?: error("no port to set DTR")
		if (value) {
			p.setDTR()
		} else {
			p.clearDTR()
		}
	}

	override fun setRTS(value: Boolean) {
		val p = port ?: error("no port to set RTS")
		if (value) {
			p.setRTS()
		} else {
			p.clearRTS()
		}
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
		val p = port ?: error("no port to set the baud")
		if (!p.setBaudRate(baud)) {
			error("Unable to change baudrate")
		}
	}

	override fun setReadTimeout(timeout: Long) {
		val p = port ?: error("no port to set the timeout")
		p.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, timeout.toInt(), 0)
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
