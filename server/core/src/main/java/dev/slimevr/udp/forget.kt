package dev.slimevr.udp

import dev.slimevr.AppContextProvider
import dev.slimevr.VRServerActions
import dev.slimevr.config.DevicesConfigActions
import dev.slimevr.device.Device
import dev.slimevr.logging.AppLogger
import dev.slimevr.skeleton.SkeletonActions
import dev.slimevr.util.isActive
import kotlinx.coroutines.cancel
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus

private fun findUdpDevices(appContext: AppContextProvider, predicate: (String) -> Boolean): List<Device> = appContext.server.context.state.value.devices.values.filter { device ->
	val state = device.context.state.value
	val hardwareId = state.hardwareId
	state.origin == DeviceOrigin.UDP && hardwareId != null && predicate(hardwareId)
}

/**
 * Disconnects a UDP device and removes it, its trackers and their saved config from the server.
 * Leaves the allowed list alone.
 */
fun removeUdpDevice(appContext: AppContextProvider, device: Device) {
	val server = appContext.server
	val deviceId = device.context.state.value.id

	appContext.udpServer.findConnectionForDevice(deviceId)?.let { conn ->
		appContext.udpServer.removeConnection(conn.context.state.value.address)
	}

	val deviceTrackers = server.context.state.value.trackers.values
		.filter { it.context.state.value.deviceId == deviceId }
	val trackerStates = deviceTrackers.map { it.context.state.value }

	// The tracker's own skeleton behaviour dies with its scope, so release its bone here
	trackerStates
		.filter { it.status.isActive() || it.status == TrackerStatus.OCCLUDED }
		.mapNotNull { it.bodyPart }
		.forEach { bodyPart ->
			appContext.skeleton.context.dispatch(SkeletonActions.DisableBone(bodyPart))
		}
	deviceTrackers.forEach { it.context.scope.cancel() }
	device.context.scope.cancel()

	server.context.dispatch(VRServerActions.DeviceRemoved(deviceId, trackerStates.map { it.id }))

	// After the scopes are cancelled, so the tracker config behaviour can't write it back
	val hardwareIds = trackerStates.map { it.hardwareId }.toSet()
	appContext.config.devices.context.dispatch(DevicesConfigActions.Update { copy(trackers = trackers - hardwareIds) })
}

/** Drops a hardware ID from the allowed list and removes its UDP device from the server. */
suspend fun forgetUdpDevice(appContext: AppContextProvider, hardwareId: String) {
	// Removed first so a handshake arriving mid-forget gets rejected instead of recreating the device
	appContext.config.devices.context.dispatch(DevicesConfigActions.RemoveAllowedUdpDevice(hardwareId))
	findUdpDevices(appContext) { it == hardwareId }.forEach { removeUdpDevice(appContext, it) }
	AppLogger.udp.info("Forgot device $hardwareId")
}

suspend fun forgetAllUdpDevices(appContext: AppContextProvider) {
	findUdpDevices(appContext) { true }
		.mapNotNull { it.context.state.value.hardwareId }
		.forEach { hardwareId -> forgetUdpDevice(appContext, hardwareId) }
}
