package dev.slimevr.config

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import solarxr_protocol.rpc.ErrorReportingConsent
import solarxr_protocol.rpc.ProfileKind
import solarxr_protocol.rpc.ProfileOperationStatus
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
			}.let(::withSafeProfileNames)
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

private fun withSafeProfileNames(state: GlobalConfigState): GlobalConfigState {
	val defaults = GlobalConfigState()
	return state.copy(
		selectedUserProfile = state.selectedUserProfile.takeIf(::isSafeProfileName) ?: defaults.selectedUserProfile,
		selectedSettingsProfile = state.selectedSettingsProfile.takeIf(::isSafeProfileName) ?: defaults.selectedSettingsProfile,
	)
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
	val devices: DevicesConfig,
	private val storage: ConfigStorage,
) {
	private val profilesLock = Mutex()
	private val profilesState = MutableStateFlow(
		ProfilesState(
			settings = ProfileNames(settings.context.state.value.name, listOf(settings.context.state.value.name)),
			user = ProfileNames(userConfig.context.state.value.name, listOf(userConfig.context.state.value.name)),
		),
	)

	/** Names of the profiles on disk, always including the active ones. */
	val profiles: StateFlow<ProfilesState> = profilesState

	suspend fun refreshProfiles() {
		profilesState.value = ProfilesState(
			settings = ProfileNames(activeProfile(ProfileKind.SETTINGS), listProfiles(ProfileKind.SETTINGS)),
			user = ProfileNames(activeProfile(ProfileKind.USER), listProfiles(ProfileKind.USER)),
		)
	}

	private fun dirOf(kind: ProfileKind) = when (kind) {
		ProfileKind.SETTINGS -> SETTINGS_PROFILE_DIR
		ProfileKind.USER -> USER_PROFILE_DIR
		ProfileKind.UNKNOWN -> error("No profile directory for $kind")
	}

	private fun pathOf(kind: ProfileKind, name: String) = configPath(dirOf(kind), profileFileName(name))

	private fun activeProfile(kind: ProfileKind) = when (kind) {
		ProfileKind.SETTINGS -> settings.context.state.value.name
		ProfileKind.USER -> userConfig.context.state.value.name
		ProfileKind.UNKNOWN -> error("No active profile for $kind")
	}

	private suspend fun flushActive(kind: ProfileKind) = when (kind) {
		ProfileKind.SETTINGS -> settings.flush()
		ProfileKind.USER -> userConfig.flush()
		ProfileKind.UNKNOWN -> error("No active profile for $kind")
	}

	private suspend fun listProfiles(kind: ProfileKind): List<String> = (profileNamesIn(storage.list(dirOf(kind))) + activeProfile(kind)).distinct().sortedBy { it.lowercase() }

	private suspend fun profileOperation(kind: ProfileKind, block: suspend () -> ProfileOperationStatus): ProfileOperationStatus = profilesLock.withLock {
		if (kind == ProfileKind.UNKNOWN) return@withLock ProfileOperationStatus.INVALID_KIND
		try {
			block()
		} catch (e: CancellationException) {
			throw e
		} catch (e: Exception) {
			AppLogger.config.error(e, "Profile operation failed")
			ProfileOperationStatus.FAILED
		}
	}

	suspend fun switchProfile(kind: ProfileKind, name: String): ProfileOperationStatus = profileOperation(kind) {
		if (name !in listProfiles(kind)) return@profileOperation ProfileOperationStatus.NOT_FOUND
		when (kind) {
			ProfileKind.SETTINGS -> {
				globalConfig.context.dispatch(GlobalConfigActions.SetSettingsProfile(name))
				settings.swap(name)
			}

			ProfileKind.USER -> {
				globalConfig.context.dispatch(GlobalConfigActions.SetUserProfile(name))
				userConfig.swap(name)
			}

			ProfileKind.UNKNOWN -> error("Cannot switch $kind")
		}
		refreshProfiles()
		ProfileOperationStatus.OK
	}

	/** Creates a profile from the defaults, or as a copy of [copyFrom]. Does not switch to it. */
	suspend fun createProfile(kind: ProfileKind, name: String, copyFrom: String?): ProfileOperationStatus = profileOperation(kind) {
		if (!isValidProfileName(name)) return@profileOperation ProfileOperationStatus.INVALID_NAME
		val names = listProfiles(kind)
		if (hasProfileNamed(names, name)) return@profileOperation ProfileOperationStatus.ALREADY_EXISTS

		val content = if (copyFrom == null) {
			when (kind) {
				ProfileKind.SETTINGS -> jsonConfig.encodeToString(SettingsConfigState())
				ProfileKind.USER -> jsonConfig.encodeToString(UserConfigData())
				ProfileKind.UNKNOWN -> error("No default profile for $kind")
			}
		} else {
			if (copyFrom !in names) return@profileOperation ProfileOperationStatus.NOT_FOUND
			if (copyFrom == activeProfile(kind)) flushActive(kind)
			storage.read(pathOf(kind, copyFrom)) ?: return@profileOperation ProfileOperationStatus.NOT_FOUND
		}
		if (!storage.write(pathOf(kind, name), content, overwrite = false)) return@profileOperation ProfileOperationStatus.ALREADY_EXISTS
		refreshProfiles()
		ProfileOperationStatus.OK
	}

	suspend fun renameProfile(kind: ProfileKind, from: String, to: String): ProfileOperationStatus = profileOperation(kind) {
		if (!isValidProfileName(to)) return@profileOperation ProfileOperationStatus.INVALID_NAME
		val names = listProfiles(kind)
		if (from !in names) return@profileOperation ProfileOperationStatus.NOT_FOUND
		if (hasProfileNamed(names, to)) return@profileOperation ProfileOperationStatus.ALREADY_EXISTS

		if (from != activeProfile(kind)) {
			storage.move(pathOf(kind, from), pathOf(kind, to))
		} else {
			when (kind) {
				ProfileKind.SETTINGS -> {
					settings.rename(to)
					globalConfig.context.dispatch(GlobalConfigActions.SetSettingsProfile(to))
				}

				ProfileKind.USER -> {
					userConfig.rename(to)
					globalConfig.context.dispatch(GlobalConfigActions.SetUserProfile(to))
				}

				ProfileKind.UNKNOWN -> error("Cannot rename $kind")
			}
		}
		refreshProfiles()
		ProfileOperationStatus.OK
	}

	suspend fun deleteProfile(kind: ProfileKind, name: String): ProfileOperationStatus = profileOperation(kind) {
		if (name !in listProfiles(kind)) return@profileOperation ProfileOperationStatus.NOT_FOUND
		if (name == activeProfile(kind)) return@profileOperation ProfileOperationStatus.IS_ACTIVE

		storage.delete(pathOf(kind, name))
		refreshProfiles()
		ProfileOperationStatus.OK
	}

	/**
	 * Switches both kinds back to the default profile and restores its default values. Other profiles are kept.
	 * A deleted default profile is created again. Known devices are cleared.
	 */
	suspend fun reset() = profilesLock.withLock {
		val defaults = GlobalConfigState()
		val settingsName = defaultProfileName(ProfileKind.SETTINGS, defaults.selectedSettingsProfile)
		if (settings.context.state.value.name != settingsName) {
			globalConfig.context.dispatch(GlobalConfigActions.SetSettingsProfile(settingsName))
			settings.swap(settingsName)
		}
		val userName = defaultProfileName(ProfileKind.USER, defaults.selectedUserProfile)
		if (userConfig.context.state.value.name != userName) {
			globalConfig.context.dispatch(GlobalConfigActions.SetUserProfile(userName))
			userConfig.swap(userName)
		}
		settings.context.dispatch(SettingsActions.Update { SettingsConfigState() })
		userConfig.context.dispatch(UserConfigActions.Update { UserConfigData() })
		devices.context.dispatch(DevicesConfigActions.Update { DevicesConfigState() })
		val errorReporting = globalConfig.context.state.value.errorReporting
		globalConfig.context.dispatch(
			GlobalConfigActions.SetErrorReporting(changeConsent(errorReporting, ErrorReportingConsent.UNDECIDED)),
		)
		refreshProfiles()
	}

	// A profile named like the default in another case is the same file on case-insensitive filesystems
	private suspend fun defaultProfileName(kind: ProfileKind, name: String): String = listProfiles(kind).firstOrNull { it.equals(name, ignoreCase = true) } ?: name

	companion object {
		suspend fun create(scope: CoroutineScope, storage: ConfigStorage): AppConfig {
			val globalConfig = GlobalConfig.create(scope, storage)

			val userConfig = UserConfig.create(scope, storage, globalConfig.context.state.value.selectedUserProfile)
			val settings = Settings.create(scope, storage, globalConfig.context.state.value.selectedSettingsProfile)
			val devices = DevicesConfig.create(scope, storage)

			return AppConfig(
				globalConfig = globalConfig,
				userConfig = userConfig,
				settings = settings,
				devices = devices,
				storage = storage,
			).also { it.refreshProfiles() }
		}
	}
}
