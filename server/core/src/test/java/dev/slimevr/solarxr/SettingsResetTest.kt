package dev.slimevr.solarxr

import dev.slimevr.TestAppContext
import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.buildTestAppConfig
import dev.slimevr.buildTestTracker
import dev.slimevr.buildTestVrServerStub
import dev.slimevr.config.AppConfig
import dev.slimevr.config.DevicesConfigActions
import dev.slimevr.config.GlobalConfigActions
import dev.slimevr.config.UserConfigActions
import dev.slimevr.config.changeConsent
import dev.slimevr.context.Context
import dev.slimevr.device.Device
import dev.slimevr.solarxr.rpc.SettingsResetBehaviour
import dev.slimevr.tracker.TrackerActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.rpc.ErrorReportingConsent
import solarxr_protocol.rpc.SettingsResetRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
private fun awaitReset(scope: TestScope, config: AppConfig) {
	val deadline = System.nanoTime() + 5_000_000_000L
	while (config.globalConfig.context.state.value.errorReporting.consent != ErrorReportingConsent.UNDECIDED && System.nanoTime() < deadline) {
		scope.runCurrent()
		Thread.sleep(5)
	}
	scope.runCurrent()
}

private fun buildBridge(scope: CoroutineScope, config: AppConfig, server: VRServer): SolarXRBridge {
	val appContext = object : TestAppContext() {
		override val server: VRServer = server
		override val config: AppConfig = config
	}
	val context = Context.create(
		initialState = SolarXRBridgeState(dataFeedConfigs = listOf()),
		scope = scope,
		reducer = ::reduce,
		behaviours = listOf(SettingsResetBehaviour(appContext)),
		name = "SolarXRSettingsResetTest",
	)
	return SolarXRBridge(id = 1, context = context, appContext = appContext)
}

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

		val bridge = buildBridge(backgroundScope, config, buildTestVrServerStub(backgroundScope))
		bridge.startObserving()
		runCurrent()

		bridge.rpcDispatcher.emit(SettingsResetRequest())
		awaitReset(this, config)

		val errorReporting = config.globalConfig.context.state.value.errorReporting
		assertEquals(ErrorReportingConsent.UNDECIDED, errorReporting.consent)
		assertEquals(userId, errorReporting.userId)
		assertEquals(1.6f, config.userConfig.context.state.value.data.userHeight)
	}

	@Test
	fun `reset forgets every udp device`() = runTest {
		val config = buildTestAppConfig(backgroundScope)
		val server = buildTestVrServerStub(backgroundScope)
		val bridge = buildBridge(backgroundScope, config, server)
		val mac = "AA:BB:CC:DD:EE:FF"
		config.devices.context.dispatch(DevicesConfigActions.AddAllowedUdpDevice(mac))
		val device = Device.create(
			scope = server.context.scope,
			appContext = bridge.appContext,
			id = server.nextHandle(),
			address = "127.0.0.1",
			macAddress = mac,
			hardwareId = mac,
			origin = DeviceOrigin.UDP,
			protocolVersion = 21,
		)
		server.context.dispatch(VRServerActions.NewDevice(device.context.state.value.id, device))
		val tracker = buildTestTracker(server.context.scope, bridge.appContext, config.settings, server.nextHandle())
		tracker.context.dispatch(TrackerActions.Update { copy(deviceId = device.context.state.value.id, hardwareId = "$mac:0") })
		server.context.dispatch(VRServerActions.NewTracker(tracker.context.state.value.id, tracker))
		config.devices.context.dispatch(DevicesConfigActions.UpdateTracker("$mac:0") { copy(customName = "Left foot") })
		config.globalConfig.context.dispatch(
			GlobalConfigActions.SetErrorReporting(
				changeConsent(config.globalConfig.context.state.value.errorReporting, ErrorReportingConsent.ALLOWED),
			),
		)
		bridge.startObserving()
		runCurrent()

		bridge.rpcDispatcher.emit(SettingsResetRequest())
		awaitReset(this, config)

		assertTrue(server.context.state.value.devices.isEmpty())
		assertTrue(server.context.state.value.trackers.isEmpty())
		assertTrue(config.devices.context.state.value.trackers.isEmpty())
		assertTrue(config.devices.context.state.value.allowedUdpDevices.isEmpty())
	}
}
