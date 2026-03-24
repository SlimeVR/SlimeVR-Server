package dev.slimevr.config

import dev.slimevr.context.Context
import dev.slimevr.context.CustomModule
import dev.slimevr.context.createContext
import dev.slimevr.tracker.DeviceActions
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
	data class Update(val transform: UserConfigState.() -> UserConfigState) : UserConfigActions
	data class LoadProfile(val newState: UserConfigState) : UserConfigActions
}

typealias UserConfigContext = Context<UserConfigState, UserConfigActions>
typealias UserConfigModule = CustomModule<UserConfigState, UserConfigActions, UserConfig>

data class UserConfig(
	val context: UserConfigContext,
	val configDir: String,
	val swap: suspend (String) -> Unit,
)

val DefaultUserModule = UserConfigModule(
	reducer = { s, a ->
		when (a) {
			is UserConfigActions.Update -> a.transform(s)
			is UserConfigActions.LoadProfile -> a.newState
			else -> s
		}
	},
)

suspend fun createUserConfig(scope: CoroutineScope, configDir: File, name: String): UserConfig {
	val userConfigDir = File(configDir, "user")

	val initialData = loadFileWithBackup(File(userConfigDir, "$name.json"), UserConfigData()) {
		parseAndMigrateUserConfig(it)
	}
	val initialState = UserConfigState(name = name, data = initialData)

	val modules = listOf(DefaultUserModule)
	val context = createContext(
		initialState = initialState,
		reducers = modules.map { it.reducer },
		scope = scope
	)

	fun startAutosave() = launchAutosave(
		scope = scope,
		state = context.state,
		toFile = { state -> File(userConfigDir, "${state.name}.json") },
		serialize = { state -> jsonConfig.encodeToString(state.data) },
	)

	var autosaveJob: Job = startAutosave()

	val swap: suspend (String) -> Unit = { newName ->
		autosaveJob.cancelAndJoin()

		val newData = loadFileWithBackup(File(userConfigDir, "$newName.json"), UserConfigData()) {
			parseAndMigrateUserConfig(it)
		}
		val newState = UserConfigState(name = newName, data = newData)
		context.dispatch(UserConfigActions.LoadProfile(newState))

		autosaveJob = startAutosave()
	}

	return UserConfig(context, userConfigDir.toString(), swap)
}
