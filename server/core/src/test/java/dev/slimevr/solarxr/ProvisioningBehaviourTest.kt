package dev.slimevr.solarxr

import dev.slimevr.TestAppContext
import dev.slimevr.TestSerial
import dev.slimevr.VRServer
import dev.slimevr.buildTestSerial
import dev.slimevr.buildTestSettings
import dev.slimevr.buildTestVrServerStub
import dev.slimevr.context.Context
import dev.slimevr.provisioning.ProvisioningManager
import dev.slimevr.serial.SerialPortInfo
import dev.slimevr.solarxr.rpc.ProvisioningBehaviour
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import solarxr_protocol.rpc.StartWifiScanRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import dev.slimevr.provisioning.reduce as reduceProvisioning

private fun fakePort(loc: String = "COM1") = SerialPortInfo(loc, "Fake $loc", 0x1A86, 0x7523)

private fun buildManager(serial: TestSerial, scope: CoroutineScope): ProvisioningManager {
	val context = Context.create(
		initialState = ProvisioningManager.INITIAL_STATE,
		scope = scope,
		reducer = ::reduceProvisioning,
		name = "ProvisioningBehaviourTest",
	)
	return ProvisioningManager(
		context = context,
		serialServer = serial.server,
		settings = buildTestSettings(scope),
		scope = scope,
	).also { it.startObserving() }
}

private fun buildBridge(id: Int, server: VRServer, manager: ProvisioningManager, scope: CoroutineScope): Pair<SolarXRBridge, CoroutineScope> {
	val bridgeScope = CoroutineScope(scope.coroutineContext + Job())
	val context = Context.create(
		initialState = SolarXRBridgeState(dataFeedConfigs = listOf()),
		scope = bridgeScope,
		reducer = ::reduce,
		behaviours = listOf(ProvisioningBehaviour(server, manager)),
		name = "SolarXRProvisioningTest[$id]",
	)
	val bridge = SolarXRBridge(id = id, context = context, appContext = object : TestAppContext() {})
	bridge.startObserving()
	return bridge to bridgeScope
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProvisioningBehaviourTest {
	@Test
	fun `a client going away while scanning releases the port`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		val manager = buildManager(serial, backgroundScope)
		val server = buildTestVrServerStub(backgroundScope)
		val (bridge, bridgeScope) = buildBridge(1, server, manager, backgroundScope)
		serial.plug(fakePort())

		bridge.rpcDispatcher.emit(StartWifiScanRequest())
		advanceTimeBy(4_000)
		assertEquals(1, serial.watcher.openCount["COM1"])

		bridgeScope.cancel()
		advanceTimeBy(1_000)

		assertEquals(1, serial.watcher.closeCount["COM1"])
	}

	@Test
	fun `a client that never scanned does not stop another client's scan`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		val manager = buildManager(serial, backgroundScope)
		val server = buildTestVrServerStub(backgroundScope)
		val (scanner, scannerScope) = buildBridge(1, server, manager, backgroundScope)
		val (_, idleScope) = buildBridge(2, server, manager, backgroundScope)
		serial.plug(fakePort())

		scanner.rpcDispatcher.emit(StartWifiScanRequest())
		advanceTimeBy(4_000)

		idleScope.cancel()
		advanceTimeBy(1_000)

		assertEquals(null, serial.watcher.closeCount["COM1"])
		scannerScope.cancel()
	}
}
