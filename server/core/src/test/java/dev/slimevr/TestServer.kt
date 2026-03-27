package dev.slimevr

import dev.llelievr.espflashkotlin.FlasherSerialInterface
import dev.slimevr.firmware.createFirmwareManager
import dev.slimevr.serial.SerialPortHandle
import dev.slimevr.serial.SerialServer
import dev.slimevr.vrchat.createVRCConfigManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.emptyFlow

fun buildTestSerialServer(scope: CoroutineScope) = SerialServer.create(
	openPort = { loc, _, _, _ -> SerialPortHandle(loc, "Fake $loc", {}, {}) },
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

fun buildTestVrServer(scope: CoroutineScope): VRServer {
	val serialServer = buildTestSerialServer(scope)
	return VRServer.create(scope, serialServer, createFirmwareManager(serialServer, scope),
		createVRCConfigManager(
			scope = scope,
			userHeight = { 1.6 },
			isSupported = false,
			values = emptyFlow(),
		)
	)
}
