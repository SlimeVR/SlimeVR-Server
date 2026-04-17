package dev.slimevr.solarxr

import dev.slimevr.AppContextProvider
import dev.slimevr.config.SettingsActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.udp.SensorConfigFlags
import dev.slimevr.udp.UDPConnectionActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import solarxr_protocol.datatypes.DeviceId
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.datatypes.TrackerId
import solarxr_protocol.rpc.ChangeMagToggleRequest
import solarxr_protocol.rpc.ChangeSettingsRequest
import solarxr_protocol.rpc.MagToggleRequest
import solarxr_protocol.rpc.MagToggleResponse

class MagBehaviour(
	private val appContext: AppContextProvider,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {

		receiver.rpcDispatcher.on<ChangeMagToggleRequest> { req ->
			val trackerId = req.trackerId

			if (trackerId == null) {
				receiver.appContext.config.settings.context.dispatch(
					SettingsActions.Update {
						copy(globalMagEnabled = req.enable)
					}
				)
				receiver.sendRpc(MagToggleResponse(
					trackerId = null,
					enable = req.enable
				))
				return@on
			}

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
						UDPConnectionActions.SetSensorConfig(
							sensorId = sensorId,
							flags = SensorConfigFlags(magEnabled = req.enable)
						),
					)

					tracker.context.scope.launch {
						withTimeout(10_000) {
							tracker.context.state
								.distinctUntilChangedBy { it.magStatus }
								.first { trackerState ->
									val status = when (trackerState.magStatus) {
										MagnetometerStatus.ENABLED -> true
										MagnetometerStatus.DISABLED -> false
										MagnetometerStatus.NOT_SUPPORTED -> false
									}
									return@first status == req.enable
								}
							receiver.sendRpc(
								MagToggleResponse(
									trackerId = trackerId,
									enable = req.enable
								)
							)
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
				val globalMagEnabled = receiver.appContext.config.settings.context.state.value.data.globalMagEnabled
				receiver.sendRpc(MagToggleResponse(
					trackerId = null,
					enable = globalMagEnabled
				))
				return@on
			}

			val tracker = appContext.server.getTracker(trackerId.trackerNum.toInt()) ?: return@on
			val trackerState = tracker.context.state.value
			receiver.sendRpc(MagToggleResponse(
				trackerId = trackerId,
				enable = when (trackerState.magStatus) {
					MagnetometerStatus.ENABLED -> true
					MagnetometerStatus.DISABLED -> false
					MagnetometerStatus.NOT_SUPPORTED -> false
				}
			))
		}
	}
}
