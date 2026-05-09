package dev.slimevr.config

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.vmc.VMCConfig
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import solarxr_protocol.datatypes.BodyPart
import java.io.File

private const val SETTINGS_CONFIG_VERSION = 2

@Serializable
data class TrackerConfig(
	@Serializable(with = BodyPartSerializer::class)
	val bodyPart: BodyPart? = null,
	val customName: String? = null,
	@Serializable(with = QuaternionSerializer::class)
	val mountingOrientation: Quaternion? = null,
	val magEnabled: Boolean? = null,
)

@Serializable
data class SettingsConfigState(
	val trackerPort: Int = 6969,
	val mutedVRCWarnings: List<String> = listOf(),
	val mutedChecklistSteps: Set<String> = emptySet(),
	val trackers: Map<String, TrackerConfig> = emptyMap(),
	val globalMagEnabled: Boolean = true,
	val allowedUdpDevices: Set<String> = emptySet(),
	val vrcOscConfig: VRCOSCConfig = defaultVrcOscConfig(),
	val vmcConfig: VMCConfig = VMCConfig(),
	val version: Int = SETTINGS_CONFIG_VERSION,
)

private fun defaultVrcOscConfig() = VRCOSCConfig()

private fun migrateSettingsConfig(json: JsonObject): JsonObject {
	val version = json["version"]?.jsonPrimitive?.intOrNull ?: 0
	return when {
		version < 2 -> migrateSettingsConfig(migrateSettingsConfigV2(json))
		else -> json
	}
}

private fun migrateSettingsConfigV2(json: JsonObject): JsonObject {
	val vrcOscConfig = json["vrcOscConfig"]?.jsonObject
	val migratedVrcOscConfig = if (vrcOscConfig == null || "manualNetwork" in vrcOscConfig) {
		vrcOscConfig?.let(::normalizeVrcOscConfig)
	} else {
		val portIn = vrcOscConfig["portIn"]?.jsonPrimitive?.intOrNull ?: 9001
		val portOut = vrcOscConfig["portOut"]?.jsonPrimitive?.intOrNull ?: 9000
		val address = vrcOscConfig["address"]?.jsonPrimitive?.contentOrNull.orEmpty()
		val oscqueryEnabled = vrcOscConfig["oscqueryEnabled"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: true
		val useAutomaticNetwork =
			oscqueryEnabled &&
				portIn == 9001 &&
				portOut == 9000 &&
				(address.isBlank() || address == "127.0.0.1" || address == "localhost")

		buildMap {
			vrcOscConfig["enabled"]?.let { put("enabled", it) }
			vrcOscConfig["trackers"]?.let { put("trackers", it) }
			if (!useAutomaticNetwork) {
				put(
					"manualNetwork",
					JsonObject(
						mapOf(
							"portIn" to JsonPrimitive(portIn),
							"portOut" to JsonPrimitive(portOut),
							"address" to JsonPrimitive(address),
						),
					),
				)
			}
		}.let(::JsonObject)
	}

	return JsonObject(
		json.toMutableMap().apply {
			put("version", JsonPrimitive(SETTINGS_CONFIG_VERSION))
			if (migratedVrcOscConfig != null) put("vrcOscConfig", migratedVrcOscConfig)
		},
	)
}

private fun normalizeVrcOscConfig(vrcOscConfig: JsonObject): JsonObject {
	val manualNetwork = vrcOscConfig["manualNetwork"]?.jsonObject ?: return vrcOscConfig
	val normalizedManualNetwork = JsonObject(
		mapOf(
			"portIn" to (manualNetwork["portIn"] ?: JsonPrimitive(DEFAULT_VRC_OSC_PORT_IN)),
			"portOut" to (manualNetwork["portOut"] ?: JsonPrimitive(DEFAULT_VRC_OSC_PORT_OUT)),
			"address" to (manualNetwork["address"] ?: JsonPrimitive(DEFAULT_VRC_OSC_ADDRESS)),
		),
	)
	return JsonObject(
		vrcOscConfig.toMutableMap().apply {
			put("manualNetwork", normalizedManualNetwork)
		},
	)
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
	private val settingsDir: File,
) {
	private var autosaveJob: Job = startAutosave()

	private fun startAutosave() = launchAutosave(
		scope = scope,
		state = context.state,
		toFile = { state -> File(settingsDir, "${state.name}.json") },
		serialize = { state -> jsonConfig.encodeToString(state.data) },
	)

	suspend fun swap(newName: String) {
		autosaveJob.cancelAndJoin()

		val newData = loadFileWithBackup(File(settingsDir, "$newName.json"), SettingsConfigState()) {
			parseAndMigrateSettingsConfig(it)
		}
		val newState = SettingsState(name = newName, data = newData)
		context.dispatch(SettingsActions.LoadProfile(newState))

		autosaveJob = startAutosave()
	}

	companion object {
		suspend fun create(scope: CoroutineScope, configDir: File, name: String): Settings {
			val settingsDir = File(configDir, "settings")

			val initialData = loadFileWithBackup(File(settingsDir, "$name.json"), SettingsConfigState()) {
				parseAndMigrateSettingsConfig(it)
			}
			val initialState = SettingsState(name = name, data = initialData)

			val behaviours = listOf(DefaultSettingsBehaviour)
			val context = Context.create(
				initialState = initialState,
				scope = scope,
				behaviours = behaviours,
				name = "Settings[$name]",
			)
			val settings = Settings(context, scope = scope, settingsDir = settingsDir)
			behaviours.forEach { it.observe(settings) }
			return settings
		}
	}
}
