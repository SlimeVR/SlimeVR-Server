package dev.slimevr

import dev.slimevr.bvh.BVHManager
import dev.slimevr.config.AppConfig
import dev.slimevr.config.ConfigStorage
import dev.slimevr.config.DefaultUserBehaviour
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.SettingsConfigState
import dev.slimevr.config.SettingsState
import dev.slimevr.config.TextFileHandle
import dev.slimevr.config.UserConfig
import dev.slimevr.config.UserConfigData
import dev.slimevr.config.UserConfigState
import dev.slimevr.context.Context
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.firmware.FirmwareManager
import dev.slimevr.heightcalibration.HeightCalibrationActions
import dev.slimevr.heightcalibration.HeightCalibrationManager
import dev.slimevr.heightcalibration.HeightCalibrationState
import dev.slimevr.keybind.KeybindManager
import dev.slimevr.math.angle.Angle
import dev.slimevr.math.angle.AngleErrors
import dev.slimevr.networkprofile.NetworkProfileManager
import dev.slimevr.outputtrackertoggle.OutputTrackerToggleManager
import dev.slimevr.provisioning.ProvisioningManager
import dev.slimevr.resets.ResetsBasicBehaviour
import dev.slimevr.resets.ResetsManager
import dev.slimevr.resets.ResetsMountingTimeoutBehaviour
import dev.slimevr.resets.ResetsState
import dev.slimevr.serial.FlashingHandler
import dev.slimevr.serial.SerialPortHandle
import dev.slimevr.serial.SerialServer
import dev.slimevr.skeleton.DEFAULT_SKELETON_STATE
import dev.slimevr.skeleton.ProportionsBehaviour
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.buildBones
import dev.slimevr.stayaligned.StayAlignedActions
import dev.slimevr.stayaligned.StayAlignedManager
import dev.slimevr.stayaligned.StayAlignedState
import dev.slimevr.tapdetection.TapDetectionManager
import dev.slimevr.tracker.Motion
import dev.slimevr.tracker.SessionCalibration
import dev.slimevr.tracker.StayAlignedData
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerBasicBehaviour
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.tracker.TrackerState
import dev.slimevr.trackingchecklist.TrackingChecklist
import dev.slimevr.udp.UdpServer
import dev.slimevr.vmc.VMCManager
import dev.slimevr.vrchat.VRCConfigManager
import dev.slimevr.vrcosc.VRCOSCManager
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.ImuType
import solarxr_protocol.rpc.UserHeightCalibrationStatus

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
		behaviours = listOf(DefaultUserBehaviour()),
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
		behaviours = listOf(ProportionsBehaviour(buildTestUserConfig(scope))),
		name = "TestSkeleton",
	)
	val skeleton = Skeleton(context, MutableStateFlow(buildBones(context.state.value)))
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
		behaviours = listOf(ResetsBasicBehaviour(), ResetsMountingTimeoutBehaviour()),
		name = "TestResetsManager",
	)
	val resetsManager = ResetsManager(context, server, settings)
	resetsManager.startObserving()
	return resetsManager
}

fun buildTestTracker(
	scope: CoroutineScope,
	appContext: AppContextProvider,
	settings: Settings,
	id: Int,
	bodyPart: BodyPart? = null,
	status: TrackerStatus = TrackerStatus.DISCONNECTED,
	origin: DeviceOrigin = DeviceOrigin.UDP,
	sensorType: ImuType? = ImuType.BNO085,
	position: Vector3? = null,
	completedRestCalibration: Boolean? = true,
	rawRotation: Quaternion = Quaternion.IDENTITY,
	additionalBehaviours: List<TrackerBehaviour> = listOf(),
	sessionCalibration: SessionCalibration? = null,
): Tracker {
	val state = TrackerState(
		id = id,
		hardwareId = "test-$id",
		name = "Tracker $id",
		restOrientation = Quaternion.IDENTITY,
		rawRotation = rawRotation,
		rotation = Quaternion.IDENTITY,
		rawAcceleration = Vector3.NULL,
		acceleration = Vector3.NULL,
		rawMagnetometer = Vector3.NULL,
		bodyPart = bodyPart,
		mountingOrientation = Quaternion.IDENTITY,
		origin = origin,
		deviceId = 0,
		customName = null,
		imuType = sensorType,
		position = position,
		tps = 0u,
		imuTemp = null,
		status = status,
		completedRestCalibration = completedRestCalibration,
		magStatus = MagnetometerStatus.NOT_SUPPORTED,
		sessionCalibration = sessionCalibration,
		motion = Motion.ROTATING,
		yawResetSmoothing = null,
		stayAlignedData = StayAlignedData(Quaternion.IDENTITY, null, Angle.ZERO),
	)
	val context = Context.create(
		initialState = state,
		scope = scope,
		behaviours = listOf(TrackerBasicBehaviour(buildTestStayAlignedManager(appContext.server, scope))) + additionalBehaviours,
		name = "TestTracker[$id]",
	)
	return Tracker(context, appContext, settings)
}

fun buildTestSettings(scope: CoroutineScope): Settings {
	val initialState = SettingsState(data = SettingsConfigState(), name = "test")
	val context = Context.create<SettingsState, SettingsActions>(
		initialState = initialState,
		scope = scope,
		behaviours = emptyList(),
		name = "Settings[test]",
	)
	return Settings(context, scope, NoopConfigStorage, "settings")
}

fun buildTestHeightCalibration(server: VRServer, userConfig: UserConfig, scope: CoroutineScope): HeightCalibrationManager {
	val initialState = HeightCalibrationState(status = UserHeightCalibrationStatus.NONE, currentHeight = 1.6f, canDoUserHeightCalibration = true)
	val context = Context.create<HeightCalibrationState, HeightCalibrationActions>(
		initialState = initialState,
		scope = scope,
		behaviours = emptyList(),
		name = "HeightCalibration[test]",
	)
	return HeightCalibrationManager(context, server, userConfig)
}

fun buildTestStayAlignedManager(server: VRServer, scope: CoroutineScope): StayAlignedManager {
	val initialState = StayAlignedState(hideYawCorrection = false)
	val context = Context.create<StayAlignedState, StayAlignedActions>(
		initialState = initialState,
		scope = scope,
		behaviours = emptyList(),
		name = "StayAligned[test]",
	)
	return StayAlignedManager(context, server, buildTestSkeleton(scope), buildTestSettings(scope))
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

abstract class TestAppContext : AppContextProvider {
	override val featureFlags: FeatureFlags = FeatureFlags()
	override val keybindManager: KeybindManager get() = error("not used in test")
	override val skeleton: Skeleton get() = error("not used in test")
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
	override val resetsManager: ResetsManager get() = error("not used in test")
	override val tapDetectionManager: TapDetectionManager get() = error("not used in test")
	override val outputTrackerToggle: OutputTrackerToggleManager get() = error("not used in test")
	override val stayAlignedManager: StayAlignedManager get() = error("not used in test")
	override fun startObserving() {}
	override suspend fun dispose() = Unit
}
