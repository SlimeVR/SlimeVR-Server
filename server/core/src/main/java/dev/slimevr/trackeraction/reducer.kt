package dev.slimevr.trackeraction

fun reduce(state: TrackerActionState, action: TrackerActionActions): TrackerActionState = when (action) {
	is TrackerActionActions.SetSetupMode -> state.copy(setupMode = action.setupMode)
	is TrackerActionActions.UpdateBodyParts -> state.copy(yawResetBodyPart = action.yawResetBodyPart, fullResetBodyPart = action.fullResetBodyPart, mountingResetBodyPart = action.mountingResetBodyPart)
}
