package dev.slimevr.config

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
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
typealias GlobalConfigBehaviour = Behaviour<GlobalConfigState, GlobalConfigActions, GlobalConfigContext>

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

class AppConfig(
	val globalContext: GlobalConfigContext,
	val userConfig: UserConfig,
	val settings: Settings,
) {
	suspend fun switchUserProfile(name: String) {
		globalContext.dispatch(GlobalConfigActions.SetUserProfile(name))
		userConfig.swap(name)
	}

	suspend fun switchSettingsProfile(name: String) {
		globalContext.dispatch(GlobalConfigActions.SetSettingsProfile(name))
		settings.swap(name)
	}

	companion object {
		suspend fun create(scope: CoroutineScope, configFolder: File): AppConfig {
			val initialGlobal = loadFileWithBackup(File(configFolder, "global.json"), GlobalConfigState()) {
				parseAndMigrateGlobalConfig(it)
			}

			val behaviours = listOf(DefaultGlobalConfigBehaviour)
			val globalContext = Context.create(
				initialState = initialGlobal,
				scope = scope,
				behaviours = behaviours,
			)
			behaviours.forEach { it.observe(globalContext) }

			launchAutosave(
				scope = scope,
				state = globalContext.state,
				toFile = { File(configFolder, "global.json") },
				serialize = { jsonConfig.encodeToString(it) },
			)

			val userConfig = UserConfig.create(scope, configFolder, initialGlobal.selectedUserProfile)
			val settings = Settings.create(scope, configFolder, initialGlobal.selectedSettingsProfile)

			return AppConfig(
				globalContext = globalContext,
				userConfig = userConfig,
				settings = settings,
			)
		}
	}
}