package dev.slimevr.tapdetection

fun reduce(state: TapDetectionState, action: TapDetectionActions): TapDetectionState = when (action) {
	is TapDetectionActions.SetSetupMode -> state.copy(setupMode = action.setupMode)
	is TapDetectionActions.UpdateBodyParts -> state.copy(yawResetBodyPart = action.yawResetBodyPart, fullResetBodyPart = action.fullResetBodyPart, mountingResetBodyPart = action.mountingResetBodyPart)
}
