package dev.slimevr.solarxr

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.ChangeHIDSettingsRequest
import solarxr_protocol.rpc.HIDSettingsRequest
import solarxr_protocol.rpc.HIDSettingsResponse

class HIDSettingsBehaviour(
	private val settings: Settings,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<HIDSettingsRequest> {
			receiver.sendRpc(HIDSettingsResponse(trackersOverHid = settings.context.state.value.data.hidConfig.trackersOverHid))
		}.launchIn(receiver.context.scope)

		settings.context.state
			.drop(1)
			.map { HIDSettingsResponse(trackersOverHid = it.data.hidConfig.trackersOverHid) }
			.distinctUntilChanged()
			.onEach(receiver::sendRpc)
			.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ChangeHIDSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(hidConfig = hidConfig.copy(trackersOverHid = req.trackersOverHid == true))
				},
			)
		}.launchIn(receiver.context.scope)
	}
}
