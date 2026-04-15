package dev.slimevr.solarxr

import dev.slimevr.AppContextProvider
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.udp.SensorConfigFlags
import dev.slimevr.udp.UDPConnectionActions
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.DeviceId
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.datatypes.TrackerId
import solarxr_protocol.rpc.ChangeMagToggleRequest
import solarxr_protocol.rpc.MagToggleResponse

class MagBehaviour(
	private val appContext: AppContextProvider,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		val subscribedTrackers = mutableSetOf<Int>()
		appContext.server.context.state
			.distinctUntilChangedBy { it.trackers.keys }
			.onEach { serverState ->
				serverState.trackers.values.forEach { tracker ->
					val trackerState = tracker.context.state.value
					if (subscribedTrackers.add(trackerState.id)) {
						val device = appContext.server.getDevice(trackerState.deviceId) ?: return@forEach
						val trackerId = TrackerId(
							trackerNum = trackerState.id.toUByte(),
							deviceId = DeviceId(device.context.state.value.id.toUByte()),
						)
						tracker.context.state
							.distinctUntilChangedBy { it.magStatus }
							.onEach { state ->
								receiver.sendRpc(
									MagToggleResponse(trackerId = trackerId, enable = state.magStatus == MagnetometerStatus.ENABLED),
								)
							}
							.launchIn(receiver.context.scope)
					}
				}
			}
			.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ChangeMagToggleRequest> { req ->
			val trackerId = req.trackerId ?: return@on
			val tracker = appContext.server.getTracker(trackerId.trackerNum.toInt()) ?: return@on
			val trackerState = tracker.context.state.value

			if (trackerState.magStatus == MagnetometerStatus.NOT_SUPPORTED) return@on

			when (trackerState.origin) {
				DeviceOrigin.UDP -> {
					val connection = appContext.udpServer.findConnectionForDevice(trackerState.deviceId) ?: return@on
					val sensorId = connection.context.state.value.trackerIds
						.find { it.id == trackerState.id }
						?.trackerNum
						?: return@on
					connection.context.dispatch(
						UDPConnectionActions.SetSensorConfig(sensorId = sensorId, flags = SensorConfigFlags(magStatus = if (req.enable) MagnetometerStatus.ENABLED else MagnetometerStatus.DISABLED)),
					)
				}
				DeviceOrigin.HID -> {
					// TODO: implement HID mag toggle
				}
				else -> return@on
			}
		}
	}
}
