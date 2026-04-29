package dev.slimevr.provisioning

import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.buildTestSerialServer
import dev.slimevr.buildTestVrServer
import dev.slimevr.context.Context
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.serial.SerialPortInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.WifiProvisioningStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private fun fakePort(loc: String = "COM1") = SerialPortInfo(loc, "Fake $loc", 0x1A86, 0x7523)

private fun buildManager(serialServer: dev.slimevr.serial.SerialServer, scope: CoroutineScope): ProvisioningManager {
	val context = Context.create(
		initialState = ProvisioningManager.INITIAL_STATE,
		scope = scope,
		behaviours = listOf(ProvisioningManagerBaseBehaviour),
		name = "ProvisioningManagerTest",
	)
	return ProvisioningManager(context = context, serialServer = serialServer, scope = scope).also { it.startObserving() }
}

// Injects a fully connected device into the VRServer, simulating a tracker appearing on the network.
private fun connectDevice(vrServer: VRServer, mac: String, scope: CoroutineScope) {
	val device = Device.create(
		scope,
		id = vrServer.nextHandle(),
		address = "192.168.1.100",
		macAddress = mac,
		origin = DeviceOrigin.UDP,
		protocolVersion = 0,
	)
	vrServer.context.dispatch(VRServerActions.NewDevice(device.context.state.value.id, device))
	device.context.dispatch(DeviceActions.Update { copy(status = TrackerStatus.OK) })
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProvisioningManagerTest {

	@Test
	fun `NO_SERIAL_DEVICE_FOUND when no port appears within 15 seconds`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass", null)

		advanceTimeBy(15_001)

		assertEquals(WifiProvisioningStatus.NO_SERIAL_DEVICE_FOUND, manager.context.state.value.status)
	}

	@Test
	fun `OBTAINING_MAC_ADDRESS after port is detected and 2 second reboot delay elapses`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass", null)
		serialServer.onPortDetected(fakePort())

		advanceTimeBy(2_001)

		assertEquals(WifiProvisioningStatus.OBTAINING_MAC_ADDRESS, manager.context.state.value.status)
	}

	@Test
	fun `NO_SERIAL_LOGS_ERROR when tracker produces no serial output after 5 second timeout`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass", null)
		serialServer.onPortDetected(fakePort())

		// reboot delay (2s) + MAC acquisition timeout (5s)
		advanceTimeBy(7_001)

		assertEquals(WifiProvisioningStatus.NO_SERIAL_LOGS_ERROR, manager.context.state.value.status)
	}

	@Test
	fun `recovers from NO_SERIAL_LOGS_ERROR and sets macAddress when logs appear containing the MAC`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass", null)
		serialServer.onPortDetected(fakePort())

		advanceTimeBy(7_001)
		assertEquals(WifiProvisioningStatus.NO_SERIAL_LOGS_ERROR, manager.context.state.value.status)

		launch {
			delay(100)
			serialServer.onDataReceived("COM1", "mac: AA:BB:CC:DD:EE:FF")
		}

		advanceTimeBy(200)

		assertEquals("AA:BB:CC:DD:EE:FF", manager.context.state.value.macAddress)
	}

	@Test
	fun `CONNECTION_ERROR when logs are present but no MAC received within 5 second timeout`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass", null)
		serialServer.onPortDetected(fakePort())

		launch {
			delay(500)
			serialServer.onDataReceived("COM1", "some log line without a mac address")
		}

		// reboot delay (2s) + MAC timeout (5s) + error delay (3s)
		advanceTimeBy(10_001)

		assertEquals(WifiProvisioningStatus.CONNECTION_ERROR, manager.context.state.value.status)
	}

	@Test
	fun `CONNECTION_ERROR when WiFi credentials not acknowledged within 5 second timeout`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass", null)
		serialServer.onPortDetected(fakePort())

		launch {
			delay(2_100)
			serialServer.onDataReceived("COM1", "mac: AA:BB:CC:DD:EE:FF")
			// No credential acknowledgement sent
		}

		// reboot delay (2s) + MAC at 2.1s + credential ack timeout (5s) + error delay (3s)
		advanceTimeBy(10_200)

		assertEquals(WifiProvisioningStatus.CONNECTION_ERROR, manager.context.state.value.status)
	}

	@Test
	fun `CONNECTION_ERROR when WiFi does not connect within 15 second timeout`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass", null)
		serialServer.onPortDetected(fakePort())

		launch {
			delay(2_100)
			serialServer.onDataReceived("COM1", "mac: AA:BB:CC:DD:EE:FF")
			delay(100)
			serialServer.onDataReceived("COM1", "new wifi credentials set")
			// No WiFi connection log
		}

		// reboot delay (2s) + MAC + creds ack + WiFi connect timeout (15s) + error delay (3s)
		advanceTimeBy(20_500)

		assertEquals(WifiProvisioningStatus.CONNECTION_ERROR, manager.context.state.value.status)
	}

	@Test
	fun `CONNECTION_ERROR after exhausting all retries on can't connect`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass", null)
		serialServer.onPortDetected(fakePort())

		launch {
			delay(2_100)
			serialServer.onDataReceived("COM1", "mac: AA:BB:CC:DD:EE:FF")
			delay(100)
			serialServer.onDataReceived("COM1", "new wifi credentials set")
			// One initial attempt + MAX_CONNECTION_RETRIES retries; each retry needs a fresh "can't connect"
			repeat(MAX_CONNECTION_RETRIES + 1) {
				delay(100)
				serialServer.onDataReceived("COM1", "can't connect from any credentials")
				delay(3_000) // wait out the per-retry delay before the next window opens
			}
		}

		// 2200ms (MAC + creds) + 4 × (100ms "can't connect" + 3000ms retry delay) + 3000ms final delay
		advanceTimeBy(18_000)

		assertEquals(WifiProvisioningStatus.CONNECTION_ERROR, manager.context.state.value.status)
	}

	@Test
	fun `COULD_NOT_FIND_SERVER when tracker does not appear on the network within 30 second timeout`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass", null)
		serialServer.onPortDetected(fakePort())

		launch {
			delay(2_100)
			serialServer.onDataReceived("COM1", "mac: AA:BB:CC:DD:EE:FF")
			delay(100)
			serialServer.onDataReceived("COM1", "new wifi credentials set")
			delay(100)
			serialServer.onDataReceived("COM1", "looking for the server")
			// No device connects to the server
		}

		// reboot delay (2s) + MAC + creds + WiFi connect + server connect timeout (30s) + error delay (3s)
		advanceTimeBy(35_500)

		assertEquals(WifiProvisioningStatus.COULD_NOT_FIND_SERVER, manager.context.state.value.status)
	}

	@Test
	fun `DONE when full provisioning succeeds`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val vrServer = buildTestVrServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(vrServer, "wifi", "pass", null)
		serialServer.onPortDetected(fakePort())

		launch {
			delay(2_100)
			serialServer.onDataReceived("COM1", "mac: AA:BB:CC:DD:EE:FF")
			delay(100)
			serialServer.onDataReceived("COM1", "new wifi credentials set")
			delay(100)
			serialServer.onDataReceived("COM1", "looking for the server")
			delay(100)
			connectDevice(vrServer, "AA:BB:CC:DD:EE:FF", backgroundScope)
		}

		// Advance past the device connection but well before the 30s device connection timeout
		advanceTimeBy(5_000)

		assertEquals(WifiProvisioningStatus.DONE, manager.context.state.value.status)
	}

	@Test
	fun `resets to NONE and clears port after USB disconnect following DONE`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val vrServer = buildTestVrServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(vrServer, "wifi", "pass", null)
		serialServer.onPortDetected(fakePort())

		launch {
			delay(2_100)
			serialServer.onDataReceived("COM1", "mac: AA:BB:CC:DD:EE:FF")
			delay(100)
			serialServer.onDataReceived("COM1", "new wifi credentials set")
			delay(100)
			serialServer.onDataReceived("COM1", "looking for the server")
			delay(100)
			connectDevice(vrServer, "AA:BB:CC:DD:EE:FF", backgroundScope)
		}

		// Advance past the device connection but well before the 30s server-connect timeout
		advanceTimeBy(5_000)
		assertEquals(WifiProvisioningStatus.DONE, manager.context.state.value.status)

		serialServer.onPortLost("COM1")
		advanceTimeBy(1)

		assertEquals(WifiProvisioningStatus.NONE, manager.context.state.value.status)
		assertNull(manager.context.state.value.portLocation)
	}

	@Test
	fun `does not retry the same port after failure - waits for USB disconnect`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass", null)
		serialServer.onPortDetected(fakePort())

		launch {
			delay(500)
			serialServer.onDataReceived("COM1", "some log without mac")
		}

		// Reach CONNECTION_ERROR: reboot delay (2s) + MAC timeout (5s) + error delay (3s)
		advanceTimeBy(10_001)
		assertEquals(WifiProvisioningStatus.CONNECTION_ERROR, manager.context.state.value.status)

		// Port is still connected, advance significant time and confirm provisioning
		// is blocked waiting for disconnect, not looping back to select a new port
		advanceTimeBy(30_000)
		assertEquals(WifiProvisioningStatus.CONNECTION_ERROR, manager.context.state.value.status)
	}
}
