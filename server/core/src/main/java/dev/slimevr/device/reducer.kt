package dev.slimevr.device

fun reduce(state: DeviceState, action: DeviceActions): DeviceState {
	var nextState = when (action) {
		is DeviceActions.Update -> action.transform(state)

		is DeviceActions.PacketStats -> state.copy(
			packetsReceived = action.packetsReceived,
			packetsLost = action.packetsLost,
		)
	}

	if (nextState.signalStrength == null && nextState.packetsReceived == 0L && nextState.packetsLost == 0L) {
		return nextState
	}

	val prevSample = nextState.samples.lastOrNull()
	if (prevSample == null ||
		prevSample.rssi != nextState.signalStrength ||
		prevSample.packetsReceived != nextState.packetsReceived ||
		prevSample.packetsLost != nextState.packetsLost
	) {
		val now = System.currentTimeMillis()
		val sample = DevicePacketSample(
			time = now,
			rssi = nextState.signalStrength,
			packetsReceived = nextState.packetsReceived,
			packetsLost = nextState.packetsLost,
		)
		nextState = nextState.copy(samples = trimSamples(nextState.samples + sample, now))
	}

	return nextState
}
