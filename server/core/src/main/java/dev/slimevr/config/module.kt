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
typealias GlobalConfigBehaviour = Behaviour<GlobalConfigState, GlobalConfigActions, GlobalConfig>

class GlobalConfig(
	val context: GlobalConfigContext,
) {
	fun startObserving() = context.observeAll(this)

	companion object {
		suspend fun create(scope: CoroutineScope, storage: ConfigStorage): GlobalConfig {
			val initialState = loadFileWithBackup(storage, "global.json", GlobalConfigState()) {
				parseAndMigrateGlobalConfig(it)
			}
			val behaviours = listOf(DefaultGlobalConfigBehaviour())
			val context = Context.create(
				initialState = initialState,
				scope = scope,
				behaviours = behaviours,
				name = "GlobalConfig",
			)
			val globalConfig = GlobalConfig(context)
			globalConfig.startObserving()
			launchAutosave(
				scope = scope,
				state = context.state,
				storage = storage,
				toPath = { "global.json" },
				serialize = { jsonConfig.encodeToString(it) },
			)
			return globalConfig
		}
	}
}

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
	val globalConfig: GlobalConfig,
	val userConfig: UserConfig,
	val settings: Settings,
) {
	suspend fun switchUserProfile(name: String) {
		globalConfig.context.dispatch(GlobalConfigActions.SetUserProfile(name))
		userConfig.swap(name)
	}

	suspend fun switchSettingsProfile(name: String) {
		globalConfig.context.dispatch(GlobalConfigActions.SetSettingsProfile(name))
		settings.swap(name)
	}

	companion object {
		suspend fun create(scope: CoroutineScope, storage: ConfigStorage): AppConfig {
			val globalConfig = GlobalConfig.create(scope, storage)

			val userConfig = UserConfig.create(scope, storage, globalConfig.context.state.value.selectedUserProfile)
			val settings = Settings.create(scope, storage, globalConfig.context.state.value.selectedSettingsProfile)

			return AppConfig(
				globalConfig = globalConfig,
				userConfig = userConfig,
				settings = settings,
			)
		}
	}
}
