package dev.slimevr

import dev.llelievr.espflashkotlin.FlasherSerialInterface
import dev.slimevr.serial.SerialPortHandle
import dev.slimevr.serial.SerialServer
import kotlinx.coroutines.CoroutineScope

fun buildTestSerialServer(scope: CoroutineScope) = SerialServer.create(
	openPort = { loc, _, _ -> SerialPortHandle(loc, "Fake $loc", {}, {}) },
	openFlashingPort = {
		object : FlasherSerialInterface {
			override fun openSerial(port: Any) = Unit
			override fun closeSerial() = Unit
			override fun write(data: ByteArray) = Unit
			override fun read(length: Int) = ByteArray(length)
			override fun setDTR(value: Boolean) = Unit
			override fun setRTS(value: Boolean) = Unit
			override fun changeBaud(baud: Int) = Unit
			override fun setReadTimeout(timeout: Long) = Unit
			override fun availableBytes() = 0
			override fun flushIOBuffers() = Unit
		}
	},
	scope = scope,
)

fun buildTestVrServer(scope: CoroutineScope): VRServer = VRServer.create(scope)