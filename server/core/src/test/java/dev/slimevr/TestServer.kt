package dev.slimevr

import dev.llelievr.espflashkotlin.FlasherSerialInterface
import dev.slimevr.config.DefaultUserBehaviour
import dev.slimevr.config.UserConfig
import dev.slimevr.config.UserConfigData
import dev.slimevr.config.UserConfigState
import dev.slimevr.context.Context
import dev.slimevr.serial.SerialPortHandle
import dev.slimevr.serial.SerialServer
import kotlinx.coroutines.CoroutineScope
import java.nio.file.Files

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

fun buildTestUserConfig(scope: CoroutineScope): UserConfig {
	val tempDir = Files.createTempDirectory("slimevr-test").toFile()
	tempDir.deleteOnExit()
	val behaviours = listOf(DefaultUserBehaviour)
	val context = Context.create(
		initialState = UserConfigState(data = UserConfigData(), name = "test"),
		scope = scope,
		behaviours = behaviours,
	)
	val userConfig = UserConfig(context, scope = scope, userConfigDir = tempDir)
	behaviours.forEach { it.observe(userConfig) }
	return userConfig
}
