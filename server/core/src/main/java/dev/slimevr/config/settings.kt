package dev.slimevr.config

import dev.slimevr.context.Context
import dev.slimevr.context.CustomModule
import dev.slimevr.context.createContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

private const val SETTINGS_CONFIG_VERSION = 1

@Serializable
data class SettingsConfigState(
	val trackerPort: Int = 6969,
	val version: Int = SETTINGS_CONFIG_VERSION,
)

private fun migrateSettingsConfig(json: JsonObject): JsonObject {
	val version = json["version"]?.jsonPrimitive?.intOrNull ?: 0
	return when {
		// add migration branches here as: version < N -> migrateSettingsConfig(...)
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
	data class Update(val transform: SettingsState.() -> SettingsState) : SettingsActions
	data class LoadProfile(val newState: SettingsState) : SettingsActions
}

typealias SettingsContext = Context<SettingsState, SettingsActions>
typealias SettingsModule = CustomModule<SettingsState, SettingsActions, Settings>

data class Settings(
	val context: SettingsContext,
	val configDir: String,
	val swap: suspend (String) -> Unit,
)

val DefaultSettingsModule = SettingsModule(
	reducer = { s, a ->
		when (a) {
			is SettingsActions.Update -> a.transform(s)
			is SettingsActions.LoadProfile -> a.newState
			else -> s
		}
	},
)

suspend fun createSettings(scope: CoroutineScope, configDir: File, name: String): Settings {
	val settingsDir = File(configDir, "settings")

	val initialData = loadFileWithBackup(File(settingsDir, "$name.json"), SettingsConfigState()) {
		parseAndMigrateSettingsConfig(it)
	}
	val initialState = SettingsState(name = name, data = initialData)

	val modules = listOf(DefaultSettingsModule)
	val context = createContext(
		initialState = initialState,
		reducers = modules.map { it.reducer },
		scope = scope
	)

	fun startAutosave() = launchAutosave(
		scope = scope,
		state = context.state,
		toFile = { state -> File(settingsDir, "${state.name}.json") },
		serialize = { state -> jsonConfig.encodeToString(state.data) },
	)

	var autosaveJob: Job = startAutosave()

	val swap: suspend (String) -> Unit = { newName ->
		autosaveJob.cancelAndJoin()

		val newData = loadFileWithBackup(File(settingsDir, "$newName.json"), SettingsConfigState()) {
			parseAndMigrateSettingsConfig(it)
		}
		val newState = SettingsState(name = newName, data = newData)
		context.dispatch(SettingsActions.LoadProfile(newState))

		autosaveJob = startAutosave()
	}

	return Settings(context, configDir = settingsDir.toString(), swap)
}
