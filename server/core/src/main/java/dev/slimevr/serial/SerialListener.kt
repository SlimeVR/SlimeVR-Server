package dev.slimevr.serial

abstract class SerialPort {
	abstract val portLocation: String
	abstract val descriptivePortName: String

	override fun equals(other: Any?): Boolean {
		val other: SerialPort = other as? SerialPort ?: return super.equals(other)

		return this.portLocation == other.portLocation &&
			this.descriptivePortName == other.descriptivePortName
	}
}

interface SerialListener {
	fun onSerialConnected(port: SerialPort)
	fun onSerialDisconnected()
	fun onSerialLog(str: String)
	fun onNewSerialDevice(port: SerialPort)
}
