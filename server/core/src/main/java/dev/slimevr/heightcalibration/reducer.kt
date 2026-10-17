package dev.slimevr.heightcalibration

fun reduce(state: HeightCalibrationState, action: HeightCalibrationActions): HeightCalibrationState = when (action) {
	is HeightCalibrationActions.Update -> state.copy(
		status = action.status,
		currentHeight = action.currentHeight,
	)

	is HeightCalibrationActions.SetCanCalibrate -> state.copy(
		canDoUserHeightCalibration = action.canDo,
	)
}
