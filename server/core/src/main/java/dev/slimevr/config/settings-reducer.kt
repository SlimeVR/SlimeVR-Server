package dev.slimevr.config

fun reduce(state: SettingsState, action: SettingsActions): SettingsState = when (action) {
	is SettingsActions.Update -> state.copy(data = action.transform(state.data))
	is SettingsActions.LoadProfile -> action.newState
	is SettingsActions.Rename -> state.copy(name = action.name)
}
