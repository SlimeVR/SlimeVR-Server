package dev.slimevr.stepmounting

fun reduce(state: StepMountingState, action: StepMountingActions): StepMountingState = when (action) {
	is StepMountingActions.Update -> state.copy(
		status = action.status,
	)

	is StepMountingActions.SetCanCalibrate -> state.copy(
		canDoStepMounting = action.canDo,
	)
}
