package dev.slimevr.firmware

import dev.slimevr.FakeSerialPortWatcher
import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.buildTestAppContext
import dev.slimevr.buildTestSerial
import dev.slimevr.buildTestSettings
import dev.slimevr.buildTestVrServerStub
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.serial.FlashingHandler
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
import solarxr_protocol.rpc.FirmwareUpdateStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

private fun fakePort(loc: String = "COM1") = SerialPortInfo(loc, "Fake $loc", 0x1A86, 0x7523)

/** Fails immediately at openSerial so the Flasher throws with no IO delays */
private fun fakeFlashHandler() = object : FlashingHandler {
	override fun openSerial(port: Any) = error("simulated flash failure")
	override fun closeSerial() {}
	override fun write(data: ByteArray) {}
	override fun read(length: Int) = ByteArray(length)
	override fun setDTR(value: Boolean) {}
	override fun setRTS(value: Boolean) {}
	override fun changeBaud(baud: Int) {}
	override fun setReadTimeout(timeout: Long) {}
	override fun availableBytes() = 0
	override fun flushIOBuffers() {}
}

private val failingFirmwareFlasher = FirmwareFlasher { portLocation, handler, _, _ ->
	handler.openSerial(portLocation)
}

private fun buildSerialServer(
	scope: kotlinx.coroutines.CoroutineScope,
	flashHandler: () -> FlashingHandler = ::fakeFlashHandler,
) = buildTestSerial(scope, FakeSerialPortWatcher(flashingHandler = flashHandler))

// VRServer's BaseBehaviour sets up infinite StateFlow collectors via launchIn(scope).
// backgroundScope lets those run on the test scheduler but doesn't cause
// UncompletedCoroutinesError when the test ends.
private fun buildVrServer(
	backgroundScope: CoroutineScope,
): VRServer = VRServer.create(backgroundScope)

class DoSerialFlashTest {

	@Test
	fun `emits ERROR_DEVICE_NOT_FOUND when port is not available`() = runTest {
		val serial = buildSerialServer(backgroundScope)
		val vrServer = buildTestVrServerStub(backgroundScope)
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		doSerialFlash(
			portLocation = "COM1",
			parts = emptyList(),
			needManualReboot = false,
			ssid = null,
			password = null,
			serialServer = serial.server,
			settings = buildTestSettings(backgroundScope),
			server = vrServer,
			flasher = failingFirmwareFlasher,
			onStatus = { s, _ -> statuses += s },
			scope = this,
		)

		assertEquals(FirmwareUpdateStatus.ERROR_DEVICE_NOT_FOUND, statuses.last())
	}

	@Test
	fun `closes existing connection before flashing`() = runTest {
		val serial = buildSerialServer(backgroundScope)
		serial.plug(fakePort())
		serial.server.awaitConsole("COM1", 5.seconds)
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		doSerialFlash(
			portLocation = "COM1",
			parts = emptyList(),
			needManualReboot = false,
			ssid = null,
			password = null,
			serialServer = serial.server,
			settings = buildTestSettings(backgroundScope),
			server = buildTestVrServerStub(backgroundScope),
			flasher = failingFirmwareFlasher,
			onStatus = { s, _ -> statuses += s },
			scope = this,
		)

		assertEquals(FirmwareUpdateStatus.ERROR_UPLOAD_FAILED, statuses.last())
		assertEquals(emptySet(), serial.server.context.state.value.flashing)
	}

	@Test
	fun `emits ERROR_UPLOAD_FAILED when flash throws`() = runTest {
		val serial = buildSerialServer(backgroundScope, ::fakeFlashHandler)
		serial.plug(fakePort())
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		doSerialFlash(
			portLocation = "COM1",
			parts = emptyList(),
			needManualReboot = false,
			ssid = null,
			password = null,
			serialServer = serial.server,
			settings = buildTestSettings(backgroundScope),
			server = buildTestVrServerStub(backgroundScope),
			flasher = failingFirmwareFlasher,
			onStatus = { s, _ -> statuses += s },
			scope = this,
		)

		assertEquals(FirmwareUpdateStatus.ERROR_UPLOAD_FAILED, statuses.last())
		assertEquals(emptySet(), serial.server.context.state.value.flashing)
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `emits ERROR_DEVICE_NOT_FOUND when device has not reconnected after flash`() = runTest {
		// Port never comes back, so awaitConsole inside doSerialFlashPostFlash times out
		val serial = buildSerialServer(backgroundScope)
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		doSerialFlashPostFlash(
			portLocation = "COM1",
			needManualReboot = false,
			ssid = "wifi",
			password = "pass",
			serialServer = serial.server,
			settings = buildTestSettings(backgroundScope),
			server = buildTestVrServerStub(backgroundScope),
			onStatus = { s, _ -> statuses += s },
		)

		assertEquals(FirmwareUpdateStatus.ERROR_DEVICE_NOT_FOUND, statuses.last())
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `emits ERROR_PROVISIONING_FAILED when MAC not received within timeout`() = runTest {
		val serial = buildSerialServer(backgroundScope)
		serial.plug(fakePort())
		serial.server.awaitConsole("COM1", 5.seconds)
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		val job = launch {
			doSerialFlashPostFlash(
				portLocation = "COM1",
				needManualReboot = false,
				ssid = "wifi",
				password = "pass",
				serialServer = serial.server,
				settings = buildTestSettings(backgroundScope),
				server = buildTestVrServerStub(backgroundScope),
				onStatus = { s, _ -> statuses += s },
			)
		}

		advanceTimeBy(10_001)
		job.join()

		assertEquals(FirmwareUpdateStatus.ERROR_PROVISIONING_FAILED, statuses.last())
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `emits ERROR_PROVISIONING_FAILED when ssid or password is null`() = runTest {
		val serial = buildSerialServer(backgroundScope)
		serial.plug(fakePort())
		serial.server.awaitConsole("COM1", 5.seconds)
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		backgroundScope.launch {
			doSerialFlashPostFlash(
				portLocation = "COM1",
				needManualReboot = false,
				ssid = null,
				password = null,
				serialServer = serial.server,
				settings = buildTestSettings(backgroundScope),
				server = buildTestVrServerStub(backgroundScope),
				onStatus = { s, _ -> statuses += s },
			)
		}

		backgroundScope.launch {
			delay(100)
			serial.emitLine("COM1", "mac: AA:BB:CC:DD:EE:FF")
		}

		advanceTimeBy(500)

		assertEquals(FirmwareUpdateStatus.ERROR_PROVISIONING_FAILED, statuses.last())
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `emits ERROR_PROVISIONING_FAILED when wifi does not connect within timeout`() = runTest {
		val serial = buildSerialServer(backgroundScope)
		serial.plug(fakePort())
		serial.server.awaitConsole("COM1", 5.seconds)
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		val job = launch {
			doSerialFlashPostFlash(
				portLocation = "COM1",
				needManualReboot = false,
				ssid = "wifi",
				password = "pass",
				serialServer = serial.server,
				settings = buildTestSettings(backgroundScope),
				server = buildTestVrServerStub(backgroundScope),
				onStatus = { s, _ -> statuses += s },
			)
		}

		backgroundScope.launch {
			delay(100)
			serial.emitLine("COM1", "mac: AA:BB:CC:DD:EE:FF")
		}

		// MAC arrives at 100ms; wifi timeout fires 30s later
		advanceTimeBy(30_101)
		job.join()

		assertEquals(FirmwareUpdateStatus.ERROR_PROVISIONING_FAILED, statuses.last())
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `emits ERROR_TIMEOUT when tracker does not appear within timeout`() = runTest {
		val serial = buildSerialServer(backgroundScope)
		serial.plug(fakePort())
		serial.server.awaitConsole("COM1", 5.seconds)
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		val job = launch {
			doSerialFlashPostFlash(
				portLocation = "COM1",
				needManualReboot = false,
				ssid = "wifi",
				password = "pass",
				serialServer = serial.server,
				settings = buildTestSettings(backgroundScope),
				server = buildTestVrServerStub(backgroundScope),
				onStatus = { s, _ -> statuses += s },
			)
		}

		backgroundScope.launch {
			delay(100)
			serial.emitLine("COM1", "mac: AA:BB:CC:DD:EE:FF")
			delay(200)
			serial.emitLine("COM1", "looking for the server")
		}

		// MAC at 100ms, wifi log at 200ms; tracker timeout fires 60s after wifi confirmed
		advanceTimeBy(60_201)
		job.join()

		assertEquals(FirmwareUpdateStatus.ERROR_TIMEOUT, statuses.last())
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `emits DONE when everything succeeds`() = runTest {
		val serial = buildSerialServer(backgroundScope)
		serial.plug(fakePort())
		serial.server.awaitConsole("COM1", 5.seconds)
		val vrServer = buildTestVrServerStub(backgroundScope)
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		backgroundScope.launch {
			doSerialFlashPostFlash(
				portLocation = "COM1",
				needManualReboot = false,
				ssid = "wifi",
				password = "pass",
				serialServer = serial.server,
				settings = buildTestSettings(backgroundScope),
				server = vrServer,
				onStatus = { s, _ -> statuses += s },
			)
		}

		backgroundScope.launch {
			delay(100)
			serial.emitLine("COM1", "mac: AA:BB:CC:DD:EE:FF")
			delay(200)
			serial.emitLine("COM1", "looking for the server")
			delay(300)
			val appContext = buildTestAppContext(vrServer)
			val device = Device.create(
				scope = backgroundScope,
				appContext = appContext,
				id = vrServer.nextHandle(),
				address = "192.168.1.100",
				macAddress = "AA:BB:CC:DD:EE:FF",
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
					scope = backgroundScope,
					reducer = ::reduce,
					behaviours = emptyList(),
					name = "TestConn",
				),
				appContext = appContext,
				packetEvents = dev.slimevr.EventDispatcher(name = "TestEvents", scope = backgroundScope),
				packetChannel = kotlinx.coroutines.channels.Channel(),
				socket = dev.slimevr.FakeDatagramSocket(),
				remoteAddress = io.ktor.network.sockets.InetSocketAddress("192.168.1.100", 6969),
				scope = backgroundScope,
			)
			appContext.udpServer.addConnection("192.168.1.100", conn)
		}

		advanceTimeBy(1000)

		assertEquals(FirmwareUpdateStatus.DONE, statuses.last())
	}
}
