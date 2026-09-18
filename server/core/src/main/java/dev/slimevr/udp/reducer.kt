package dev.slimevr.udp

fun reduce(state: UdpServerState, action: UdpServerActions): UdpServerState = when (action) {
	is UdpServerActions.ConnectionAdded -> state.copy(connections = state.connections + (action.address to action.conn))
	is UdpServerActions.ConnectionRemoved -> state.copy(connections = state.connections - action.address)
}
