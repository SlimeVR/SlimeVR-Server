package dev.slimevr.device

fun reduce(state: DeviceState, action: DeviceActions): DeviceState = when (action) {
	is DeviceActions.Update -> action.transform(state)
}
