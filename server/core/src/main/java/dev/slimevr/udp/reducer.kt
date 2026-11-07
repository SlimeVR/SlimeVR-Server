package dev.slimevr.udp

fun reduce(state: UdpServerState, action: UdpServerActions): UdpServerState = when (action) {
	is UdpServerActions.ConnectionAdded -> state.copy(connections = state.connections + (action.address to action.conn))

	is UdpServerActions.ConnectionRemoved -> state.copy(connections = state.connections - action.address)

	is UdpServerActions.UnknownDeviceSeen -> {
		val id = action.device.hardwareId
		if (id in state.ignoredHardwareIds) {
			state
		} else {
			state.copy(unknownDevices = state.unknownDevices + (id to action.device))
		}
	}

	is UdpServerActions.UnknownDeviceRemoved -> state.copy(unknownDevices = state.unknownDevices - action.hardwareId)

	is UdpServerActions.UnknownDevicesExpired -> state.copy(
		unknownDevices = state.unknownDevices.filterValues { it.lastSeen > action.cutoff },
	)

	is UdpServerActions.IgnoreUnknownDevice -> state.copy(
		unknownDevices = state.unknownDevices - action.hardwareId,
		ignoredHardwareIds = state.ignoredHardwareIds + action.hardwareId,
	)
}
