package dev.slimevr.solarxr

import dev.slimevr.TestAppContext
import dev.slimevr.buildTestAppConfig
import dev.slimevr.config.GlobalConfigActions
import dev.slimevr.config.UserConfigActions
import dev.slimevr.config.changeConsent
import dev.slimevr.context.Context
import dev.slimevr.solarxr.rpc.SettingsResetBehaviour
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.rpc.ErrorReportingConsent
import solarxr_protocol.rpc.SettingsResetRequest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsResetTest {
	@Test
	fun `reset asks for error reporting consent again and resets the user profile`() = runTest {
		val config = buildTestAppConfig(backgroundScope)
		val userId = config.globalConfig.context.state.value.errorReporting.userId
		config.globalConfig.context.dispatch(
			GlobalConfigActions.SetErrorReporting(
				changeConsent(config.globalConfig.context.state.value.errorReporting, ErrorReportingConsent.ALLOWED),
			),
		)
		config.userConfig.context.dispatch(UserConfigActions.Update { copy(userHeight = 2f) })

		val context = Context.create(
			initialState = SolarXRBridgeState(dataFeedConfigs = listOf()),
			scope = backgroundScope,
			reducer = ::reduce,
			behaviours = listOf(SettingsResetBehaviour(config)),
			name = "SolarXRSettingsResetTest",
		)
		val bridge = SolarXRBridge(id = 1, context = context, appContext = object : TestAppContext() {})
		bridge.startObserving()
		runCurrent()

		bridge.rpcDispatcher.emit(SettingsResetRequest())
		runCurrent()

		val errorReporting = config.globalConfig.context.state.value.errorReporting
		assertEquals(ErrorReportingConsent.UNDECIDED, errorReporting.consent)
		assertEquals(userId, errorReporting.userId)
		assertEquals(1.6f, config.userConfig.context.state.value.data.userHeight)
	}
}
