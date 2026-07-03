package dev.slimevr.tapdetection

class TapDetectionBasicBehaviour : TapDetectionBehaviour {
	override fun reduce(state: TapDetectionState, action: TapDetectionActions) = when (action) {
		is TapDetectionActions.SetSetupMode -> {
			state.copy(setupMode = action.setupMode)
		}
	}
}
