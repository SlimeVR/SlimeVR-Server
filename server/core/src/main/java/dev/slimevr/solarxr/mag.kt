package dev.slimevr.solarxr

import dev.slimevr.AppContextProvider
import dev.slimevr.config.SettingsActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.udp.SensorConfigFlags
import dev.slimevr.udp.UDPConnectionActions
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.rpc.ChangeMagToggleRequest
import solarxr_protocol.rpc.MagToggleRequest
import solarxr_protocol.rpc.MagToggleResponse
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MagBehaviour(
	private val appContext: AppContextProvider,
) : SolarXRBridgeBehaviour {
	private fun setUDPTrackerMag(trackerId: Int, deviceId: Int, enable: Boolean): Boolean {
		val connection = appContext.udpServer.findConnectionForDevice(deviceId) ?: return false
		val sensorId = connection.context.state.value.trackerIds.find { it.id == trackerId }?.trackerNum ?: return false
		connection.context.dispatch(
			UDPConnectionActions.SetSensorConfig(
				sensorId = sensorId,
				flags = SensorConfigFlags(magStatus = if (enable) MagnetometerStatus.ENABLED else MagnetometerStatus.DISABLED),
			),
		)
		return true
	}

	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<ChangeMagToggleRequest> { req ->
			val trackerId = req.trackerId
			val enable = req.enable == true;

			if (trackerId == null) {
				receiver.appContext.config.settings.context.dispatch(SettingsActions.Update { copy(globalMagEnabled = enable) })
				appContext.server.context.state.value.trackers.values.forEach { tracker ->
					val state = tracker.context.state.value
					if (state.magStatus == MagnetometerStatus.NOT_SUPPORTED) return@forEach
					when (state.origin) {
						DeviceOrigin.UDP -> setUDPTrackerMag(state.id, state.deviceId, enable)
						DeviceOrigin.HID -> { /* TODO: implement HID mag toggle */ }
						else -> Unit
					}
				}
				receiver.sendRpc(MagToggleResponse(trackerId = null, enable = enable))
				return@on
			}

			val trackerNum = trackerId.trackerNum ?: return@on
			val tracker = appContext.server.getTracker(trackerNum.toInt()) ?: return@on
			val trackerState = tracker.context.state.value
			if (trackerState.magStatus == MagnetometerStatus.NOT_SUPPORTED) return@on

			when (trackerState.origin) {
				DeviceOrigin.UDP -> {
					if (!setUDPTrackerMag(trackerState.id, trackerState.deviceId, enable)) return@on
					tracker.context.scope.launch {
						withTimeout(10.seconds) {
							tracker.context.state
								.distinctUntilChangedBy { it.magStatus }
								.first { it.magStatus == if (enable) MagnetometerStatus.ENABLED else MagnetometerStatus.DISABLED }
							receiver.sendRpc(MagToggleResponse(trackerId = trackerId, enable = enable))
						}
					}
				}

				DeviceOrigin.HID -> {
					// TODO: implement HID mag toggle
				}

				else -> return@on
			}
		}

		receiver.rpcDispatcher.on<MagToggleRequest> { req ->
			val trackerId = req.trackerId

			if (trackerId == null) {
				receiver.sendRpc(
					MagToggleResponse(
						trackerId = null,
						enable = receiver.appContext.config.settings.context.state.value.data.globalMagEnabled,
					),
				)
				return@on
			}

			val trackerNum = trackerId.trackerNum ?: return@on
			val trackerState = appContext.server.getTracker(trackerNum.toInt())?.context?.state?.value ?: return@on
			receiver.sendRpc(
				MagToggleResponse(
					trackerId = trackerId,
					enable = trackerState.magStatus == MagnetometerStatus.ENABLED,
				),
			)
		}
	}
}
