package dev.slimevr.config

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
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

private const val USER_CONFIG_VERSION = 1

@Serializable
data class UserConfigData(
	val userHeight: Float = 1.6f,
	val proportions: Map<String, Float> = emptyMap(),
	val version: Int = USER_CONFIG_VERSION,
)

private fun migrateUserConfig(json: JsonObject): JsonObject {
	val version = json["version"]?.jsonPrimitive?.intOrNull ?: 0
	return when {
		// add migration branches here as: version < N -> migrateUserConfig(...)
		else -> json
	}
}

private fun parseAndMigrateUserConfig(raw: String): UserConfigData {
	val json = jsonConfig.parseToJsonElement(raw).jsonObject
	return jsonConfig.decodeFromJsonElement(migrateUserConfig(json))
}

data class UserConfigState(
	val data: UserConfigData,
	val name: String,
)

sealed interface UserConfigActions {
	data class Update(val transform: UserConfigData.() -> UserConfigData) : UserConfigActions
	data class LoadProfile(val newState: UserConfigState) : UserConfigActions
}

typealias UserConfigContext = Context<UserConfigState, UserConfigActions>
typealias UserConfigBehaviour = Behaviour<UserConfigState, UserConfigActions, UserConfig>

class UserConfig(
	val context: UserConfigContext,
	private val scope: CoroutineScope,
	private val userConfigDir: File,
) {
	private var autosaveJob: Job = startAutosave()

	private fun startAutosave() = launchAutosave(
		scope = scope,
		state = context.state,
		toFile = { state -> File(userConfigDir, "${state.name}.json") },
		serialize = { state -> jsonConfig.encodeToString(state.data) },
	)

	suspend fun swap(newName: String) {
		autosaveJob.cancelAndJoin()

		val newData = loadFileWithBackup(File(userConfigDir, "$newName.json"), UserConfigData()) {
			parseAndMigrateUserConfig(it)
		}
		val newState = UserConfigState(name = newName, data = newData)
		context.dispatch(UserConfigActions.LoadProfile(newState))

		autosaveJob = startAutosave()
	}

	companion object {
		suspend fun create(scope: CoroutineScope, configDir: File, name: String): UserConfig {
			val userConfigDir = File(configDir, "user")

			val initialData = loadFileWithBackup(File(userConfigDir, "$name.json"), UserConfigData()) {
				parseAndMigrateUserConfig(it)
			}
			val initialState = UserConfigState(name = name, data = initialData)

			val context = Context.create(
				initialState = initialState,
				scope = scope,
				behaviours = listOf(DefaultUserBehaviour),
				name = "UserConfig[$name]",
			)
			val userConfig = UserConfig(context, scope = scope, userConfigDir = userConfigDir)
			context.observeAll(userConfig)
			return userConfig
		}
	}
}
