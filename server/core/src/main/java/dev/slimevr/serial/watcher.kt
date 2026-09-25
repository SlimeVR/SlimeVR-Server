package dev.slimevr.serial

import kotlinx.coroutines.flow.Flow
import solarxr_protocol.rpc.SerialDevice

interface FlashingHandler {
	fun openSerial(port: Any)
	fun closeSerial()
	fun write(data: ByteArray)
	fun read(length: Int): ByteArray
	fun setDTR(value: Boolean)
	fun setRTS(value: Boolean)
	fun changeBaud(baud: Int)
	fun setReadTimeout(timeout: Long)
	fun availableBytes(): Int
	fun flushIOBuffers()
}

data class SerialPortHandle(
	val portLocation: String,
	val descriptivePortName: String,
	val writeCommand: suspend (String) -> Unit,
	val close: suspend () -> Unit,
)

data class SerialPortInfo(
	val portLocation: String,
	val descriptivePortName: String,
	val vendorId: Int,
	val productId: Int,
	val serialNumber: String? = null,
) {
	val type get() = classifyPort(vendorId, productId)

	fun toSerialDevice() = SerialDevice(
		port = portLocation,
		name = descriptivePortName,
		type = type,
		vendorId = vendorId.toUShort(),
		productId = productId.toUShort(),
		serialNumber = serialNumber,
	)
}

/** Platform side of the serial server: what is plugged in, when that changes, and how to open it */
interface SerialPortWatcher {
	/** USB serial ports present right now, keyed by port location */
	suspend fun enumerate(): Map<String, SerialPortInfo>

	/**
	 * Emits when the OS reports a serial port arriving or leaving. If it fails the error is logged and
	 * the port list stops updating
	 */
	val changes: Flow<Unit>

	/**
	 * Opens a console on the port. [onLine] receives each received line without blocking. [onClosed] runs
	 * when the port goes away or fails, after which the handle is dead
	 */
	suspend fun open(portLocation: String, onLine: (String) -> Unit, onClosed: () -> Unit): SerialPortHandle?

	fun openForFlashing(): FlashingHandler
}
