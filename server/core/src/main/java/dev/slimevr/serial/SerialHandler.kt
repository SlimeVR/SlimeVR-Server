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
	abstract fun wifiScanRequest()
	abstract fun closeSerial()
	abstract fun write(buff: ByteArray)
	abstract fun setWifi(ssid: String, passwd: String)
	abstract fun getCurrentPort(): SerialPort?

	companion object {
		val supportedSerial: Set<Pair<Int, Int>> = setOf(
			// / QinHeng
			// CH340
			Pair(0x1A86, 0x7522),
			Pair(0x1A86, 0x7523),
			// CH341
			Pair(0x1A86, 0x5523),
			// CH9102x
			Pair(0x1A86, 0x55D4),
			// / Silabs
			// CP210x
			Pair(0x10C4, 0xEA60),
			// / Espressif
			// ESP32-C3
			Pair(0x303A, 0x1001),
			// / FTDI
			// FT232BM/L/Q, FT245BM/L/Q
			// FT232RL/Q, FT245RL/Q
			// VNC1L with VDPS Firmware
			// VNC2 with FT232Slave
			Pair(0x0403, 0x6001),
		)
		fun isKnownBoard(port: SerialPort): Boolean =
			supportedSerial.contains(Pair(port.vendorId, port.productId))
	}
}

class SerialHandlerStub : SerialHandler() {
	override val isConnected: Boolean = false
	override val knownPorts: Stream<out SerialPort> = Stream.empty()

	override fun addListener(channel: SerialListener) {}

	override fun removeListener(channel: SerialListener) {}

	override fun openSerial(portLocation: String?, auto: Boolean): Boolean = false

	override fun rebootRequest() {}

	override fun factoryResetRequest() {}

	override fun infoRequest() {}

	override fun wifiScanRequest() {}

	override fun closeSerial() {}

	override fun write(buff: ByteArray) {}

	override fun setWifi(ssid: String, passwd: String) {}

	override fun getCurrentPort(): SerialPort? = null
}
