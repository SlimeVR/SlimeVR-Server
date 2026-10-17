package dev.slimevr.solarxr

fun reduce(state: SolarXRBridgeState, action: SolarXRBridgeActions): SolarXRBridgeState = when (action) {
	is SolarXRBridgeActions.SetConfig -> state.copy(dataFeedConfigs = action.configs)
	is SolarXRBridgeActions.SetDriverInfo -> state.copy(driverName = action.name, boneMask = action.boneMask)
}
