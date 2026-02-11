package dev.slimevr.config

class ConfigManager {
	private val globalConfigFile: ConfigFileHandler<GlobalConfig> = ConfigFileHandler(
		serializer = GlobalConfig.serializer(),
		currentVersion = GlobalConfig.CONFIG_VERSION,
		initDefault = { GlobalConfig() },
		migrateYamlConfig = null,
		path = "config/global.yml",
	)
	private lateinit var userConfigFile: ConfigFileHandler<UserConfig>
	private lateinit var settingsConfigFile: ConfigFileHandler<SettingsConfig>

	init {
		global.load()
		loadCurrentProfiles()
	}

	@Synchronized
	private fun loadCurrentSettingProfile() {
		val globalConfig = global.get()

		settingsConfigFile = ConfigFileHandler(
			serializer = SettingsConfig.serializer(),
			currentVersion = SettingsConfig.CONFIG_VERSION,
			initDefault = { SettingsConfig() },
			migrateYamlConfig = null,
			path = "config/settings/${globalConfig.settingsProfile}.yml",
		)

		settings.load()
	}

	@Synchronized
	private fun loadCurrentUserProfile() {
		val globalConfig = global.get()

		userConfigFile = ConfigFileHandler(
			serializer = UserConfig.serializer(),
			currentVersion = UserConfig.CONFIG_VERSION,
			initDefault = { UserConfig() },
			migrateYamlConfig = null,
			path = "config/user/${globalConfig.userProfile}.yml",
		)

		user.load()
	}

	@Synchronized
	fun loadCurrentProfiles() {
		loadCurrentSettingProfile()
		loadCurrentUserProfile()
	}

	@Synchronized
	internal fun getGlobalHandler(): ConfigFileHandler<GlobalConfig> = globalConfigFile

	@Synchronized
	internal fun getUserHandler(): ConfigFileHandler<UserConfig> = userConfigFile

	@Synchronized
	internal fun getSettingsHandler(): ConfigFileHandler<SettingsConfig> = settingsConfigFile

	@Synchronized
	fun saveAll() {
		globalConfigFile.save()
		userConfigFile.save()
		settingsConfigFile.save()
	}

	@Synchronized
	fun switchUserProfile(newProfile: String) {
		global.updateAndSave { it.copy(userProfile = newProfile) }
		loadCurrentUserProfile()
	}

	@Synchronized
	fun switchSettingsProfile(newProfile: String) {
		global.updateAndSave { it.copy(settingsProfile = newProfile) }
		loadCurrentSettingProfile()

		settings.updateAndSave {
			it.copy(
				keybindings = it.keybindings.copy(fullResetBinding = "CTRL+J"),
			)
		}
	}

	val global: ConfigFileHandler<GlobalConfig>
		get() = getGlobalHandler()

	val user: ConfigFileHandler<UserConfig>
		get() = getUserHandler()

	val settings: ConfigFileHandler<SettingsConfig>
		get() = getSettingsHandler()
}
