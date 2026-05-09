package dev.slimevr

import dev.llelievr.espflashkotlin.FlasherSerialInterface
import dev.slimevr.bvh.BVHManager
import dev.slimevr.config.AppConfig
import dev.slimevr.config.DefaultUserBehaviour
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsConfigState
import dev.slimevr.config.SettingsState
import dev.slimevr.config.UserConfig
import dev.slimevr.config.UserConfigData
import dev.slimevr.config.UserConfigState
import dev.slimevr.context.Context
import dev.slimevr.firmware.FirmwareManager
import dev.slimevr.heightcalibration.HeightCalibrationManager
import dev.slimevr.networkprofile.NetworkProfileManager
import dev.slimevr.provisioning.ProvisioningManager
import dev.slimevr.serial.SerialPortHandle
import dev.slimevr.serial.SerialServer
import dev.slimevr.skeleton.DEFAULT_SKELETON_STATE
import dev.slimevr.skeleton.ProportionsBehaviour
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.buildBones
import dev.slimevr.trackingchecklist.TrackingChecklist
import dev.slimevr.udp.UdpServer
import dev.slimevr.vmc.VMCManager
import dev.slimevr.vrchat.VRCConfigManager
import dev.slimevr.vrcosc.VRCOSCManager
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

fun buildTestVrServerStub(scope: CoroutineScope): VRServer {
	val vrServer = VRServer.create(scope)
	// Don't call startObserving() to avoid starting infinite flow observers
	return vrServer
}

fun buildTestUserConfig(scope: CoroutineScope): UserConfig {
	val tempDir = Files.createTempDirectory("slimevr-test").toFile()
	tempDir.deleteOnExit()
	val context = Context.create(
		initialState = UserConfigState(data = UserConfigData(), name = "test"),
		scope = scope,
		behaviours = listOf(DefaultUserBehaviour),
		name = "TestUserConfig",
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
		name = "TestSkeleton",
	)
	val skeleton = Skeleton(context, MutableStateFlow(buildBones(context.state.value)))
	skeleton.startObserving()
	return skeleton
}

fun buildTestSettings(scope: CoroutineScope): Settings {
	val initialState = SettingsState(data = SettingsConfigState(), name = "test")
	val context = Context.create<SettingsState, dev.slimevr.config.SettingsActions>(
		initialState = initialState,
		scope = scope,
		behaviours = emptyList(),
		name = "Settings[test]",
	)
	// Use a path in /tmp that won't actually be written to in tests
	val fakeDir = java.io.File("/tmp/slimevr-test-${System.nanoTime()}")
	return Settings(context, scope, fakeDir)
}

abstract class TestAppContext : AppContextProvider {
	override val config: AppConfig get() = error("not used in test")
	override val serialServer: SerialServer get() = error("not used in test")
	override val firmwareManager: FirmwareManager get() = error("not used in test")
	override val vrcConfigManager: VRCConfigManager? = null
	override val provisioningManager: ProvisioningManager get() = error("not used in test")
	override val heightCalibrationManager: HeightCalibrationManager get() = error("not used in test")
	override val trackingChecklist: TrackingChecklist get() = error("not used in test")
	override val udpServer: UdpServer get() = error("not used in test")
	override val networkProfileManager: NetworkProfileManager? = null
	override val bvhManager: BVHManager get() = error("not used in test")
	override val vmcManager: VMCManager get() = error("not used in test")
	override val vrcOscManager: VRCOSCManager get() = error("not used in test")
	override fun startObserving() {}
}
