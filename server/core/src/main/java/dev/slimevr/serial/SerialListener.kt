package dev.slimevr.serial

import java.util.*

abstract class SerialPort {
	abstract val portLocation: String
	abstract val descriptivePortName: String
	abstract val vendorId: Int?
	abstract val productId: Int?

	override fun equals(other: Any?): Boolean {
		val other: SerialPort = other as? SerialPort ?: return super.equals(other)

		return this.portLocation == other.portLocation &&
			this.descriptivePortName == other.descriptivePortName &&
			this.vendorId == other.vendorId &&
			this.productId == other.productId
	}

	override fun hashCode(): Int = Objects.hash(portLocation, descriptivePortName, vendorId, productId)
}

interface SerialListener {
	fun onSerialConnected(port: SerialPort)
	fun onSerialDisconnected()
	fun onSerialLog(str: String)
	fun onNewSerialDevice(port: SerialPort)

	// This is called when the serial diver does not see the device anymore
	fun onSerialDeviceDeleted(port: SerialPort)
}
