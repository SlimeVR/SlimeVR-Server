package dev.slimevr.udp

fun reduce(state: UDPConnectionState, action: UDPConnectionActions): UDPConnectionState = when (action) {
	is UDPConnectionActions.LastPacket -> {
		var newState = state.copy(lastPacket = action.time)
		if (action.packetNum != null) newState = newState.copy(lastPacketNum = action.packetNum)
		newState
	}

	is UDPConnectionActions.StartPing -> state.copy(lastPing = state.lastPing.copy(startTime = action.startTime, id = action.pingId))

	is UDPConnectionActions.Handshake -> state.copy(didHandshake = true, lastHandshake = System.currentTimeMillis(), deviceId = action.deviceId)

	is UDPConnectionActions.AssignTracker -> state.copy(trackerIds = state.trackerIds + action.trackerId)

	is UDPConnectionActions.FirmwareFeatures -> state.copy(features = action.features)

	is UDPConnectionActions.SetSensorConfig -> state.copy(
		sensorConfigFlags = state.sensorConfigFlags + (action.sensorId to action.flags),
	)

	is UDPConnectionActions.TimedOut -> state
}
