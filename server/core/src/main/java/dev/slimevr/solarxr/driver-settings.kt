package dev.slimevr.solarxr

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.ChangeDriverSettingsRequest
import solarxr_protocol.rpc.DriverSettingsRequest
import solarxr_protocol.rpc.DriverSettingsResponse

class DriverSettingsBehaviour(
	private val settings: Settings,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<DriverSettingsRequest> {
			val config = settings.context.state.value.data.driverConfig
			receiver.sendRpc(DriverSettingsResponse(sendDerivedVelocity = config.sendDerivedVelocity))
		}.launchIn(receiver.context.scope)

		settings.context.state
			.drop(1)
			.map { DriverSettingsResponse(sendDerivedVelocity = it.data.driverConfig.sendDerivedVelocity) }
			.distinctUntilChanged()
			.onEach(receiver::sendRpc)
			.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ChangeDriverSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(driverConfig = driverConfig.copy(sendDerivedVelocity = req.sendDerivedVelocity == true))
				},
			)
		}.launchIn(receiver.context.scope)
	}
}
