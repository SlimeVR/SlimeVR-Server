package dev.slimevr.config

import dev.slimevr.logging.AppLogger
import dev.slimevr.tracker.hidTrackerHardwareId
import dev.slimevr.tracker.udpTrackerHardwareId
import io.github.axisangles.ktmath.Quaternion
import it.krzeminski.snakeyaml.engine.kmp.api.Load
import it.krzeminski.snakeyaml.engine.kmp.api.LoadSettings
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ArmsMountingResetMode
import solarxr_protocol.rpc.StayAlignedRelaxedPose

/** Config file of the previous server, in the same folder as the current config. */
const val LEGACY_CONFIG_PATH = "vrconfig.yml"
private const val MIN_LEGACY_CONFIG_VERSION = 14

private val UDP_TRACKER_NAME = Regex("udp://((?:[0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2})/(\\d{1,9})")
private val HID_TRACKER_NAME = Regex("([0-9A-Fa-f]{12})/0")

private val LEGACY_POSE_KEYS = listOf(
	StayAlignedRelaxedPose.STANDING to "standingRelaxedPose",
	StayAlignedRelaxedPose.SITTING to "sittingRelaxedPose",
	StayAlignedRelaxedPose.FLAT to "flatRelaxedPose",
)

data class LegacyConfigMigration(
	val settings: SettingsConfigState,
	val user: UserConfigData,
	val devices: DevicesConfigState,
)

suspend fun importLegacyConfig(storage: ConfigStorage) {
	if (storage.exists(GLOBAL_CONFIG_PATH)) return
	val raw = storage.read(LEGACY_CONFIG_PATH) ?: return

	val migration = try {
		migrateLegacyConfig(raw)
	} catch (e: Exception) {
		AppLogger.config.error(e, "Failed to read ${storage.displayPath(LEGACY_CONFIG_PATH)}")
		return
	}
	if (migration == null) {
		AppLogger.config.info("Skipped ${storage.displayPath(LEGACY_CONFIG_PATH)}, only version $MIN_LEGACY_CONFIG_VERSION and newer are imported")
		return
	}

	val defaults = GlobalConfigState()
	storage.write(
		configPath(SETTINGS_PROFILE_DIR, profileFileName(defaults.selectedSettingsProfile)),
		jsonConfig.encodeToString(migration.settings),
		overwrite = false,
	)
	storage.write(
		configPath(USER_PROFILE_DIR, profileFileName(defaults.selectedUserProfile)),
		jsonConfig.encodeToString(migration.user),
		overwrite = false,
	)
	storage.write(DEVICES_CONFIG_PATH, jsonConfig.encodeToString(migration.devices), overwrite = false)
	AppLogger.config.info("Imported ${storage.displayPath(LEGACY_CONFIG_PATH)}")
}

fun migrateLegacyConfig(yaml: String): LegacyConfigMigration? {
	// Configs saved before the GUI trimmed the VRM hold its whole glTF JSON, which can go past the 3 MB default
	val loadSettings = LoadSettings.builder().setCodePointLimit(Int.MAX_VALUE).build()
	val root = Load(loadSettings).loadOne(yaml) as? Map<*, *> ?: return null
	val version = intOf(root["modelVersion"]) ?: return null
	if (version < MIN_LEGACY_CONFIG_VERSION) return null

	return LegacyConfigMigration(
		settings = settingsOf(root),
		user = userOf(root),
		devices = devicesOf(root),
	)
}

private fun settingsOf(root: Map<*, *>): SettingsConfigState {
	val defaults = SettingsConfigState()
	val server = section(root, "server")
	val skeleton = section(root, "skeleton")
	val toggles = section(skeleton, "toggles")
	val values = section(skeleton, "values")
	val resets = section(root, "resetsConfig")
	val tap = section(root, "tapDetection")
	val vmc = section(root, "vmc")
	val hid = section(root, "hidConfig")
	val stayAligned = section(root, "stayAlignedConfig")

	val defaultToggles = defaults.skeletonConfig.toggles
	val defaultRatios = defaults.skeletonConfig.ratios
	val defaultResets = defaults.resetsConfig
	val defaultTap = defaults.tapDetectionConfig
	val defaultVmc = defaults.vmcConfig

	return defaults.copy(
		trackersConfig = defaults.trackersConfig.copy(
			globalMagEnabled = boolOf(server["useMagnetometerOnAllTrackers"]) ?: defaults.trackersConfig.globalMagEnabled,
		),
		hidConfig = defaults.hidConfig.copy(
			trackersOverHid = boolOf(hid["trackersOverHID"]) ?: defaults.hidConfig.trackersOverHid,
		),
		skeletonConfig = defaults.skeletonConfig.copy(
			toggles = defaultToggles.copy(
				floorClip = boolOf(toggles["floorClip"]) ?: defaultToggles.floorClip,
				skatingCorrection = boolOf(toggles["skatingCorrection"]) ?: defaultToggles.skatingCorrection,
				toeSnap = boolOf(toggles["toeSnap"]) ?: defaultToggles.toeSnap,
				footPlant = boolOf(toggles["footPlant"]) ?: defaultToggles.footPlant,
				mocapMode = boolOf(toggles["selfLocalization"]) ?: defaultToggles.mocapMode,
				enforceConstraints = boolOf(toggles["enforceConstraints"]) ?: defaultToggles.enforceConstraints,
			),
			ratios = defaultRatios.copy(
				interpolateUpperLegsTwistWithLowerLegs = floatOf(values["kneeTrackerAnkleAveraging"])
					?: defaultRatios.interpolateUpperLegsTwistWithLowerLegs,
			),
		),
		resetsConfig = defaultResets.copy(
			resetMountingFeet = boolOf(resets["resetMountingFeet"]) ?: defaultResets.resetMountingFeet,
			armsMountingResetMode = armsMountingResetModeOf(resets["mode"]) ?: defaultResets.armsMountingResetMode,
			yawResetSmoothTime = floatOf(resets["yawResetSmoothTime"]) ?: defaultResets.yawResetSmoothTime,
			saveMountingReset = boolOf(resets["saveMountingReset"]) ?: defaultResets.saveMountingReset,
			resetHmdAttitude = boolOf(resets["resetHmdPitch"]) ?: defaultResets.resetHmdAttitude,
		),
		tapDetectionConfig = defaultTap.copy(
			yawResetDelay = floatOf(tap["yawResetDelay"]) ?: defaultTap.yawResetDelay,
			fullResetDelay = floatOf(tap["fullResetDelay"]) ?: defaultTap.fullResetDelay,
			mountingResetDelay = floatOf(tap["mountingResetDelay"]) ?: defaultTap.mountingResetDelay,
			yawResetEnabled = boolOf(tap["yawResetEnabled"]) ?: defaultTap.yawResetEnabled,
			fullResetEnabled = boolOf(tap["fullResetEnabled"]) ?: defaultTap.fullResetEnabled,
			mountingResetEnabled = boolOf(tap["mountingResetEnabled"]) ?: defaultTap.mountingResetEnabled,
			yawResetBodyPart = stringOf(tap["yawResetTracker"])?.let(::bodyPartOf) ?: defaultTap.yawResetBodyPart,
			fullResetBodyPart = stringOf(tap["fullResetTracker"])?.let(::bodyPartOf) ?: defaultTap.fullResetBodyPart,
			mountingResetBodyPart = stringOf(tap["mountingResetTracker"])?.let(::bodyPartOf) ?: defaultTap.mountingResetBodyPart,
			yawResetTaps = intOf(tap["yawResetTaps"]) ?: defaultTap.yawResetTaps,
			fullResetTaps = intOf(tap["fullResetTaps"]) ?: defaultTap.fullResetTaps,
			mountingResetTaps = intOf(tap["mountingResetTaps"]) ?: defaultTap.mountingResetTaps,
			numberTrackersOverThreshold = intOf(tap["numberTrackersOverThreshold"]) ?: defaultTap.numberTrackersOverThreshold,
		),
		vmcConfig = defaultVmc.copy(
			enabled = boolOf(vmc["enabled"]) ?: defaultVmc.enabled,
			portIn = intOf(vmc["portIn"]) ?: defaultVmc.portIn,
			portOut = intOf(vmc["portOut"]) ?: defaultVmc.portOut,
			address = stringOf(vmc["address"]) ?: defaultVmc.address,
			mirrorTracking = boolOf(vmc["mirrorTracking"]) ?: defaultVmc.mirrorTracking,
			anchorAtHips = boolOf(vmc["anchorHip"]) ?: defaultVmc.anchorAtHips,
			vrmJson = vrmJsonOf(vmc["vrmJson"]) ?: defaultVmc.vrmJson,
		),
		stayAlignedConfig = defaults.stayAlignedConfig.copy(
			enabled = boolOf(stayAligned["enabled"]) ?: defaults.stayAlignedConfig.enabled,
		),
	)
}

private fun userOf(root: Map<*, *>): UserConfigData {
	val skeleton = section(root, "skeleton")
	val stayAligned = section(root, "stayAlignedConfig")

	val poses = LEGACY_POSE_KEYS.mapNotNull { (pose, key) ->
		val old = section(stayAligned, key)
		if (boolOf(old["enabled"]) != true) return@mapNotNull null
		pose to StayAlignedPoseConfig(
			recorded = true,
			enabled = true,
			upperLegAngleInDeg = floatOf(old["upperLegAngleInDeg"]) ?: 0f,
			lowerLegAngleInDeg = floatOf(old["lowerLegAngleInDeg"]) ?: 0f,
			footAngleInDeg = floatOf(old["footAngleInDeg"]) ?: 0f,
		)
	}.toMap()

	val user = UserConfigData(stayAlignedPoses = poses)
	val height = (floatOf(skeleton["hmdHeight"]) ?: 0f) - (floatOf(skeleton["floorHeight"]) ?: 0f)
	if (height < MIN_USER_HEIGHT) return user
	// Proportions are not imported, they are the defaults for the imported height
	return withDefaultProportions(user.copy(userHeight = height))
}

private fun devicesOf(root: Map<*, *>): DevicesConfigState {
	val oldTrackers = section(root, "trackers")
	val trackers = oldTrackers.entries.mapNotNull { (name, old) ->
		val hardwareId = (name as? String)?.let(::hardwareIdOf) ?: return@mapNotNull null
		val config = old as? Map<*, *> ?: return@mapNotNull null
		hardwareId to TrackerConfig(
			bodyPart = stringOf(config["designation"])?.let { bodyPartOf(it.removePrefix("body:").uppercase()) },
			customName = stringOf(config["customName"])?.takeIf { it.isNotBlank() },
			mountingOrientation = quaternionOf(config["mountingOrientation"]) ?: Quaternion.IDENTITY,
		)
	}.toMap()

	val knownDevices = (root["knownDevices"] as? List<*>).orEmpty().mapNotNull { stringOf(it)?.uppercase() }.toSet()

	return DevicesConfigState(allowedUdpDevices = knownDevices, trackers = trackers)
}

private fun hardwareIdOf(trackerName: String): String? {
	UDP_TRACKER_NAME.matchEntire(trackerName)?.let { return udpTrackerHardwareId(it.groupValues[1].uppercase(), it.groupValues[2].toInt()) }
	HID_TRACKER_NAME.matchEntire(trackerName)?.let { return hidTrackerHardwareId(it.groupValues[1].uppercase()) }
	return null
}

private fun bodyPartOf(trackerPosition: String): BodyPart? = when (trackerPosition) {
	"CHEST" -> BodyPart.UPPER_CHEST
	"WAIST" -> BodyPart.LOWER_WAIST
	else -> BodyPart.entries.firstOrNull { it != BodyPart.NONE && it.name == trackerPosition }
}

/** T-pose down does not exist anymore, both T-poses become the side T-pose. */
private fun armsMountingResetModeOf(value: Any?): ArmsMountingResetMode? = when (stringOf(value)) {
	"BACK" -> ArmsMountingResetMode.BACK
	"FORWARD" -> ArmsMountingResetMode.FORWARD
	"TPOSE_UP", "TPOSE_DOWN" -> ArmsMountingResetMode.SIDE
	else -> null
}

/** Strip older VRM that were too big */
private fun vrmJsonOf(value: Any?): String? {
	val raw = stringOf(value)?.takeIf { it.isNotEmpty() } ?: return null
	val gltf = try {
		jsonConfig.parseToJsonElement(raw).jsonObject
	} catch (_: IllegalArgumentException) {
		return null
	}
	val extensions = gltf["extensions"] as? JsonObject
	return buildJsonObject {
		if (extensions != null) {
			put(
				"extensions",
				buildJsonObject {
					extensions["VRM"]?.let { put("VRM", it) }
					extensions["VRMC_vrm"]?.let { put("VRMC_vrm", it) }
				},
			)
		}
		gltf["extensionsUsed"]?.let { put("extensionsUsed", it) }
		gltf["nodes"]?.let { put("nodes", it) }
	}.toString()
}

private fun quaternionOf(value: Any?): Quaternion? {
	val map = value as? Map<*, *> ?: return null
	return Quaternion(
		floatOf(map["w"]) ?: return null,
		floatOf(map["x"]) ?: return null,
		floatOf(map["y"]) ?: return null,
		floatOf(map["z"]) ?: return null,
	)
}

private fun section(map: Map<*, *>, key: String): Map<*, *> = map[key] as? Map<*, *> ?: emptyMap<Any?, Any?>()

private fun stringOf(value: Any?): String? = value as? String

private fun boolOf(value: Any?): Boolean? = when (value) {
	is Boolean -> value
	is String -> value.toBooleanStrictOrNull()
	else -> null
}

private fun floatOf(value: Any?): Float? = when (value) {
	is Number -> value.toFloat()
	is String -> value.toFloatOrNull()
	else -> null
}

private fun intOf(value: Any?): Int? = when (value) {
	is Number -> value.toInt()
	is String -> value.toIntOrNull()
	else -> null
}
