package dev.slimevr.config

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.skeleton.inputprocessors.DEFAULT_SPINE_UPPER_LOWER
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.MountingMethod
import solarxr_protocol.rpc.ArmsMountingResetMode
import solarxr_protocol.rpc.FilteringType
import solarxr_protocol.rpc.KeybindId
import solarxr_protocol.rpc.RoutingOutput

private const val SETTINGS_CONFIG_VERSION = 2

@Serializable
data class TrackersConfig(
	val trackerPort: Int = 6969, // Not in SolarXR
	val globalMagEnabled: Boolean = false,
	val timeoutDelay: Float = 20.0f,
)

@Serializable
data class HidConfig(
	val trackersOverHid: Boolean = false,
)

@Serializable
data class BoneRoutingConfig(
	/** Generate the routes from the connected trackers and the output priority. */
	val automatic: Boolean = true,
	/**
	 * Explicit routes. Ignored while [automatic], apart from the overridable bones, which
	 * the user owns in either mode. Do not read directly, use BoneRoutingManager.
	 */
	val manualRoutes: Map<BodyPart, Set<RoutingOutput>>? = null,
)

@Serializable
data class DriverConfig(
	val enabled: Boolean = true,
	val sendVelocity: Boolean = false,
)

@Serializable
data class TapDetectionConfig(
	var yawResetDelay: Float = 0.2f,
	var fullResetDelay: Float = 1.4f,
	var mountingResetDelay: Float = 1.4f,
	var yawResetEnabled: Boolean = true,
	var fullResetEnabled: Boolean = true,
	var mountingResetEnabled: Boolean = true,
	@Serializable(with = BodyPartSerializer::class)
	var yawResetBodyPart: BodyPart? = BodyPart.UPPER_CHEST,
	@Serializable(with = BodyPartSerializer::class)
	var mountingResetBodyPart: BodyPart? = BodyPart.RIGHT_UPPER_LEG,
	@Serializable(with = BodyPartSerializer::class)
	var fullResetBodyPart: BodyPart? = BodyPart.LEFT_UPPER_LEG,
	var yawResetTaps: Int = 2,
	var fullResetTaps: Int = 3,
	var mountingResetTaps: Int = 3,
	var numberTrackersOverThreshold: Int = 1,
)

@Serializable
data class ResetsConfig(
	/** Always reset mounting for feet */
	val resetMountingFeet: Boolean = false,
	/** Reset mode used for the arms */
	val armsMountingResetMode: ArmsMountingResetMode = ArmsMountingResetMode.BACK,
	/** Yaw reset smoothing time in seconds */
	val yawResetSmoothTime: Float = 0.0f,
	/** Save automatic mounting reset calibration */
	val saveMountingReset: Boolean = false,
	/** Reset a VR headset's pitch and roll on full reset */
	val resetHmdAttitude: Boolean = false,
	/** How a mounting reset calibrates the trackers, UNKNOWN until the user has picked one */
	val mountingMethod: MountingMethod = MountingMethod.UNKNOWN,
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
	val floorClip: Boolean = true,
	val skatingCorrection: Boolean = true,
	val toeSnap: Boolean = true,
	val footPlant: Boolean = true,
	val mocapMode: Boolean = false,
	val useTrackerPositions: Boolean = true,
	val enforceConstraints: Boolean = true,
)

// Used in SkeletonConfig
@Serializable
data class SkeletonRatiosConfig(
	val spineCurvature: Float = 0.75f,
	val imputeSpineFromUpperToLower: Float = DEFAULT_SPINE_UPPER_LOWER,
	val interpolateUpperLegsTwistWithLowerLegs: Float = 0.85f,
	val skatingCorrectionStrength: Float = 0.5f,
)

// Used in SkeletonConfig
@Serializable
data class SkeletonFilteringConfig(
	val type: FilteringType = FilteringType.NONE,
	val amount: Float = 0.25f,
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
	val enabled: Boolean = false,
	val portIn: Int = 39540,
	val portOut: Int = 39539,
	val address: String = "127.0.0.1",
	/** Mirror the tracking before sending it (turn left <=> turn right, left leg <=> right leg) */
	val mirrorTracking: Boolean = false,
	/** Anchor the tracking at the hip (sitting down)? */
	val anchorAtHips: Boolean = true,
	/** JSON part of the VRM to be used */
	val vrmJson: String? = null,
)

@Serializable
data class StayAlignedConfig(
	/** Whether Stay Aligned runs. The relaxed poses it uses are in the user config. */
	val enabled: Boolean = false,
)

@Serializable
data class SettingsConfigState(
	val version: Int = SETTINGS_CONFIG_VERSION,
	val mutedVRCWarnings: List<String> = listOf(),
	val mutedChecklistSteps: Set<String> = emptySet(),
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
	data class Rename(val name: String) : SettingsActions
}

typealias SettingsContext = Context<SettingsState, SettingsActions>
typealias SettingsBehaviour = Behaviour<Settings>

class Settings(
	val context: SettingsContext,
	private val scope: CoroutineScope,
	private val storage: ConfigStorage,
	private val settingsDir: String,
) {
	fun startObserving() = context.observeAll(this)

	private var autosave: Autosave = startAutosave()

	private fun startAutosave() = launchAutosave(
		scope = scope,
		state = context.state,
		storage = storage,
		toPath = { state -> configPath(settingsDir, "${state.name}.json") },
		serialize = { state -> jsonConfig.encodeToString(state.data) },
	)

	suspend fun flush() = autosave.flush()

	suspend fun rename(newName: String) {
		autosave.stop()
		try {
			storage.move(configPath(settingsDir, profileFileName(context.state.value.name)), configPath(settingsDir, profileFileName(newName)))
			context.dispatch(SettingsActions.Rename(newName))
		} finally {
			autosave = startAutosave()
		}
	}

	suspend fun swap(newName: String) {
		autosave.stop()

		val newData = loadFileWithBackup(storage, configPath(settingsDir, "$newName.json"), SettingsConfigState()) {
			parseAndMigrateSettingsConfig(it)
		}
		val newState = SettingsState(name = newName, data = newData)
		context.dispatch(SettingsActions.LoadProfile(newState))

		autosave = startAutosave()
	}

	companion object {
		suspend fun create(scope: CoroutineScope, storage: ConfigStorage, name: String): Settings {
			val settingsDir = "settings"

			val initialData = loadFileWithBackup(storage, configPath(settingsDir, "$name.json"), SettingsConfigState()) {
				parseAndMigrateSettingsConfig(it)
			}
			val initialState = SettingsState(name = name, data = initialData)

			val context = Context.create(
				initialState = initialState,
				scope = scope,
				reducer = ::reduce,
				name = "Settings[$name]",
			)
			val settings = Settings(context, scope = scope, storage = storage, settingsDir = settingsDir)
			settings.startObserving()
			return settings
		}
	}
}
