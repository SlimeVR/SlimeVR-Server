package dev.slimevr.serial

import java.util.stream.Stream

abstract class SerialHandler {
	abstract val isConnected: Boolean
	abstract val knownPorts: Stream<out SerialPort>

	abstract fun addListener(channel: SerialListener)
	abstract fun removeListener(channel: SerialListener)

	abstract fun openSerial(portLocation: String?, auto: Boolean): Boolean
	abstract fun rebootRequest()
	abstract fun factoryResetRequest()
	abstract fun infoRequest()
	abstract fun closeSerial()
	abstract fun setWifi(ssid: String, passwd: String)
}

class SerialHandlerStub() : SerialHandler() {
	override val isConnected: Boolean = false
	override val knownPorts: Stream<out SerialPort> = Stream.empty()

	override fun addListener(channel: SerialListener) {}

	override fun removeListener(channel: SerialListener) {}

	override fun openSerial(portLocation: String?, auto: Boolean): Boolean {
		return false
	}

	override fun rebootRequest() {}

	override fun factoryResetRequest() {}

	override fun infoRequest() {}

	override fun closeSerial() {}

	override fun setWifi(ssid: String, passwd: String) {}
}
