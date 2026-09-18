package dev.slimevr.solarxr.rpc

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import solarxr_protocol.rpc.ChangeTimeoutSettingsRequest
import solarxr_protocol.rpc.TimeoutSettingsRequest
import solarxr_protocol.rpc.TimeoutSettingsResponse

class TimeoutSettingsBehaviour(
	private val settings: Settings,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		// Send config
		receiver.rpcDispatcher.on<TimeoutSettingsRequest> {
			val config = settings.context.state.value.data.trackersConfig
			receiver.sendRpc(
				TimeoutSettingsResponse(
					delay = config.timeoutDelay,
				),
			)
		}.launchIn(receiver.context.scope)

		// Receive config
		receiver.rpcDispatcher.on<ChangeTimeoutSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						trackersConfig = trackersConfig.copy(
							timeoutDelay = req.delay,
						),
					)
				},
			)
		}.launchIn(receiver.context.scope)
	}
}
