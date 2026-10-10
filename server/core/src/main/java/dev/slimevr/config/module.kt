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
import solarxr_protocol.rpc.ErrorReportingConsent
import java.util.UUID

private const val GLOBAL_CONFIG_VERSION = 1
private const val CURRENT_CONSENT_VERSION = 2

@Serializable
data class ErrorReportingConfig(
	val consent: ErrorReportingConsent = ErrorReportingConsent.UNDECIDED,
	/** Version of the consent text the user answered. So we can change the version and ask for consent again */
	val consentVersion: Int = CURRENT_CONSENT_VERSION,
	val userId: String = UUID.randomUUID().toString(),
)

fun effectiveConsent(config: ErrorReportingConfig): ErrorReportingConsent {
	if (config.consent == ErrorReportingConsent.ALLOWED && config.consentVersion < CURRENT_CONSENT_VERSION) {
		return ErrorReportingConsent.UNDECIDED
	}
	return config.consent
}

fun changeConsent(config: ErrorReportingConfig, consent: ErrorReportingConsent): ErrorReportingConfig {
	val version = if (consent == ErrorReportingConsent.UNDECIDED) 0 else CURRENT_CONSENT_VERSION
	return config.copy(consent = consent, consentVersion = version)
}

@Serializable
data class GlobalConfigState(
	val selectedUserProfile: String = "default",
	val selectedSettingsProfile: String = "default",
	val errorReporting: ErrorReportingConfig = ErrorReportingConfig(),
	val version: Int = GLOBAL_CONFIG_VERSION,
)

sealed interface GlobalConfigActions {
	data class SetUserProfile(val name: String) : GlobalConfigActions
	data class SetSettingsProfile(val name: String) : GlobalConfigActions
	data class SetErrorReporting(val config: ErrorReportingConfig) : GlobalConfigActions
}

typealias GlobalConfigContext = Context<GlobalConfigState, GlobalConfigActions>
typealias GlobalConfigBehaviour = Behaviour<GlobalConfig>

class GlobalConfig(
	val context: GlobalConfigContext,
) {
	fun startObserving() = context.observeAll(this)

	companion object {
		suspend fun create(scope: CoroutineScope, storage: ConfigStorage): GlobalConfig {
			val initialState = loadFileWithBackup(storage, "global.json", GlobalConfigState()) {
				parseAndMigrateGlobalConfig(it)
			}
			val context = Context.create(
				initialState = initialState,
				scope = scope,
				reducer = ::reduce,
				name = "GlobalConfig",
			)
			val globalConfig = GlobalConfig(context)
			globalConfig.startObserving()
			// Persists defaults filled on load (the generated userId), autosave skips the initial state
			storage.write("global.json", jsonConfig.encodeToString(initialState))
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

	fun reset() {
		settings.context.dispatch(SettingsActions.Update { SettingsConfigState() })
		userConfig.context.dispatch(UserConfigActions.Update { UserConfigData() })
		val errorReporting = globalConfig.context.state.value.errorReporting
		globalConfig.context.dispatch(
			GlobalConfigActions.SetErrorReporting(changeConsent(errorReporting, ErrorReportingConsent.UNDECIDED)),
		)
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
