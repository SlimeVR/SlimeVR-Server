package dev.slimevr.udp

fun reduce(state: UdpServerState, action: UdpServerActions): UdpServerState = when (action) {
	is UdpServerActions.ConnectionAdded -> state.copy(connections = state.connections + (action.address to action.conn))

	is UdpServerActions.ConnectionRemoved -> state.copy(connections = state.connections - action.address)

	is UdpServerActions.UnknownDeviceSeen -> {
		val mac = action.device.macAddress
		if (mac in state.ignoredMacs) {
			state
		} else {
			state.copy(unknownDevices = state.unknownDevices + (mac to action.device))
		}
	}

	is UdpServerActions.UnknownDeviceRemoved -> state.copy(unknownDevices = state.unknownDevices - action.macAddress)

	is UdpServerActions.UnknownDevicesExpired -> state.copy(
		unknownDevices = state.unknownDevices.filterValues { it.lastSeen > action.cutoff },
	)

	is UdpServerActions.IgnoreUnknownDevice -> state.copy(
		unknownDevices = state.unknownDevices - action.macAddress,
		ignoredMacs = state.ignoredMacs + action.macAddress,
	)
}
