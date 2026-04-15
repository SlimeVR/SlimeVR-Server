package dev.slimevr

import dev.llelievr.espflashkotlin.FlasherSerialInterface
import dev.slimevr.config.AppConfig
import dev.slimevr.config.DefaultUserBehaviour
import dev.slimevr.config.UserConfig
import dev.slimevr.config.UserConfigData
import dev.slimevr.config.UserConfigState
import dev.slimevr.context.Context
import dev.slimevr.firmware.FirmwareManager
import dev.slimevr.heightcalibration.HeightCalibrationManager
import dev.slimevr.provisioning.ProvisioningManager
import dev.slimevr.serial.SerialPortHandle
import dev.slimevr.serial.SerialServer
import dev.slimevr.skeleton.DEFAULT_SKELETON_STATE
import dev.slimevr.skeleton.ProportionsBehaviour
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.buildBones
import dev.slimevr.trackingchecklist.TrackingChecklist
import dev.slimevr.vrchat.VRCConfigManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
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
	val context = Context.create(
		initialState = UserConfigState(data = UserConfigData(), name = "test"),
		scope = scope,
		behaviours = listOf(DefaultUserBehaviour),
	)
	val userConfig = UserConfig(context, scope = scope, userConfigDir = tempDir)
	context.observeAll(userConfig)
	return userConfig
}

fun buildTestSkeleton(scope: CoroutineScope): Skeleton {
	val context = Context.create(
		initialState = DEFAULT_SKELETON_STATE,
		scope = scope,
		behaviours = listOf(ProportionsBehaviour()),
	)
	val skeleton = Skeleton(context, MutableStateFlow(buildBones(context.state.value)))
	skeleton.startObserving()
	return skeleton
}

abstract class TestAppContext : AppContextProvider {
	override val config: AppConfig get() = error("not used in test")
	override val serialServer: SerialServer get() = error("not used in test")
	override val firmwareManager: FirmwareManager get() = error("not used in test")
	override val vrcConfigManager: VRCConfigManager? = null
	override val provisioningManager: ProvisioningManager get() = error("not used in test")
	override val heightCalibrationManager: HeightCalibrationManager get() = error("not used in test")
	override val trackingChecklist: TrackingChecklist get() = error("not used in test")
	override fun startObserving() {}
}
