package dev.slimevr.solarxr

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import solarxr_protocol.rpc.AddUnknownDeviceRequest
import solarxr_protocol.rpc.ForgetDeviceRequest

class KnownTrackersBehaviour(
	private val settings: Settings,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<AddUnknownDeviceRequest> { req ->
			val mac = req.macAddress ?: return@on
			settings.context.dispatch(SettingsActions.AddAllowedUdpDevice(mac))
		}

		receiver.rpcDispatcher.on<ForgetDeviceRequest> { req ->
			val mac = req.macAddress ?: return@on
			settings.context.dispatch(SettingsActions.RemoveAllowedUdpDevice(mac))
		}
	}
}
