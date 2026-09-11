package dev.slimevr.config

fun reduce(state: SettingsState, action: SettingsActions): SettingsState = when (action) {
	is SettingsActions.Update -> state.copy(data = action.transform(state.data))

	is SettingsActions.LoadProfile -> action.newState

	is SettingsActions.UpdateTracker -> {
		val existing = state.data.trackers[action.hardwareId] ?: TrackerConfig()
		state.copy(data = state.data.copy(trackers = state.data.trackers + (action.hardwareId to action.transform(existing))))
	}

	is SettingsActions.UpdateDongle -> {
		val existing = state.data.dongles[action.serialNumber] ?: DongleConfig()
		state.copy(data = state.data.copy(dongles = state.data.dongles + (action.serialNumber to action.transform(existing))))
	}

	is SettingsActions.AddAllowedUdpDevice -> state.copy(data = state.data.copy(allowedUdpDevices = state.data.allowedUdpDevices + action.mac))

	is SettingsActions.RemoveAllowedUdpDevice -> state.copy(data = state.data.copy(allowedUdpDevices = state.data.allowedUdpDevices - action.mac))
}
