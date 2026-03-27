package dev.slimevr.config

object DefaultGlobalConfigBehaviour : GlobalConfigBehaviour {
	override fun reduce(state: GlobalConfigState, action: GlobalConfigActions) = when (action) {
		is GlobalConfigActions.SetUserProfile -> state.copy(selectedUserProfile = action.name)
		is GlobalConfigActions.SetSettingsProfile -> state.copy(selectedSettingsProfile = action.name)
	}
}

object DefaultSettingsBehaviour : SettingsBehaviour {
	override fun reduce(state: SettingsState, action: SettingsActions) = when (action) {
		is SettingsActions.Update -> state.copy(data = action.transform(state.data))
		is SettingsActions.LoadProfile -> action.newState
	}
}

object DefaultUserBehaviour : UserConfigBehaviour {
	override fun reduce(state: UserConfigState, action: UserConfigActions) = when (action) {
		is UserConfigActions.Update -> state.copy(data = action.transform(state.data))
		is UserConfigActions.LoadProfile -> action.newState
	}
}