package dev.slimevr.solarxr

import dev.slimevr.TestAppContext
import dev.slimevr.buildTestErrorReporting
import dev.slimevr.config.ErrorReportingConfig
import dev.slimevr.config.GlobalConfig
import dev.slimevr.config.GlobalConfigState
import dev.slimevr.config.effectiveConsent
import dev.slimevr.context.Context
import dev.slimevr.sentry.ErrorReportingManager
import dev.slimevr.solarxr.rpc.ErrorReportingSettingsBehaviour
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.MessageBundle
import solarxr_protocol.rpc.ChangeErrorReportingSettingsRequest
import solarxr_protocol.rpc.ErrorReportingConsent
import solarxr_protocol.rpc.ErrorReportingSettingsRequest
import solarxr_protocol.rpc.ErrorReportingSettingsResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import dev.slimevr.config.reduce as reduceConfig

private fun TestScope.buildGlobalConfig(errorReporting: ErrorReportingConfig): GlobalConfig {
	val context = Context.create(
		initialState = GlobalConfigState(errorReporting = errorReporting),
		scope = backgroundScope,
		reducer = ::reduceConfig,
		name = "GlobalConfig[test]",
	)
	return GlobalConfig(context).also { it.startObserving() }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun TestScope.testConn(
	id: Int,
	globalConfig: GlobalConfig,
	errorReporting: ErrorReportingManager,
): Pair<SolarXRBridge, List<ErrorReportingSettingsResponse>> {
	val context = Context.create(
		initialState = SolarXRBridgeState(dataFeedConfigs = listOf()),
		scope = backgroundScope,
		reducer = ::reduce,
		behaviours = listOf(ErrorReportingSettingsBehaviour(globalConfig, errorReporting)),
		name = "SolarXRErrorReportingTest[$id]",
	)
	val bridge = SolarXRBridge(id = id, context = context, appContext = object : TestAppContext() {})
	val responses = mutableListOf<ErrorReportingSettingsResponse>()
	bridge.outbound.on<MessageBundle> { bundle ->
		bundle.rpcMsgs.orEmpty().mapNotNull { it.message as? ErrorReportingSettingsResponse }.forEach(responses::add)
	}.launchIn(backgroundScope)
	bridge.startObserving()
	runCurrent()
	return bridge to responses
}

@OptIn(ExperimentalCoroutinesApi::class)
class ErrorReportingTest {
	private val fresh = ErrorReportingConfig(userId = "server-id")

	@Test
	fun `settings request returns the effective consent, user id and session id`() = runTest {
		val globalConfig = buildGlobalConfig(fresh.copy(consent = ErrorReportingConsent.ALLOWED, consentVersion = 1))
		val errorReporting = buildTestErrorReporting(backgroundScope, globalConfig)
		val (conn, responses) = testConn(1, globalConfig, errorReporting)

		conn.rpcDispatcher.emit(ErrorReportingSettingsRequest())
		runCurrent()

		assertEquals(
			listOf(
				ErrorReportingSettingsResponse(
					consent = ErrorReportingConsent.UNDECIDED,
					userId = "server-id",
					sessionId = errorReporting.context.state.value.sessionId,
				),
			),
			responses,
		)
	}

	@Test
	fun `a change is persisted and sent to every connected gui`() = runTest {
		val globalConfig = buildGlobalConfig(fresh)
		val errorReporting = buildTestErrorReporting(backgroundScope, globalConfig)
		val (first, firstResponses) = testConn(1, globalConfig, errorReporting)
		val (_, secondResponses) = testConn(2, globalConfig, errorReporting)

		first.rpcDispatcher.emit(ChangeErrorReportingSettingsRequest(consent = ErrorReportingConsent.ALLOWED))
		runCurrent()

		val stored = globalConfig.context.state.value.errorReporting
		assertEquals(ErrorReportingConsent.ALLOWED, stored.consent)
		assertEquals(ErrorReportingConsent.ALLOWED, effectiveConsent(stored))
		assertEquals(ErrorReportingConsent.ALLOWED, firstResponses.last().consent)
		assertEquals(ErrorReportingConsent.ALLOWED, secondResponses.last().consent)
	}
}
