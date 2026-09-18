package dev.slimevr.provisioning

import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.buildTestAppContext
import dev.slimevr.buildTestSerialServer
import dev.slimevr.buildTestSettings
import dev.slimevr.buildTestVrServer
import dev.slimevr.context.Context
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.serial.SerialPortInfo
import dev.slimevr.udp.reduce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.TrackerProvisioningStatus
import solarxr_protocol.rpc.WifiAuthMode
import solarxr_protocol.rpc.WifiNetwork
import solarxr_protocol.rpc.WifiScanStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import dev.slimevr.provisioning.reduce as reduceProvisioning

private fun fakePort(loc: String = "COM1") = SerialPortInfo(loc, "Fake $loc", 0x1A86, 0x7523)

private fun fakeDonglePort(loc: String = "COM2") = SerialPortInfo(loc, "Fake dongle $loc", 0x1209, 0x7690)

private fun buildManager(serialServer: dev.slimevr.serial.SerialServer, scope: CoroutineScope): ProvisioningManager {
	val context = Context.create(
		initialState = ProvisioningManager.INITIAL_STATE,
		scope = scope,
		reducer = ::reduceProvisioning,
		name = "ProvisioningManagerTest",
	)
	return ProvisioningManager(context = context, serialServer = serialServer, settings = buildTestSettings(scope), scope = scope).also { it.startObserving() }
}

private fun ProvisioningManager.trackerStatus(port: String = "COM1") = context.state.value.trackers[port]?.status
private fun ProvisioningManager.trackerMac(port: String = "COM1") = context.state.value.trackers[port]?.macAddress

// Injects a fully connected device into the VRServer, simulating a tracker appearing on the network.
private fun connectDevice(vrServer: VRServer, mac: String, scope: CoroutineScope) {
	val appContext = buildTestAppContext(vrServer)
	val device = Device.create(
		scope = scope,
		appContext = appContext,
		id = vrServer.nextHandle(),
		address = "192.168.1.100",
		macAddress = mac,
		origin = DeviceOrigin.UDP,
		protocolVersion = 0,
	)
	vrServer.context.dispatch(VRServerActions.NewDevice(device.context.state.value.id, device))
	device.context.dispatch(DeviceActions.Update { copy(status = TrackerStatus.OK) })

	val conn = dev.slimevr.udp.UDPConnection(
		context = dev.slimevr.context.Context.create(
			initialState = dev.slimevr.udp.UDPConnectionState(
				address = "192.168.1.100",
				lastPacket = System.currentTimeMillis(),
				lastPacketNum = 0,
				lastHandshake = System.currentTimeMillis(),
				lastPing = dev.slimevr.udp.LastPing(0, 0),
				didHandshake = true,
				deviceId = device.context.state.value.id,
				trackerIds = emptyList(),
				features = null,
				sensorConfigFlags = emptyMap(),
			),
			scope = scope,
			reducer = ::reduce,
			behaviours = emptyList(),
			name = "TestConn",
		),
		appContext = appContext,
		packetEvents = dev.slimevr.EventDispatcher(name = "TestEvents", scope = scope),
		packetChannel = kotlinx.coroutines.channels.Channel(),
		socket = dev.slimevr.FakeDatagramSocket(),
		remoteAddress = io.ktor.network.sockets.InetSocketAddress("192.168.1.100", 6969),
		scope = scope,
	)
	appContext.udpServer.addConnection("192.168.1.100", conn)
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProvisioningManagerTest {

	@Test
	fun `OBTAINING_MAC_ADDRESS after port is detected and 2 second reboot delay elapses`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass")
		serialServer.onPortDetected(fakePort())

		advanceTimeBy(2_001)

		assertEquals(TrackerProvisioningStatus.OBTAINING_MAC_ADDRESS, manager.trackerStatus())
	}

	@Test
	fun `recovers from NO_SERIAL_LOGS_ERROR and sets macAddress when logs appear containing the MAC`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass")
		serialServer.onPortDetected(fakePort())

		advanceTimeBy(7_001)
		assertEquals(TrackerProvisioningStatus.NO_SERIAL_LOGS_ERROR, manager.trackerStatus())

		launch {
			delay(100)
			serialServer.onDataReceived("COM1", "mac: AA:BB:CC:DD:EE:FF")
		}

		advanceTimeBy(200)

		assertEquals("AA:BB:CC:DD:EE:FF", manager.trackerMac())
	}

	@Test
	fun `CONNECTION_ERROR when logs are present but no MAC received within 5 second timeout`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass")
		serialServer.onPortDetected(fakePort())

		launch {
			delay(500)
			serialServer.onDataReceived("COM1", "some log line without a mac address")
		}

		// A single slow/unresponsive window retries MAX_CONNECTION_RETRIES (3) times
		// before actually giving up - reboot delay (2s) + 4 x (MAC timeout 5s + error
		// delay 3s) for the initial attempt plus all 3 retries.
		advanceTimeBy(34_001)

		assertEquals(TrackerProvisioningStatus.CONNECTION_ERROR, manager.trackerStatus())
	}

	@Test
	fun `CONNECTION_ERROR when WiFi credentials not acknowledged within 5 second timeout`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass")
		serialServer.onPortDetected(fakePort())

		launch {
			delay(2_100)
			serialServer.onDataReceived("COM1", "mac: AA:BB:CC:DD:EE:FF")
			// No credential acknowledgement sent
		}

		// reboot delay (2s) + MAC at 2.1s + credential ack timeout (5s) + error delay (3s)
		advanceTimeBy(10_200)

		assertEquals(TrackerProvisioningStatus.CONNECTION_ERROR, manager.trackerStatus())
	}

	@Test
	fun `CONNECTION_ERROR when WiFi does not connect within 15 second timeout`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass")
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

		assertEquals(TrackerProvisioningStatus.CONNECTION_ERROR, manager.trackerStatus())
	}

	@Test
	fun `COULD_NOT_FIND_SERVER when tracker does not appear on the network within 30 second timeout`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass")
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

		assertEquals(TrackerProvisioningStatus.COULD_NOT_FIND_SERVER, manager.trackerStatus())
	}

	@Test
	fun `DONE when scanner succeeds and tracker sends handshake`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val vrServer = buildTestVrServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(vrServer, "wifi", "pass")
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

		advanceTimeBy(5_000)

		assertEquals(TrackerProvisioningStatus.DONE, manager.trackerStatus())
	}

	@Test
	fun `DONE when device was previously connected and goes offline before reconnecting`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val vrServer = buildTestVrServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		// Device was already connected to VRServer prior to provisioning
		connectDevice(vrServer, "AA:BB:CC:DD:EE:FF", backgroundScope)

		manager.startProvisioning(vrServer, "wifi", "pass")
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

		advanceTimeBy(5_000)

		assertEquals(TrackerProvisioningStatus.DONE, manager.trackerStatus())
	}

	@Test
	fun `tracker entry is removed from the map after USB disconnect following DONE`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val vrServer = buildTestVrServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(vrServer, "wifi", "pass")
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
		assertEquals(TrackerProvisioningStatus.DONE, manager.trackerStatus())

		serialServer.onPortLost("COM1")
		advanceTimeBy(1)

		assertNull(manager.trackerStatus())
		assertEquals(emptyMap(), manager.context.state.value.trackers)
	}

	@Test
	fun `does not relaunch a job for the same port after failure - waits for USB disconnect`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass")
		serialServer.onPortDetected(fakePort())

		launch {
			delay(500)
			serialServer.onDataReceived("COM1", "some log without mac")
		}

		// Reach the true terminal CONNECTION_ERROR (after all MAX_CONNECTION_RETRIES
		// retries are exhausted): reboot delay (2s) + 4 x (MAC timeout 5s + error delay 3s)
		advanceTimeBy(34_001)
		assertEquals(TrackerProvisioningStatus.CONNECTION_ERROR, manager.trackerStatus())

		// Port is still connected, advance significant time and confirm provisioning
		// is blocked waiting for disconnect, not relaunching a fresh job for the same port
		advanceTimeBy(30_000)
		assertEquals(TrackerProvisioningStatus.CONNECTION_ERROR, manager.trackerStatus())
	}

	@Test
	fun `provisions two trackers concurrently without one clobbering the other's status`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val vrServer = buildTestVrServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(vrServer, "wifi", "pass")
		serialServer.onPortDetected(fakePort("COM1"))
		serialServer.onPortDetected(fakePort("COM2"))

		launch {
			delay(2_100)
			serialServer.onDataReceived("COM1", "mac: AA:BB:CC:DD:EE:01")
			serialServer.onDataReceived("COM2", "mac: AA:BB:CC:DD:EE:02")
			delay(100)
			serialServer.onDataReceived("COM1", "new wifi credentials set")
			serialServer.onDataReceived("COM2", "new wifi credentials set")
			delay(100)
			serialServer.onDataReceived("COM1", "looking for the server")
			serialServer.onDataReceived("COM2", "looking for the server")
			delay(100)
			connectDevice(vrServer, "AA:BB:CC:DD:EE:01", backgroundScope)
			connectDevice(vrServer, "AA:BB:CC:DD:EE:02", backgroundScope)
		}

		advanceTimeBy(5_000)

		assertEquals(TrackerProvisioningStatus.DONE, manager.trackerStatus("COM1"))
		assertEquals(TrackerProvisioningStatus.DONE, manager.trackerStatus("COM2"))
		assertEquals("AA:BB:CC:DD:EE:01", manager.trackerMac("COM1"))
		assertEquals("AA:BB:CC:DD:EE:02", manager.trackerMac("COM2"))
	}

	@Test
	fun `stopProvisioning cancels all concurrent per-port jobs and clears the trackers map`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass")
		serialServer.onPortDetected(fakePort("COM1"))
		serialServer.onPortDetected(fakePort("COM2"))

		advanceTimeBy(2_001)
		assertEquals(TrackerProvisioningStatus.OBTAINING_MAC_ADDRESS, manager.trackerStatus("COM1"))
		assertEquals(TrackerProvisioningStatus.OBTAINING_MAC_ADDRESS, manager.trackerStatus("COM2"))

		manager.stopProvisioning()

		assertEquals(emptyMap(), manager.context.state.value.trackers)

		// Confirm the jobs were actually cancelled, not just the map cleared - no further
		// state changes should occur even if the tracker keeps talking.
		serialServer.onDataReceived("COM1", "mac: AA:BB:CC:DD:EE:FF")
		advanceTimeBy(10_000)
		assertEquals(emptyMap(), manager.context.state.value.trackers)
	}

	@Test
	fun `startScan NO_SERIAL_DEVICE_FOUND when no port appears within 15 seconds`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startWifiScan()

		advanceTimeBy(15_001)

		assertEquals(WifiScanStatus.NO_SERIAL_DEVICE_FOUND, manager.context.state.value.scan.status)
	}

	@Test
	fun `startScan keeps waiting for a port past the first 15 second window instead of giving up`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startWifiScan()

		// No tracker plugged in yet - first detection window times out.
		advanceTimeBy(15_001)
		assertEquals(WifiScanStatus.NO_SERIAL_DEVICE_FOUND, manager.context.state.value.scan.status)

		// User plugs the tracker in well after the page loaded.
		serialServer.onPortDetected(fakePort())

		launch {
			// Past selectAndOpenPort's 2s backoff delay before it retries detection.
			delay(2_100)
			serialServer.onDataReceived("COM1", "[WSCAN] Scanning for WiFi networks...")
			serialServer.onDataReceived("COM1", "[WSCAN] Found 0 networks:")
		}
		advanceTimeBy(2_200)

		assertEquals(WifiScanStatus.RESULTS, manager.context.state.value.scan.status)
	}

	@Test
	fun `startScan skips an already-connected HID dongle and selects the ESP tracker instead`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		// Dongle was already plugged in before the scan started.
		serialServer.onPortDetected(fakeDonglePort())

		manager.startWifiScan()
		advanceTimeBy(100)
		// Still waiting - the dongle alone must not satisfy port selection.
		assertEquals(null, manager.context.state.value.scan.portLocation)

		// User now plugs in the actual tracker.
		serialServer.onPortDetected(fakePort())

		launch {
			delay(100)
			serialServer.onDataReceived("COM1", "[WSCAN] Scanning for WiFi networks...")
			serialServer.onDataReceived("COM1", "[WSCAN] Found 0 networks:")
		}
		advanceTimeBy(200)

		assertEquals("COM1", manager.context.state.value.scan.portLocation)
		assertEquals(WifiScanStatus.RESULTS, manager.context.state.value.scan.status)
	}

	@Test
	fun `startScan falls back to NONE when the scanned port disconnects, then picks up a replug`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startWifiScan()
		serialServer.onPortDetected(fakePort())

		launch {
			delay(100)
			serialServer.onDataReceived("COM1", "[WSCAN] Scanning for WiFi networks...")
			serialServer.onDataReceived("COM1", "[WSCAN] Found 0 networks:")
		}
		advanceTimeBy(200)
		assertEquals(WifiScanStatus.RESULTS, manager.context.state.value.scan.status)

		// Tracker is unplugged while still sitting on the "configure Wi-Fi" step - the
		// GUI step rail should fall back to "no tracker" (step 1) instead of keeping a
		// stale RESULTS for a tracker that's no longer there.
		serialServer.onPortLost("COM1")
		advanceTimeBy(1)
		assertEquals(WifiScanStatus.NONE, manager.context.state.value.scan.status)
		assertEquals(null, manager.context.state.value.scan.portLocation)

		// Replugging the same tracker is picked up automatically, same as the very
		// first attempt.
		serialServer.onPortDetected(fakePort())
		launch {
			delay(100)
			serialServer.onDataReceived("COM1", "[WSCAN] Scanning for WiFi networks...")
			serialServer.onDataReceived("COM1", "[WSCAN] Found 0 networks:")
		}
		advanceTimeBy(200)
		assertEquals(WifiScanStatus.RESULTS, manager.context.state.value.scan.status)
	}

	@Test
	fun `stopWifiScan cancels the scan job so it stops reacting to further plug-replug cycles`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startWifiScan()
		serialServer.onPortDetected(fakePort())
		launch {
			delay(100)
			serialServer.onDataReceived("COM1", "[WSCAN] Scanning for WiFi networks...")
			serialServer.onDataReceived("COM1", "[WSCAN] Found 0 networks:")
		}
		advanceTimeBy(200)
		assertEquals(WifiScanStatus.RESULTS, manager.context.state.value.scan.status)

		manager.stopWifiScan()
		assertEquals(WifiScanStatus.NONE, manager.context.state.value.scan.status)

		// Confirms the job was actually cancelled, not just the state cleared - without
		// this, the scan job would keep running forever (watching for disconnect/replug)
		// even after the GUI navigated away, silently issuing scan commands to whatever
		// tracker shows up next.
		serialServer.onPortLost("COM1")
		serialServer.onPortDetected(fakePort())
		advanceTimeBy(20_000)
		assertEquals(WifiScanStatus.NONE, manager.context.state.value.scan.status)
	}

	@Test
	fun `startScan UNSUPPORTED when tracker logs never contain a WSCAN line within 15 seconds`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startWifiScan()
		serialServer.onPortDetected(fakePort())

		launch {
			// Each retry starts with ClearLogs, so a one-time line would be wiped before the
			// next attempt's ack-timeout check - resend periodically to simulate a device
			// that's chatty (boot logs etc.) but never prints anything WIFISCAN-tagged.
			repeat(20) {
				delay(500)
				serialServer.onDataReceived("COM1", "some unrelated log line")
			}
		}

		advanceTimeBy(15_001)

		assertEquals(WifiScanStatus.UNSUPPORTED, manager.context.state.value.scan.status)
	}

	@Test
	fun `startScan RESULTS with correctly parsed networks`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startWifiScan()
		serialServer.onPortDetected(fakePort())

		launch {
			delay(100)
			serialServer.onDataReceived("COM1", "[WSCAN] Scanning for WiFi networks...")
			serialServer.onDataReceived("COM1", "[WSCAN] Found 2 networks:")
			serialServer.onDataReceived("COM1", "[WSCAN] 0:    09    MyNetwork    (-45)    PASS")
			serialServer.onDataReceived("COM1", "[WSCAN] 1:    04    Open    (-70)    OPEN")
		}

		advanceTimeBy(200)

		assertEquals(WifiScanStatus.RESULTS, manager.context.state.value.scan.status)
		assertEquals(
			listOf(
				WifiNetwork(ssid = "MyNetwork", rssi = -45, authMode = WifiAuthMode.UNKNOWN),
				WifiNetwork(ssid = "Open", rssi = -70, authMode = WifiAuthMode.OPEN),
			),
			manager.context.state.value.scan.networks,
		)
	}

	@Test
	fun `startScan RESULTS parses the newer quoted WSCAN format (post SlimeVR-Tracker-ESP#358)`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startWifiScan()
		serialServer.onPortDetected(fakePort())

		launch {
			delay(100)
			serialServer.onDataReceived("COM1", "[WSCAN] Scanning for WiFi networks...")
			serialServer.onDataReceived("COM1", "[WSCAN] Found 2 networks:")
			serialServer.onDataReceived("COM1", "[WSCAN] 0:\t09\t'MyNetwork'\t(-45 dBm)\tWPA2_PSK")
			serialServer.onDataReceived("COM1", "[WSCAN] 1:\t15\t'Futura's Slimes'\t(-85 dBm)\tOPEN")
		}

		advanceTimeBy(200)

		assertEquals(WifiScanStatus.RESULTS, manager.context.state.value.scan.status)
		assertEquals(
			listOf(
				WifiNetwork(ssid = "MyNetwork", rssi = -45, authMode = WifiAuthMode.WPA2_PSK),
				WifiNetwork(ssid = "Futura's Slimes", rssi = -85, authMode = WifiAuthMode.OPEN),
			),
			manager.context.state.value.scan.networks,
		)
	}

	@Test
	fun `startProvisioning reuses the connection opened by a prior startScan without rediscovering the port`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startWifiScan()
		serialServer.onPortDetected(fakePort())

		launch {
			delay(100)
			serialServer.onDataReceived("COM1", "[WSCAN] Scanning for WiFi networks...")
			serialServer.onDataReceived("COM1", "[WSCAN] Found 0 networks:")
		}
		advanceTimeBy(200)
		assertEquals(WifiScanStatus.RESULTS, manager.context.state.value.scan.status)

		// The tracker is still plugged in (port never left availablePorts), so this should
		// proceed straight into the reboot delay instead of first waiting up to 15s to
		// rediscover a port that's already known.
		manager.startProvisioning(buildTestVrServer(backgroundScope), "wifi", "pass")
		advanceTimeBy(2_001)

		assertEquals(TrackerProvisioningStatus.OBTAINING_MAC_ADDRESS, manager.trackerStatus())
	}

	@Test
	fun `startScan again after RESULTS produces a fresh scan on the same still-open connection`() = runTest {
		val serialServer = buildTestSerialServer(backgroundScope)
		val manager = buildManager(serialServer, backgroundScope)

		manager.startWifiScan()
		serialServer.onPortDetected(fakePort())

		launch {
			delay(100)
			serialServer.onDataReceived("COM1", "[WSCAN] Scanning for WiFi networks...")
			serialServer.onDataReceived("COM1", "[WSCAN] Found 1 networks:")
			serialServer.onDataReceived("COM1", "[WSCAN] 0:    03    Old    (-50)    OPEN")
		}
		advanceTimeBy(200)
		assertEquals(
			listOf(WifiNetwork(ssid = "Old", rssi = -50, authMode = WifiAuthMode.OPEN)),
			manager.context.state.value.scan.networks,
		)

		// Environment changed (e.g. a new access point powered on) - the user re-triggers a scan.
		manager.startWifiScan()

		launch {
			delay(100)
			serialServer.onDataReceived("COM1", "[WSCAN] Scanning for WiFi networks...")
			serialServer.onDataReceived("COM1", "[WSCAN] Found 1 networks:")
			serialServer.onDataReceived("COM1", "[WSCAN] 0:    03    New    (-40)    OPEN")
		}
		advanceTimeBy(200)

		assertEquals(WifiScanStatus.RESULTS, manager.context.state.value.scan.status)
		assertEquals(
			listOf(WifiNetwork(ssid = "New", rssi = -40, authMode = WifiAuthMode.OPEN)),
			manager.context.state.value.scan.networks,
		)
	}
}
