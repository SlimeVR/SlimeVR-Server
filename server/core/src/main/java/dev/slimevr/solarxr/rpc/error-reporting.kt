package dev.slimevr.solarxr.rpc

import dev.slimevr.config.GlobalConfig
import dev.slimevr.config.GlobalConfigActions
import dev.slimevr.config.changeConsent
import dev.slimevr.sentry.ErrorReportingManager
import dev.slimevr.sentry.ErrorReportingState
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.ChangeErrorReportingSettingsRequest
import solarxr_protocol.rpc.ErrorReportingSettingsRequest
import solarxr_protocol.rpc.ErrorReportingSettingsResponse

private fun buildResponse(state: ErrorReportingState) = ErrorReportingSettingsResponse(
	consent = state.consent,
	userId = state.userId,
	sessionId = state.sessionId,
)

class ErrorReportingSettingsBehaviour(
	private val globalConfig: GlobalConfig,
	private val errorReporting: ErrorReportingManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<ErrorReportingSettingsRequest> {
			receiver.sendRpc(buildResponse(errorReporting.context.state.value))
		}.launchIn(receiver.context.scope)

		errorReporting.context.state
			.drop(1)
			.map(::buildResponse)
			.distinctUntilChanged()
			.onEach(receiver::sendRpc)
			.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<ChangeErrorReportingSettingsRequest> { req ->
			val current = globalConfig.context.state.value.errorReporting
			globalConfig.context.dispatch(GlobalConfigActions.SetErrorReporting(changeConsent(current, req.consent)))
		}.launchIn(receiver.context.scope)
	}
}
