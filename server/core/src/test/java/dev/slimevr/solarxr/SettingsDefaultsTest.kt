package dev.slimevr.solarxr

import dev.slimevr.TestAppContext
import dev.slimevr.context.Context
import dev.slimevr.solarxr.rpc.SettingsDefaultsBehaviour
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.MessageBundle
import solarxr_protocol.rpc.SettingsDefaultsRequest
import solarxr_protocol.rpc.SettingsDefaultsResponse
import kotlin.test.Test
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsDefaultsTest {

	@Test
	fun `defaults response fills every settings domain`() = runTest {
		val responses = mutableListOf<SettingsDefaultsResponse>()
		val bridge = buildBridge(this, responses)

		bridge.rpcDispatcher.emit(SettingsDefaultsRequest())
		runCurrent()

		val defaults = responses.single()
		assertNotNull(defaults.skeleton)
		assertNotNull(defaults.resets)
		assertNotNull(defaults.stayAligned)
		assertNotNull(defaults.tapDetection)
		assertNotNull(defaults.timeout)
		assertNotNull(defaults.hid)
		assertNotNull(defaults.driver)
		assertNotNull(defaults.vrcosc)
		assertNotNull(defaults.vmcosc)
		assertNotNull(defaults.vrm)
		assertNotNull(defaults.boneRouting)
		assertNotNull(defaults.errorReporting)
		assertNotNull(defaults.mag)
	}
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun buildBridge(scope: TestScope, responses: MutableList<SettingsDefaultsResponse>): SolarXRBridge {
	val context = Context.create(
		initialState = SolarXRBridgeState(dataFeedConfigs = listOf()),
		scope = scope.backgroundScope,
		reducer = ::reduce,
		behaviours = listOf(SettingsDefaultsBehaviour()),
		name = "SolarXRSettingsDefaultsTest",
	)
	val bridge = SolarXRBridge(id = 1, context = context, appContext = object : TestAppContext() {})
	bridge.outbound.on<MessageBundle> { bundle ->
		bundle.rpcMsgs.orEmpty().mapNotNull { it.message as? SettingsDefaultsResponse }.forEach(responses::add)
	}.launchIn(scope.backgroundScope)
	bridge.startObserving()
	scope.runCurrent()
	return bridge
}
