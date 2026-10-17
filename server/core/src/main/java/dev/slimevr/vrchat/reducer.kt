package dev.slimevr.vrchat

fun reduce(state: VRCConfigState, action: VRCConfigActions): VRCConfigState = when (action) {
	is VRCConfigActions.UpdateValues -> state.copy(currentValues = action.values)

	is VRCConfigActions.ToggleMutedWarning -> {
		if (action.key !in VRC_VALID_KEYS) {
			state
		} else if (action.key in state.mutedWarnings) {
			state.copy(mutedWarnings = state.mutedWarnings - action.key)
		} else {
			state.copy(mutedWarnings = state.mutedWarnings + action.key)
		}
	}
}
