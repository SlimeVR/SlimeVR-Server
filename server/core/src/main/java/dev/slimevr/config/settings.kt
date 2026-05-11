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

			val behaviours = listOf(DefaultSettingsBehaviour)
			val context = Context.create(
				initialState = initialState,
				scope = scope,
				behaviours = behaviours,
				name = "Settings[$name]",
			)
			val settings = Settings(context, scope = scope, storage = storage, settingsDir = settingsDir)
			behaviours.forEach { it.observe(settings) }
			return settings
		}
	}
}
