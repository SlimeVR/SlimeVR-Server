package dev.slimevr.solarxr.rpc

import dev.slimevr.AppContextProvider
import dev.slimevr.config.SettingsActions
import dev.slimevr.logging.AppLogger
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.udp.SensorConfigFlags
import dev.slimevr.udp.UDPConnectionActions
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.rpc.ChangeMagToggleRequest
import solarxr_protocol.rpc.MagToggleRequest
import solarxr_protocol.rpc.MagToggleResponse
import kotlin.time.Duration.Companion.seconds

class MagBehaviour(
	private val appContext: AppContextProvider,
) : SolarXRBridgeBehaviour {
	private fun setUDPTrackerMag(trackerId: Int, deviceId: Int, enable: Boolean): Boolean {
		val connection = appContext.udpServer.findConnectionForDevice(deviceId) ?: return false
		val sensorId = connection.context.state.value.trackerIds.find { it.trackerId == trackerId }?.sensorId ?: return false
		connection.context.dispatch(
			UDPConnectionActions.SetSensorConfig(
				sensorId = sensorId,
				flags = SensorConfigFlags(magStatus = if (enable) MagnetometerStatus.ENABLED else MagnetometerStatus.DISABLED),
			),
		)
		return true
	}

	override fun observe(receiver: SolarXRBridge) {
		receiver.appContext.config.settings.context.state
			.map { it.data.trackersConfig.globalMagEnabled }
			.distinctUntilChanged()
			.drop(1)
			.onEach { enable -> receiver.sendRpc(MagToggleResponse(trackerId = 0u, enable = enable)) }
			.launchIn(receiver.context.scope)

		receiver.onRpc<ChangeMagToggleRequest> { req, txId ->
			val trackerId = req.trackerId.toInt()
			val enable = req.enable

			if (trackerId == 0) {
				val settings = receiver.appContext.config.settings
				val oldTrackersConfig = settings.context.state.value.data.trackersConfig
				settings.context.dispatch(SettingsActions.Update { copy(trackersConfig = oldTrackersConfig.copy(globalMagEnabled = enable)) })

				appContext.server.context.state.value.trackers.values.forEach { tracker ->
					val state = tracker.context.state.value
					if (state.magStatus == MagnetometerStatus.NOT_SUPPORTED) return@forEach
					when (state.origin) {
						DeviceOrigin.UDP -> setUDPTrackerMag(state.id, state.deviceId, enable)
						DeviceOrigin.HID -> { /* TODO: implement HID mag toggle */ }
						else -> Unit
					}
				}
				receiver.sendRpc(MagToggleResponse(trackerId = 0u, enable = enable), txId)
				return@onRpc
			}

			val tracker = appContext.server.getTracker(trackerId) ?: return@onRpc
			val trackerState = tracker.context.state.value
			if (trackerState.magStatus == MagnetometerStatus.NOT_SUPPORTED) return@onRpc

			when (trackerState.origin) {
				DeviceOrigin.UDP -> {
					if (!setUDPTrackerMag(trackerState.id, trackerState.deviceId, enable)) return@onRpc
					tracker.context.scope.launch {
						try {
							withTimeout(10.seconds) {
								tracker.context.state
									.distinctUntilChangedBy { it.magStatus }
									.first { it.magStatus == if (enable) MagnetometerStatus.ENABLED else MagnetometerStatus.DISABLED }
								receiver.sendRpc(MagToggleResponse(trackerId = trackerId.toUShort(), enable = enable), txId)
							}
						} catch (_: TimeoutCancellationException) {
							AppLogger.solarxr.warn("Timeout waiting for mag toggle response from tracker")
						}
					}
				}

				DeviceOrigin.HID -> {
					// TODO: implement HID mag toggle
				}

				else -> return@onRpc
			}
		}.launchIn(receiver.context.scope)

		receiver.onRpc<MagToggleRequest> { req, txId ->
			val trackerId = req.trackerId.toInt()

			if (trackerId == 0) {
				receiver.sendRpc(
					MagToggleResponse(
						trackerId = 0u,
						enable = receiver.appContext.config.settings.context.state.value.data.trackersConfig.globalMagEnabled,
					),
					txId,
				)
				return@onRpc
			}

			val trackerState = appContext.server.getTracker(trackerId)?.context?.state?.value ?: return@onRpc
			receiver.sendRpc(
				MagToggleResponse(
					trackerId = trackerId.toUShort(),
					enable = trackerState.magStatus == MagnetometerStatus.ENABLED,
				),
				txId,
			)
		}.launchIn(receiver.context.scope)
	}
}
