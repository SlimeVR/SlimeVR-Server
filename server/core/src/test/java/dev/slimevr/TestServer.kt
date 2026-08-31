@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

package dev.slimevr

import dev.slimevr.bvh.BVHManager
import dev.slimevr.config.AppConfig
import dev.slimevr.config.ConfigStorage
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsConfigState
import dev.slimevr.config.SettingsState
import dev.slimevr.config.TextFileHandle
import dev.slimevr.config.UserConfig
import dev.slimevr.config.UserConfigData
import dev.slimevr.config.UserConfigState
import dev.slimevr.context.Context
import dev.slimevr.firmware.FirmwareManager
import dev.slimevr.heightcalibration.HeightCalibrationActions
import dev.slimevr.heightcalibration.HeightCalibrationManager
import dev.slimevr.heightcalibration.HeightCalibrationState
import dev.slimevr.keybind.KeybindManager
import dev.slimevr.networkprofile.NetworkProfileManager
import dev.slimevr.provisioning.ProvisioningManager
import dev.slimevr.resets.ResetsManager
import dev.slimevr.resets.ResetsMountingTimeoutBehaviour
import dev.slimevr.resets.ResetsState
import dev.slimevr.routing.BoneRoutingManager
import dev.slimevr.serial.FlashingHandler
import dev.slimevr.serial.SerialPortHandle
import dev.slimevr.serial.SerialServer
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.DEFAULT_SKELETON_STATE
import dev.slimevr.skeleton.ProportionsBehaviour
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.buildBones
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeState
import dev.slimevr.solarxr.driver.DriverHandshakeBehaviour
import dev.slimevr.solarxr.rpc.ServerInfos
import dev.slimevr.tapdetection.TapDetectionManager
import dev.slimevr.tracker.Motion
import dev.slimevr.tracker.SessionCalibration
import dev.slimevr.tracker.StayAlignedData
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.tracker.behaviours.TrackerCalibrationRefreshBehaviour
import dev.slimevr.tracker.behaviours.TrackerTpsBehaviour
import dev.slimevr.trackingchecklist.TrackingChecklist
import dev.slimevr.vmc.VMCManager
import dev.slimevr.vrchat.VRCConfigManager
import dev.slimevr.vrcosc.VRCOSCManager
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.ImuType
import solarxr_protocol.rpc.UserHeightCalibrationStatus
import dev.slimevr.config.reduce as reduceConfig
import dev.slimevr.heightcalibration.reduce as reduceHeightCalibration
import dev.slimevr.resets.reduce as reduceResets
import dev.slimevr.skeleton.reduce as reduceSkeleton
import dev.slimevr.tracker.reduce as reduceTracker
import dev.slimevr.udp.reduce as reduceUdpServer
import dev.slimevr.solarxr.reduce as reduceSolarXR

fun buildTestSerialServer(scope: CoroutineScope) = SerialServer.create(
	openPort = { loc, _, _ -> SerialPortHandle(loc, "Fake $loc", {}, {}) },
	openFlashingPort = {
		object : FlashingHandler {
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
	val context = Context.create(
		initialState = UserConfigState(data = UserConfigData(), name = "test"),
		scope = scope,
		reducer = ::reduceConfig,
		name = "TestUserConfig",
	)
	val userConfig = UserConfig(context, scope = scope, storage = NoopConfigStorage, userConfigDir = "user")
	context.observeAll(userConfig)
	return userConfig
}

fun buildTestSkeleton(scope: CoroutineScope): Skeleton {
	val context = Context.create(
		initialState = DEFAULT_SKELETON_STATE,
		scope = scope,
		reducer = ::reduceSkeleton,
		behaviours = listOf(ProportionsBehaviour(buildTestUserConfig(scope))),
		name = "TestSkeleton",
	)
	val computed = MutableSharedFlow<ComputedSkeleton>(
		replay = 1,
		onBufferOverflow = BufferOverflow.DROP_OLDEST,
	)
	computed.tryEmit(buildBones(context.state.value.boneInputs))
	val skeleton = Skeleton(context, buildTestSettings(scope), computed)
	skeleton.startObserving()
	return skeleton
}

fun buildTestResetsManager(server: VRServer, settings: Settings, scope: CoroutineScope): ResetsManager {
	val context = Context.create(
		initialState = ResetsState(
			canDoYawReset = true,
			canDoMountingReset = true,
			lastFullResetTime = null,
		),
		scope = scope,
		reducer = ::reduceResets,
		behaviours = listOf(ResetsMountingTimeoutBehaviour()),
		name = "TestResetsManager",
	)
	val resetsManager = ResetsManager(context, server, settings, buildTestSkeleton(scope))
	resetsManager.startObserving()
	return resetsManager
}

fun buildTestTracker(
	scope: CoroutineScope,
	appContext: AppContextProvider,
	settings: Settings,
	id: Int,
	bodyPart: BodyPart? = null,
	intendedBodyPart: BodyPart? = null,
	status: TrackerStatus = TrackerStatus.DISCONNECTED,
	origin: DeviceOrigin = DeviceOrigin.UDP,
	imuType: ImuType? = ImuType.BNO085,
	position: Vector3? = null,
	completedRestCalibration: Boolean? = true,
	rawRotation: Quaternion = Quaternion.IDENTITY,
	additionalBehaviours: List<TrackerBehaviour> = listOf(),
	sessionCalibration: SessionCalibration = SessionCalibration(),
	motion: Motion = Tracker.DEFAULT_STATE.motion,
	stayAlignedData: StayAlignedData = Tracker.DEFAULT_STATE.stayAlignedData,
): Tracker {
	val state = Tracker.DEFAULT_STATE.copy(
		id = id,
		deviceId = 0,
		origin = origin,
		hardwareId = "test-$id",
		name = "Tracker $id",
		imuType = imuType,
		bodyPart = bodyPart,
		intendedBodyPart = intendedBodyPart,
		customName = null,
		sessionCalibration = sessionCalibration,
		rawRotation = rawRotation,
		position = position,
		status = status,
		completedRestCalibration = completedRestCalibration,
		motion = motion,
		stayAlignedData = stayAlignedData,
	)
	val context = Context.create(
		initialState = state,
		scope = scope,
		reducer = ::reduceTracker,
		behaviours = listOf(TrackerCalibrationRefreshBehaviour(), TrackerTpsBehaviour()) + additionalBehaviours,
		name = "TestTracker[$id]",
	)
	return Tracker(context, appContext, settings)
}

fun buildTestSettings(scope: CoroutineScope): Settings {
	val initialState = SettingsState(data = SettingsConfigState(), name = "test")
	val context = Context.create(
		initialState = initialState,
		scope = scope,
		reducer = ::reduceConfig,
		name = "Settings[test]",
	)
	return Settings(context, scope, NoopConfigStorage, "settings")
}

fun buildTestSolarXR(scope: CoroutineScope, appContext: AppContextProvider): SolarXRBridge {
	val context = Context.create(
		initialState = SolarXRBridgeState(),
		scope = scope,
		reducer = ::reduceSolarXR,
		behaviours = listOf(DriverHandshakeBehaviour(appContext)),
		name = "SolarXR[test]",
	)
	return SolarXRBridge(id = 0, context = context, appContext = appContext)
}

fun buildTestHeightCalibration(server: VRServer, userConfig: UserConfig, scope: CoroutineScope): HeightCalibrationManager {
	val initialState = HeightCalibrationState(status = UserHeightCalibrationStatus.NONE, currentHeight = 1.6f, canDoUserHeightCalibration = true)
	val context = Context.create<HeightCalibrationState, HeightCalibrationActions>(
		initialState = initialState,
		scope = scope,
		reducer = ::reduceHeightCalibration,
		behaviours = emptyList(),
		name = "HeightCalibration[test]",
	)
	return HeightCalibrationManager(context, server, userConfig)
}

private object NoopConfigStorage : ConfigStorage {
	override suspend fun read(path: String): String? = null
	override suspend fun write(path: String, content: String) = Unit
	override suspend fun backup(path: String) = Unit
	override suspend fun exists(path: String): Boolean = false
	override suspend fun ensureDirectory(path: String): Boolean = true
	override suspend fun openTextFile(path: String): TextFileHandle = error("Not used in tests")
}

fun buildTestAppContext(server: VRServer): AppContextProvider = object : TestAppContext() {
	override val server: VRServer = server
}

private fun buildTestUdpServer(): dev.slimevr.udp.UdpServer {
	val context = Context.create(
		initialState = dev.slimevr.udp.UdpServerState(emptyMap()),
		scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Job()),
		reducer = ::reduceUdpServer,
		name = "TestUdpServer",
	)
	return dev.slimevr.udp.UdpServer(context) { "127.0.0.1" }.also { it.startObserving() }
}

class FakeDatagramSocket : io.ktor.network.sockets.BoundDatagramSocket {
	override val localAddress: io.ktor.network.sockets.InetSocketAddress = io.ktor.network.sockets.InetSocketAddress("127.0.0.1", 6969)
	override val socketContext: kotlinx.coroutines.Job = kotlinx.coroutines.Job()
	override val incoming: kotlinx.coroutines.channels.ReceiveChannel<io.ktor.network.sockets.Datagram> = kotlinx.coroutines.channels.Channel()
	override val outgoing: kotlinx.coroutines.channels.SendChannel<io.ktor.network.sockets.Datagram> = kotlinx.coroutines.channels.Channel()
	override fun close() {}
	override suspend fun receive(): io.ktor.network.sockets.Datagram = error("not used")
	override suspend fun send(datagram: io.ktor.network.sockets.Datagram) {}
}

abstract class TestAppContext : AppContextProvider {
	open override val server: VRServer get() = error("not used in test")
	override val featureFlags: FeatureFlags = FeatureFlags()
	override val keybindManager: KeybindManager get() = error("not used in test")
	override val skeleton: Skeleton get() = error("not used in test")
	override val config: AppConfig get() = error("not used in test")
	override val serialServer: SerialServer get() = error("not used in test")
	override val serverInfos: ServerInfos get() = error("not used in test")
	override val firmwareManager: FirmwareManager get() = error("not used in test")
	override val vrcConfigManager: VRCConfigManager? = null
	override val provisioningManager: ProvisioningManager get() = error("not used in test")
	override val heightCalibrationManager: HeightCalibrationManager get() = error("not used in test")
	override val trackingChecklist: TrackingChecklist get() = error("not used in test")
	override val udpServer: dev.slimevr.udp.UdpServer = buildTestUdpServer()
	override val networkProfileManager: NetworkProfileManager? = null
	override val bvhManager: BVHManager get() = error("not used in test")
	override val vmcManager: VMCManager get() = error("not used in test")
	override val vrcOscManager: VRCOSCManager get() = error("not used in test")
	override val resetsManager: ResetsManager get() = error("not used in test")
	override val tapDetectionManager: TapDetectionManager get() = error("not used in test")
	override val boneRouting: BoneRoutingManager get() = error("not used in test")
	override fun startObserving() {}
	override suspend fun dispose() = Unit
}
