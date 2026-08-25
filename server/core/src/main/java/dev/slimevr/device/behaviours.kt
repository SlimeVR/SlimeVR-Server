package dev.slimevr.device

class DeviceStatsBehaviour : DeviceBehaviour {
	override fun reduce(state: DeviceState, action: DeviceActions) = when (action) {
		is DeviceActions.Update -> action.transform(state)

		is DeviceActions.PacketStats -> state.copy(
			packetsReceived = action.packetsReceived,
			packetsLost = action.packetsLost,
		)
	}

	override fun observe(receiver: Device) {}
}

class DeviceTelemetryBehaviour : DeviceBehaviour {
	override fun reduce(state: DeviceState, action: DeviceActions): DeviceState {
		if (state.signalStrength == null && state.packetsReceived == 0L && state.packetsLost == 0L) {
			return state
		}

		val prevSample = state.samples.lastOrNull()
		if (prevSample == null ||
			prevSample.rssi != state.signalStrength ||
			prevSample.packetsReceived != state.packetsReceived ||
			prevSample.packetsLost != state.packetsLost
		) {
			val now = System.currentTimeMillis()
			val sample = DevicePacketSample(
				time = now,
				rssi = state.signalStrength,
				packetsReceived = state.packetsReceived,
				packetsLost = state.packetsLost,
			)
			return state.copy(samples = trimSamples(state.samples + sample, now))
		}

		return state
	}

	override fun observe(receiver: Device) {}
}
