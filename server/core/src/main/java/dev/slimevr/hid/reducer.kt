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
					sensorCount = action.sensorCount,
				)
				),
	)

	is HIDReceiverActions.TrackerRegistered -> {
		val existing = state.trackers[action.hidId] ?: return state
		state.copy(trackers = state.trackers + (action.hidId to existing.copy(trackerId = action.trackerId)))
	}

	is HIDReceiverActions.DeviceUnregistered -> state.copy(
		trackers = state.trackers.filterValues { it.address != action.address },
	)

	is HIDReceiverActions.SetStatus -> state.copy(status = action.status)

	is HIDReceiverActions.SetProtocolVersion -> state.copy(protocolVersion = action.version)

	is HIDReceiverActions.SetCustomName -> state.copy(customName = action.customName)

	is HIDReceiverActions.UpdateDongleInfo -> action.transform(state)
}
