package dev.slimevr.config

import dev.slimevr.context.BasicModule
import dev.slimevr.context.Context
import dev.slimevr.context.createContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable

@Serializable
data class UserConfigState(
	val userHeight: Float = 1.6f,
)

@Serializable
data class SettingsConfigState(
	val trackerPort: Int = 6969,
)

@Serializable
data class GlobalConfigState(
	val selectedUserProfile: String = "default",
	val selectedSettingsProfile: String = "default",
)

data class ConfigState(
	val userConfig: UserConfigState = UserConfigState(),
	val settingsConfig: SettingsConfigState = SettingsConfigState(),
	val globalConfig: GlobalConfigState = GlobalConfigState(),
)

sealed interface ConfigAction {
	data class ChangeProfile(val settingsProfile: String? = null, val userProfile: String? = null) : ConfigAction
}

typealias ConfigContext = Context<ConfigState, ConfigAction>
typealias ConfigModule = BasicModule<ConfigState, ConfigAction>

val ConfigModuleTest = ConfigModule(
	reducer = { s, a ->
		when (a) {
			is ConfigAction.ChangeProfile -> if (a.settingsProfile != null) {
				s.copy(globalConfig = s.globalConfig.copy(selectedSettingsProfile = a.settingsProfile))
			} else if (a.userProfile != null) {
				s.copy(globalConfig = s.globalConfig.copy(selectedUserProfile = a.userProfile))
			} else {
				s
			}
		}
	},
)

suspend fun createConfig(scope: CoroutineScope): ConfigContext {
	val modules = listOf(ConfigModuleTest)

	val context = createContext(
		initialState = ConfigState(),
		reducers = modules.map { it.reducer },
		scope = scope,
	)

	modules.map { it.observer }.forEach { it?.invoke(context) }

	context.dispatch(ConfigAction.ChangeProfile(settingsProfile = "Test"))

	return context
}
