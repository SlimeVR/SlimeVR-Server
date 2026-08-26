package dev.slimevr.hid

fun reduce(state: HIDReceiverState, action: HIDReceiverActions): HIDReceiverState = when (action) {
	is HIDReceiverActions.DeviceRegistered -> state.copy(
		trackers = state.trackers +
			(
				action.hidId to HIDTrackerRecord(
					hidId = action.hidId,
					address = action.address,
					deviceId = action.deviceId,
					trackerId = null,
				)
				),
	)

	is HIDReceiverActions.TrackerRegistered -> {
		val existing = state.trackers[action.hidId] ?: return state
		state.copy(trackers = state.trackers + (action.hidId to existing.copy(trackerId = action.trackerId)))
	}

	is HIDReceiverActions.SetStatus -> state.copy(status = action.status)

	is HIDReceiverActions.SetCustomName -> state.copy(customName = action.customName)
}
