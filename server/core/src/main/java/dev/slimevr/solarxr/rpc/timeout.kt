package dev.slimevr.solarxr.rpc

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.TrackersConfig
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.ChangeTimeoutSettingsRequest
import solarxr_protocol.rpc.TimeoutSettingsRequest
import solarxr_protocol.rpc.TimeoutSettingsResponse

internal fun buildTimeoutSettingsResponse(config: TrackersConfig) = TimeoutSettingsResponse(
	delay = config.timeoutDelay,
)

class TimeoutSettingsBehaviour(
	private val settings: Settings,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		// Send config
		receiver.rpcDispatcher.on<TimeoutSettingsRequest> {
			receiver.sendRpc(buildTimeoutSettingsResponse(settings.context.state.value.data.trackersConfig))
		}.launchIn(receiver.context.scope)

		// Push config changes, including profile switches
		settings.context.state
			.map { buildTimeoutSettingsResponse(it.data.trackersConfig) }
			.distinctUntilChanged()
			.drop(1)
			.onEach(receiver::sendRpc)
			.launchIn(receiver.context.scope)

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
