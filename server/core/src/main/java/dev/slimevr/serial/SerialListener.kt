package dev.slimevr.serial

import java.util.*

abstract class SerialPort {
	abstract val portLocation: String
	abstract val descriptivePortName: String

	override fun equals(other: Any?): Boolean {
		val other: SerialPort = other as? SerialPort ?: return super.equals(other)

		return this.portLocation == other.portLocation &&
			this.descriptivePortName == other.descriptivePortName
	}

	override fun hashCode(): Int {
		return Objects.hash(portLocation, descriptivePortName)
	}
}

interface SerialListener {
	fun onSerialConnected(port: SerialPort)
	fun onSerialDisconnected()
	fun onSerialLog(str: String)
	fun onNewSerialDevice(port: SerialPort)
}
