package dev.slimevr.solarxr

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.TimeoutConfig
import kotlinx.coroutines.flow.launchIn
import solarxr_protocol.rpc.ChangeTimeoutSettingsRequest
import solarxr_protocol.rpc.TimeoutSettingsRequest
import solarxr_protocol.rpc.TimeoutSettingsResponse

class TimeoutSettingsBehaviour(
	private val settings: Settings,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		// Send config
		receiver.rpcDispatcher.on<TimeoutSettingsRequest> {
			val config = settings.context.state.value.data.timeoutConfig
			receiver.sendRpc(
				TimeoutSettingsResponse(
					duration = config.duration,
				),
			)
		}.launchIn(receiver.context.scope)

		// Receive config
		receiver.rpcDispatcher.on<ChangeTimeoutSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						timeoutConfig = TimeoutConfig(
							duration = req.duration,
						),
					)
				},
			)
		}.launchIn(receiver.context.scope)
	}
}
