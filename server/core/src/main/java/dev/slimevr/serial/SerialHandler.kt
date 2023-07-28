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

	companion object {
		val supportedSerial: Set<Pair<Int, Int>> = setOf(
			// CH340
			Pair(0x1A86, 0x7522),
			Pair(0x1A86, 0x7523),
			// CH341
			Pair(0x1A86, 0x5523),
			// CH9102
			Pair(0x1A86, 0x55D4),
			// CP210x
			Pair(0x10C4, 0xEA60)
		)
		fun isKnownBoard(port: SerialPort): Boolean =
			supportedSerial.contains(Pair(port.vendorId, port.productId))
	}
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
