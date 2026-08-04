package dev.slimevr.config

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ArmsResetMode
import solarxr_protocol.rpc.FilteringType
import solarxr_protocol.rpc.KeybindId
import solarxr_protocol.rpc.RoutingOutput

private const val SETTINGS_CONFIG_VERSION = 2

@Serializable
data class TrackerConfig(
	@Serializable(with = BodyPartSerializer::class)
	val bodyPart: BodyPart? = null,
	val customName: String? = null,
	@Serializable(with = QuaternionSerializer::class)
	val mountingOrientation: Quaternion = Quaternion.IDENTITY,
	@Serializable(with = QuaternionSerializer::class)
	val mountingResetOrientation: Quaternion? = null,
	val magEnabled: Boolean? = null,
)

@Serializable
data class TrackersConfig(
	val trackerPort: Int = 6969, // Not in SolarXR
	val globalMagEnabled: Boolean = false,
)

@Serializable
data class HidConfig(
	val trackersOverHid: Boolean = false, // TODO
)

@Serializable
data class BoneRoutingConfig(
	/** Generate the routes from the connected trackers and the output priority. */
	val automatic: Boolean = true,
	/**
	 * Explicit routes, ignored while [automatic]. Do not read directly, use BoneRoutingManager.
	 */
	val manualRoutes: Map<BodyPart, Set<RoutingOutput>>? = null,
)

@Serializable
data class DriverConfig(
	val sendDerivedVelocity: Boolean = false, // TODO do we actually need that or can we disable OpenVR's prediction
)

@Serializable
data class TapDetectionConfig(
	var yawResetDelay: Float = 0.2f,
	var fullResetDelay: Float = 1.0f,
	var mountingResetDelay: Float = 1.0f,
	var yawResetEnabled: Boolean = true,
	var fullResetEnabled: Boolean = true,
	var mountingResetEnabled: Boolean = true,
	@Serializable(with = BodyPartSerializer::class)
	var yawResetBodyPart: BodyPart? = BodyPart.CHEST,
	@Serializable(with = BodyPartSerializer::class)
	var mountingResetBodyPart: BodyPart? = BodyPart.RIGHT_UPPER_LEG,
	@Serializable(with = BodyPartSerializer::class)
	var fullResetBodyPart: BodyPart? = BodyPart.LEFT_UPPER_LEG,
	var yawResetTaps: Int = 2,
	var fullResetTaps: Int = 3,
	var mountingResetTaps: Int = 3,
	var numberTrackersOverThreshold: Int = 1,
)

enum class MountingMethods(val id: Int) {
	MANUAL(0),
	AUTOMATIC(1),
}

@Serializable
data class ResetsConfig(
	/** Always reset mounting for feet */
	val resetMountingFeet: Boolean = false,
	/** Reset mode used for the arms */
	val armsResetMode: ArmsResetMode = ArmsResetMode.BACK,
	/** Yaw reset smoothing time in seconds */
	val yawResetSmoothTime: Float = 0.0f,
	/** Save automatic mounting reset calibration */
	val saveMountingReset: Boolean = false,
	/** Reset the HMD's pitch upon full reset */
	val resetHmdPitch: Boolean = false, // TODO
	val lastMountingMethod: MountingMethods = MountingMethods.AUTOMATIC,
)

@Serializable
data class KeybindConfig(
	@Serializable(with = KeybindIdSerializer::class)
	val id: KeybindId,
	/** Key combination, keys joined with '+' (e.g. "CTRL+ALT+SHIFT+Y") */
	val binding: String,
	/** Delay in seconds before the keybind triggers */
	val delay: Float = 0f,
)

fun defaultKeybinds(): List<KeybindConfig> = listOf(
	KeybindConfig(KeybindId.FULL_RESET, "CTRL+ALT+SHIFT+Y"),
	KeybindConfig(KeybindId.YAW_RESET, "CTRL+ALT+SHIFT+U"),
	KeybindConfig(KeybindId.MOUNTING_RESET, "CTRL+ALT+SHIFT+I"),
	KeybindConfig(KeybindId.FEET_MOUNTING_RESET, "CTRL+ALT+SHIFT+P"),
	KeybindConfig(KeybindId.PAUSE_TRACKING, "CTRL+ALT+SHIFT+O"),
)

// Used in SkeletonConfig
@Serializable
data class SkeletonTogglesConfig(
	val forceArmsFromHmd: Boolean = true, // TODO do we still need that with useTrackerPositions?
	val floorClip: Boolean = true,
	val skatingCorrection: Boolean = true,
	val toeSnap: Boolean = true,
	val footPlant: Boolean = true,
	val mocapMode: Boolean = false,
	val useTrackerPositions: Boolean = true,
	val enforceConstraints: Boolean = true,
	val correctConstraints: Boolean = true,
)

// Used in SkeletonConfig
@Serializable
data class SkeletonRatiosConfig(
	val imputeSpineFromUpperToLower: Float = 0.5f,
	val imputeSpineCurvature: Float = 0.5f,
	val interpolateHipWithUpperLegs: Float = 0.25f,
	val interpolateUpperLegsWithLowerLegs: Float = 0.8f,
	val skatingCorrectionStrength: Float = 0.3f,
)

// Used in SkeletonConfig
@Serializable
data class SkeletonFilteringConfig(
	val type: FilteringType = FilteringType.PREDICTION,
	val amount: Float = 0.2f,
)

@Serializable
data class SkeletonConfig(
	val toggles: SkeletonTogglesConfig = SkeletonTogglesConfig(),
	val ratios: SkeletonRatiosConfig = SkeletonRatiosConfig(),
	val filtering: SkeletonFilteringConfig = SkeletonFilteringConfig(),
)

const val DEFAULT_VRC_OSC_PORT_OUT: Int = 9000

@Serializable
data class VRCOSCConfig(
	val enabled: Boolean = false,
	val useManualNetwork: Boolean = false,
	val portIn: Int = 9001,
	val portOut: Int = DEFAULT_VRC_OSC_PORT_OUT,
	val address: String = "127.0.0.1",
)

@Serializable
data class VMCConfig(
	val enabled: Boolean = true,
	val portIn: Int = 39540,
	val portOut: Int = 39539,
	val address: String = "127.0.0.1",
	/** Mirror the tracking before sending it (turn left <=> turn right, left leg <=> right leg) */
	val mirrorTracking: Boolean = false,
	/** Anchor the tracking at the hip (sitting down)? */
	val anchorAtHips: Boolean = false,
	/** JSON part of the VRM to be used */
	val vrmJson: String? = null,
)

// Used in StayAlignedConfig
@Serializable
data class StayAlignedRelaxedPoseConfig(
	/** Whether Stay Aligned should adjust the tracker yaws when the player is in this pose. */
	var enabled: Boolean = false,
	/** Angle between the upper leg yaw and the center yaw. */
	var upperLegAngleInDeg: Float = 0.0f,
	/** Angle between the lower leg yaw and the center yaw. */
	var lowerLegAngleInDeg: Float = 0.0f,
	/** Angle between the foot and the center yaw. */
	var footAngleInDeg: Float = 0.0f,
)

@Serializable
data class StayAlignedConfig(
	/** Global enable toggle for all poses */
	var enabled: Boolean = false,
	/** Standing relaxed pose */
	val standingRelaxedPose: StayAlignedRelaxedPoseConfig = StayAlignedRelaxedPoseConfig(),
	/** Sitting relaxed pose */
	val sittingRelaxedPose: StayAlignedRelaxedPoseConfig = StayAlignedRelaxedPoseConfig(),
	/** Flat relaxed pose */
	val flatRelaxedPose: StayAlignedRelaxedPoseConfig = StayAlignedRelaxedPoseConfig(),
	/** Whether setup has been completed */
	var setupComplete: Boolean = false,
)

@Serializable
data class SettingsConfigState(
	val version: Int = SETTINGS_CONFIG_VERSION,
	val mutedVRCWarnings: List<String> = listOf(),
	val mutedChecklistSteps: Set<String> = emptySet(),
	val allowedUdpDevices: Set<String> = emptySet(),
	val trackers: Map<String, TrackerConfig> = emptyMap(),
	val trackersConfig: TrackersConfig = TrackersConfig(),
	val hidConfig: HidConfig = HidConfig(),
	val boneRoutingConfig: BoneRoutingConfig = BoneRoutingConfig(),
	val driverConfig: DriverConfig = DriverConfig(),
	val tapDetectionConfig: TapDetectionConfig = TapDetectionConfig(),
	val resetsConfig: ResetsConfig = ResetsConfig(),
	val keybinds: List<KeybindConfig> = defaultKeybinds(),
	val skeletonConfig: SkeletonConfig = SkeletonConfig(),
	val vrcOscConfig: VRCOSCConfig = VRCOSCConfig(),
	val vmcConfig: VMCConfig = VMCConfig(),
	val stayAlignedConfig: StayAlignedConfig = StayAlignedConfig(),
)

private fun migrateSettingsConfig(json: JsonObject): JsonObject {
	val version = json["version"]?.jsonPrimitive?.intOrNull ?: 0
	return when {
		else -> json
	}
}

private fun parseAndMigrateSettingsConfig(raw: String): SettingsConfigState {
	val json = jsonConfig.parseToJsonElement(raw).jsonObject
	return jsonConfig.decodeFromJsonElement(migrateSettingsConfig(json))
}

data class SettingsState(
	val data: SettingsConfigState,
	val name: String,
)

sealed interface SettingsActions {
	data class Update(val transform: SettingsConfigState.() -> SettingsConfigState) : SettingsActions
	data class LoadProfile(val newState: SettingsState) : SettingsActions
	data class UpdateTracker(val hardwareId: String, val transform: TrackerConfig.() -> TrackerConfig) : SettingsActions
	data class AddAllowedUdpDevice(val mac: String) : SettingsActions
	data class RemoveAllowedUdpDevice(val mac: String) : SettingsActions
}

typealias SettingsContext = Context<SettingsState, SettingsActions>
typealias SettingsBehaviour = Behaviour<SettingsState, SettingsActions, Settings>

class Settings(
	val context: SettingsContext,
	private val scope: CoroutineScope,
	private val storage: ConfigStorage,
	private val settingsDir: String,
) {
	fun startObserving() = context.observeAll(this)

	private var autosaveJob: Job = startAutosave()

	private fun startAutosave() = launchAutosave(
		scope = scope,
		state = context.state,
		storage = storage,
		toPath = { state -> configPath(settingsDir, "${state.name}.json") },
		serialize = { state -> jsonConfig.encodeToString(state.data) },
	)

	suspend fun swap(newName: String) {
		autosaveJob.cancelAndJoin()

		val newData = loadFileWithBackup(storage, configPath(settingsDir, "$newName.json"), SettingsConfigState()) {
			parseAndMigrateSettingsConfig(it)
		}
		val newState = SettingsState(name = newName, data = newData)
		context.dispatch(SettingsActions.LoadProfile(newState))

		autosaveJob = startAutosave()
	}

	companion object {
		suspend fun create(scope: CoroutineScope, storage: ConfigStorage, name: String): Settings {
			val settingsDir = "settings"

			val initialData = loadFileWithBackup(storage, configPath(settingsDir, "$name.json"), SettingsConfigState()) {
				parseAndMigrateSettingsConfig(it)
			}
			val initialState = SettingsState(name = name, data = initialData)

			val behaviours = listOf(DefaultSettingsBehaviour())
			val context = Context.create(
				initialState = initialState,
				scope = scope,
				behaviours = behaviours,
				name = "Settings[$name]",
			)
			val settings = Settings(context, scope = scope, storage = storage, settingsDir = settingsDir)
			settings.startObserving()
			return settings
		}
	}
}
