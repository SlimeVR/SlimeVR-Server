package dev.slimevr.tapdetection

fun reduce(state: TapDetectionState, action: TapDetectionActions): TapDetectionState = when (action) {
	is TapDetectionActions.SetSetupMode -> state.copy(setupMode = action.setupMode)
}
