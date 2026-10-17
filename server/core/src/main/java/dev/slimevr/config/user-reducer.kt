package dev.slimevr.config

fun reduce(state: UserConfigState, action: UserConfigActions): UserConfigState = when (action) {
	is UserConfigActions.Update -> state.copy(data = action.transform(state.data))
	is UserConfigActions.LoadProfile -> action.newState
	is UserConfigActions.Rename -> state.copy(name = action.name)
}
