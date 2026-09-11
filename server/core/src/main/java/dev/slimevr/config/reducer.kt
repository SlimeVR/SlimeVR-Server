package dev.slimevr.config

fun reduce(state: GlobalConfigState, action: GlobalConfigActions): GlobalConfigState = when (action) {
	is GlobalConfigActions.SetUserProfile -> state.copy(selectedUserProfile = action.name)
	is GlobalConfigActions.SetSettingsProfile -> state.copy(selectedSettingsProfile = action.name)
}
