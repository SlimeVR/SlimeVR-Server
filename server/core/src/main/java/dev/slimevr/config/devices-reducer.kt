package dev.slimevr.config

fun reduce(state: DevicesConfigState, action: DevicesConfigActions): DevicesConfigState = when (action) {
	is DevicesConfigActions.Update -> action.transform(state)

	is DevicesConfigActions.UpdateTracker -> {
		val existing = state.trackers[action.hardwareId] ?: TrackerConfig()
		state.copy(trackers = state.trackers + (action.hardwareId to action.transform(existing)))
	}

	is DevicesConfigActions.UpdateDongle -> {
		val existing = state.dongles[action.serialNumber] ?: DongleConfig()
		state.copy(dongles = state.dongles + (action.serialNumber to action.transform(existing)))
	}

	is DevicesConfigActions.AddAllowedUdpDevice -> state.copy(allowedUdpDevices = state.allowedUdpDevices + action.mac)

	is DevicesConfigActions.RemoveAllowedUdpDevice -> state.copy(allowedUdpDevices = state.allowedUdpDevices - action.mac)
}
