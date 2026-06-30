package dev.slimevr.config

class DefaultGlobalConfigBehaviour : GlobalConfigBehaviour {
	override fun reduce(state: GlobalConfigState, action: GlobalConfigActions) = when (action) {
		is GlobalConfigActions.SetUserProfile -> state.copy(selectedUserProfile = action.name)
		is GlobalConfigActions.SetSettingsProfile -> state.copy(selectedSettingsProfile = action.name)
	}
}

class DefaultSettingsBehaviour : SettingsBehaviour {
	override fun reduce(state: SettingsState, action: SettingsActions) = when (action) {
		is SettingsActions.Update -> state.copy(data = action.transform(state.data))

		is SettingsActions.LoadProfile -> action.newState

		is SettingsActions.UpdateTracker -> {
			val existing = state.data.trackers[action.hardwareId] ?: TrackerConfig()
			state.copy(data = state.data.copy(trackers = state.data.trackers + (action.hardwareId to action.transform(existing))))
		}

		is SettingsActions.AddAllowedUdpDevice -> state.copy(data = state.data.copy(allowedUdpDevices = state.data.allowedUdpDevices + action.mac))

		is SettingsActions.RemoveAllowedUdpDevice -> state.copy(data = state.data.copy(allowedUdpDevices = state.data.allowedUdpDevices - action.mac))
	}
}

class DefaultUserBehaviour : UserConfigBehaviour {
	override fun reduce(state: UserConfigState, action: UserConfigActions) = when (action) {
		is UserConfigActions.Update -> state.copy(data = action.transform(state.data))
		is UserConfigActions.LoadProfile -> action.newState
	}
}
