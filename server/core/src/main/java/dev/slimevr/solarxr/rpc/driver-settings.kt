package dev.slimevr.solarxr.rpc

import dev.slimevr.AppContextProvider
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.routing.driverStateFlow
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.ChangeDriverSettingsRequest
import solarxr_protocol.rpc.DriverConnectionState
import solarxr_protocol.rpc.DriverSettingsRequest
import solarxr_protocol.rpc.DriverSettingsResponse
import solarxr_protocol.rpc.DriverStatusChangeResponse
import solarxr_protocol.rpc.DriverStatusRequest
import solarxr_protocol.rpc.RoutingOutputState

class DriverSettingsBehaviour(
	private val appContext: AppContextProvider,
) : SolarXRBridgeBehaviour {
	private val settings: Settings get() = appContext.config.settings

	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<DriverSettingsRequest> {
			val config = settings.context.state.value.data.driverConfig
			receiver.sendRpc(
				DriverSettingsResponse(enabled = config.enabled),
			)
		}.launchIn(receiver.context.scope)

		settings.context.state
			.drop(1)
			.map {
				DriverSettingsResponse(enabled = it.data.driverConfig.enabled)
			}
			.distinctUntilChanged()
			.onEach(receiver::sendRpc)
			.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ChangeDriverSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						driverConfig = driverConfig.copy(enabled = req.enabled),
					)
				},
			)
		}.launchIn(receiver.context.scope)

		val status = driverStateFlow(appContext).map { state ->
			when (state) {
				RoutingOutputState.UNSUPPORTED -> DriverConnectionState.UNSUPPORTED
				RoutingOutputState.INACTIVE -> DriverConnectionState.DISABLED
				RoutingOutputState.ENABLED -> DriverConnectionState.WAITING
				RoutingOutputState.ACTIVE -> DriverConnectionState.CONNECTED
			}
		}

		receiver.rpcDispatcher.on<DriverStatusRequest> {
			receiver.sendRpc(DriverStatusChangeResponse(state = status.first()))
		}.launchIn(receiver.context.scope)

		status
			.drop(1)
			.onEach { receiver.sendRpc(DriverStatusChangeResponse(state = it)) }
			.launchIn(receiver.context.scope)
	}
}
