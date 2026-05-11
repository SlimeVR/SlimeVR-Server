package dev.slimevr.firmware

import dev.slimevr.VRServer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeoutOrNull
import solarxr_protocol.datatypes.DeviceId
import solarxr_protocol.datatypes.TrackerStatus

private fun isOnlineStatus(status: TrackerStatus): Boolean = when (status) {
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

suspend fun waitForConnected(
	server: VRServer,
	macAddress: String,
	timeoutMs: Long = 30_000,
): Boolean? = withTimeoutOrNull(timeoutMs) {
	deviceStatusFlow(server) { _, deviceMac -> deviceMac?.uppercase() == macAddress }
		.filter(::isOnlineStatus)
		.first()
	true
}

suspend fun waitForReconnected(
	server: VRServer,
	deviceId: DeviceId,
	timeoutMs: Long = 60_000,
): Boolean? = withTimeoutOrNull(timeoutMs) {
	val statuses = deviceStatusFlow(server) { id, _ -> id.toUByte() == deviceId.id }
		.distinctUntilChanged()

	if (isOnlineStatus(statuses.first())) {
		statuses.first { status -> !isOnlineStatus(status) }
	}

	statuses.first(::isOnlineStatus)
	true
}
