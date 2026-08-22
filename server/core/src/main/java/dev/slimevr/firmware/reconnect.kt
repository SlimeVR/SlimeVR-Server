package dev.slimevr.firmware

import dev.slimevr.VRServer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeoutOrNull
import solarxr_protocol.datatypes.TrackerStatus

fun isOnlineStatus(status: TrackerStatus): Boolean = when (status) {
	TrackerStatus.NONE,
	TrackerStatus.DISCONNECTED,
	TrackerStatus.TIMED_OUT,
	-> false

	else -> true
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun deviceStatusFlow(
	server: VRServer,
	matches: (Int, String?) -> Boolean,
) = server.context.state.flatMapLatest { state ->
	val device = state.devices.values.find { device ->
		val deviceState = device.context.state.value
		matches(deviceState.id, deviceState.macAddress)
	}
	device?.context?.state?.map { it.status } ?: flowOf(TrackerStatus.DISCONNECTED)
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun deviceAndConnectionStateFlow(
	server: VRServer,
	mac: String,
) = server.context.state.flatMapLatest { serverState ->
	val device = serverState.devices.values.filter { d ->
		d.context.state.value.macAddress?.uppercase() == mac
	}.lastOrNull()
	if (device == null) {
		flowOf(Pair(TrackerStatus.DISCONNECTED, null))
	} else {
		device.appContext.udpServer.context.state.flatMapLatest { udpState ->
			val conn = udpState.connections.values.find { c ->
				c.context.state.value.deviceId == device.context.state.value.id
			}
			if (conn == null) {
				device.context.state.map { dState -> Pair(dState.status, null) }
			} else {
				combine(device.context.state, conn.context.state) { dState, cState ->
					Pair(dState.status, cState)
				}
			}
		}
	}
}

suspend fun waitForConnected(
	server: VRServer,
	macAddress: String,
	minPacketTime: Long = 0L,
	timeoutMs: Long = 30_000,
): Boolean? = withTimeoutOrNull(timeoutMs) {
	val mac = macAddress.uppercase()
	deviceAndConnectionStateFlow(server, mac).first { (status, connState) ->
		if (!isOnlineStatus(status)) return@first false
		if (minPacketTime <= 0L) return@first true
		if (connState == null) return@first false

		connState.lastHandshake >= minPacketTime && connState.lastPacket >= minPacketTime
	}
	true
}

suspend fun waitForReconnected(
	server: VRServer,
	deviceId: UShort,
	timeoutMs: Long = 60_000,
): Boolean? = withTimeoutOrNull(timeoutMs) {
	val statuses = deviceStatusFlow(server) { id, _ -> id.toUShort() == deviceId }
		.distinctUntilChanged()

	if (isOnlineStatus(statuses.first())) {
		statuses.first { status -> !isOnlineStatus(status) }
	}

	statuses.first(::isOnlineStatus)
	true
}
