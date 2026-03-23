package dev.slimevr.config

import dev.slimevr.context.BasicModule
import dev.slimevr.context.Context
import dev.slimevr.context.createContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File


private const val GLOBAL_CONFIG_VERSION = 1

@Serializable
data class GlobalConfigState(
	val selectedUserProfile: String = "default",
	val selectedSettingsProfile: String = "default",
	val version: Int = GLOBAL_CONFIG_VERSION,
)

sealed interface GlobalConfigActions {
	data class SetUserProfile(val name: String) : GlobalConfigActions
	data class SetSettingsProfile(val name: String) : GlobalConfigActions
}

typealias GlobalConfigContext = Context<GlobalConfigState, GlobalConfigActions>
typealias GlobalConfigModule = BasicModule<GlobalConfigState, GlobalConfigActions>

private fun migrateGlobalConfig(json: JsonObject): JsonObject {
	val version = json["version"]?.jsonPrimitive?.intOrNull ?: 0
	return when {
		// add migration branches here as: version < N -> migrateGlobalConfig(...)
		else -> json
	}
}

private fun parseAndMigrateGlobalConfig(raw: String): GlobalConfigState {
	val json = jsonConfig.parseToJsonElement(raw).jsonObject
	return jsonConfig.decodeFromJsonElement(migrateGlobalConfig(json))
}

val DefaultGlobalConfigModule = GlobalConfigModule(
	reducer = { s, a ->
		when (a) {
			is GlobalConfigActions.SetUserProfile -> s.copy(selectedUserProfile = a.name)
			is GlobalConfigActions.SetSettingsProfile -> s.copy(selectedSettingsProfile = a.name)
		}
	},
)

data class AppConfig(
	val globalContext: GlobalConfigContext,
	val userConfig: UserConfig,
	val settings: Settings,
	val switchUserProfile: suspend (String) -> Unit,
	val switchSettingsProfile: suspend (String) -> Unit,
)

suspend fun createAppConfig(scope: CoroutineScope, configFolder: File): AppConfig {
	val initialGlobal = loadFileWithBackup(File(configFolder, "global.json"), GlobalConfigState()) {
		parseAndMigrateGlobalConfig(it)
	}

	val globalContext = createContext(
		initialState = initialGlobal,
		reducers = listOf(DefaultGlobalConfigModule.reducer),
		scope = scope,
	)

	launchAutosave(
		scope = scope,
		state = globalContext.state,
		toFile = { File(configFolder, "global.json") },
		serialize = { jsonConfig.encodeToString(it) },
	)

	val userConfig = createUserConfig(scope, configFolder, initialGlobal.selectedUserProfile)
	val settings = createSettings(scope, configFolder, initialGlobal.selectedSettingsProfile)

	val switchUserProfile: suspend (String) -> Unit = { name ->
		globalContext.dispatch(GlobalConfigActions.SetUserProfile(name))
		userConfig.swap(name)
	}

	val switchSettingsProfile: suspend (String) -> Unit = { name ->
		globalContext.dispatch(GlobalConfigActions.SetSettingsProfile(name))
		settings.swap(name)
	}

	return AppConfig(
		globalContext = globalContext,
		userConfig = userConfig,
		settings = settings,
		switchUserProfile = switchUserProfile,
		switchSettingsProfile = switchSettingsProfile,
	)
}
