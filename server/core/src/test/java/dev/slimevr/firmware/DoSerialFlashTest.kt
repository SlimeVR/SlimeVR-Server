package dev.slimevr.firmware

import dev.llelievr.espflashkotlin.FlasherSerialInterface
import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.buildTestSettings
import dev.slimevr.buildTestVrServerStub
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.serial.SerialPortHandle
import dev.slimevr.serial.SerialPortInfo
import dev.slimevr.serial.SerialServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.FirmwareUpdateStatus
import kotlin.test.Test
import kotlin.test.assertEquals

private fun fakePortHandle(loc: String) = SerialPortHandle(
	portLocation = loc,
	descriptivePortName = "Fake $loc",
	writeCommand = {},
	close = {},
)

private fun fakePort(loc: String = "COM1") = SerialPortInfo(loc, "Fake $loc", 0x1A86, 0x7523)

/** Fails immediately at openSerial so the Flasher throws with no IO delays */
private fun failingFlashHandler() = object : FlasherSerialInterface {
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

private fun buildSerialServer(
	scope: kotlinx.coroutines.CoroutineScope,
	flashHandler: () -> FlasherSerialInterface = ::failingFlashHandler,
) = SerialServer.create(
	openPort = { loc, _, _ -> fakePortHandle(loc) },
	openFlashingPort = flashHandler,
	scope = scope,
)

// VRServer's BaseBehaviour sets up infinite StateFlow collectors via launchIn(scope).
// backgroundScope lets those run on the test scheduler but doesn't cause
// UncompletedCoroutinesError when the test ends.
private fun buildVrServer(
	backgroundScope: CoroutineScope,
): VRServer = VRServer.create(backgroundScope)

class DoSerialFlashTest {

	@Test
	fun `emits ERROR_DEVICE_NOT_FOUND when port is not available`() = runTest {
		val server = buildSerialServer(this)
		val vrServer = buildTestVrServerStub(backgroundScope)
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		doSerialFlash(
			portLocation = "COM1",
			parts = emptyList(),
			needManualReboot = false,
			ssid = null,
			password = null,
			serialServer = server,
			settings = buildTestSettings(backgroundScope),
			server = vrServer,
			onStatus = { s, _ -> statuses += s },
			scope = this,
		)

		assertEquals(FirmwareUpdateStatus.ERROR_DEVICE_NOT_FOUND, statuses.last())
	}

	@Test
	fun `emits ERROR_DEVICE_NOT_FOUND when port already has a connection`() = runTest {
		val server = buildSerialServer(this)
		server.onPortDetected(fakePort())
		server.openConnection("COM1")
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		doSerialFlash(
			portLocation = "COM1",
			parts = emptyList(),
			needManualReboot = false,
			ssid = null,
			password = null,
			serialServer = server,
			settings = buildTestSettings(backgroundScope),
			server = buildTestVrServerStub(backgroundScope),
			onStatus = { s, _ -> statuses += s },
			scope = this,
		)

		assertEquals(FirmwareUpdateStatus.ERROR_DEVICE_NOT_FOUND, statuses.last())
	}

	@Test
	fun `emits ERROR_UPLOAD_FAILED when flash throws`() = runTest {
		val server = buildSerialServer(this, ::failingFlashHandler)
		server.onPortDetected(fakePort())
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		doSerialFlash(
			portLocation = "COM1",
			parts = emptyList(),
			needManualReboot = false,
			ssid = null,
			password = null,
			serialServer = server,
			settings = buildTestSettings(backgroundScope),
			server = buildTestVrServerStub(backgroundScope),
			onStatus = { s, _ -> statuses += s },
			scope = this,
		)

		assertEquals(FirmwareUpdateStatus.ERROR_UPLOAD_FAILED, statuses.last())
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `emits ERROR_DEVICE_NOT_FOUND when device has not reconnected after flash`() = runTest {
		// Port not back in availablePorts yet, openConnection inside doSerialFlashPostFlash is a no-op
		val server = buildSerialServer(this)
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		doSerialFlashPostFlash(
			portLocation = "COM1",
			needManualReboot = false,
			ssid = "wifi",
			password = "pass",
			serialServer = server,
			settings = buildTestSettings(backgroundScope),
			server = buildTestVrServerStub(backgroundScope),
			onStatus = { s, _ -> statuses += s },
		)

		assertEquals(FirmwareUpdateStatus.ERROR_DEVICE_NOT_FOUND, statuses.last())
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `emits ERROR_PROVISIONING_FAILED when MAC not received within timeout`() = runTest {
		val server = buildSerialServer(this)
		server.onPortDetected(fakePort())
		server.openConnection("COM1")
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		val job = launch {
			doSerialFlashPostFlash(
				portLocation = "COM1",
				needManualReboot = false,
				ssid = "wifi",
				password = "pass",
				serialServer = server,
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
		val server = buildSerialServer(this)
		server.onPortDetected(fakePort())
		server.openConnection("COM1")
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		backgroundScope.launch {
			doSerialFlashPostFlash(
				portLocation = "COM1",
				needManualReboot = false,
				ssid = null,
				password = null,
				serialServer = server,
				settings = buildTestSettings(backgroundScope),
				server = buildTestVrServerStub(backgroundScope),
				onStatus = { s, _ -> statuses += s },
			)
		}

		backgroundScope.launch {
			delay(100)
			server.onDataReceived("COM1", "mac: AA:BB:CC:DD:EE:FF")
		}

		advanceTimeBy(500)

		assertEquals(FirmwareUpdateStatus.ERROR_PROVISIONING_FAILED, statuses.last())
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `emits ERROR_PROVISIONING_FAILED when wifi does not connect within timeout`() = runTest {
		val server = buildSerialServer(this)
		server.onPortDetected(fakePort())
		server.openConnection("COM1")
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		val job = launch {
			doSerialFlashPostFlash(
				portLocation = "COM1",
				needManualReboot = false,
				ssid = "wifi",
				password = "pass",
				serialServer = server,
				settings = buildTestSettings(backgroundScope),
				server = buildTestVrServerStub(backgroundScope),
				onStatus = { s, _ -> statuses += s },
			)
		}

		backgroundScope.launch {
			delay(100)
			server.onDataReceived("COM1", "mac: AA:BB:CC:DD:EE:FF")
		}

		// MAC arrives at 100ms; wifi timeout fires 30s later
		advanceTimeBy(30_101)
		job.join()

		assertEquals(FirmwareUpdateStatus.ERROR_PROVISIONING_FAILED, statuses.last())
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `emits ERROR_TIMEOUT when tracker does not appear within timeout`() = runTest {
		val server = buildSerialServer(this)
		server.onPortDetected(fakePort())
		server.openConnection("COM1")
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		val job = launch {
			doSerialFlashPostFlash(
				portLocation = "COM1",
				needManualReboot = false,
				ssid = "wifi",
				password = "pass",
				serialServer = server,
				settings = buildTestSettings(backgroundScope),
				server = buildTestVrServerStub(backgroundScope),
				onStatus = { s, _ -> statuses += s },
			)
		}

		backgroundScope.launch {
			delay(100)
			server.onDataReceived("COM1", "mac: AA:BB:CC:DD:EE:FF")
			delay(200)
			server.onDataReceived("COM1", "looking for the server")
		}

		// MAC at 100ms, wifi log at 200ms; tracker timeout fires 60s after wifi confirmed
		advanceTimeBy(60_201)
		job.join()

		assertEquals(FirmwareUpdateStatus.ERROR_TIMEOUT, statuses.last())
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `emits DONE when everything succeeds`() = runTest {
		val server = buildSerialServer(this)
		server.onPortDetected(fakePort())
		server.openConnection("COM1")
		val vrServer = buildTestVrServerStub(backgroundScope)
		val statuses = mutableListOf<FirmwareUpdateStatus>()

		backgroundScope.launch {
			doSerialFlashPostFlash(
				portLocation = "COM1",
				needManualReboot = false,
				ssid = "wifi",
				password = "pass",
				serialServer = server,
				settings = buildTestSettings(backgroundScope),
				server = vrServer,
				onStatus = { s, _ -> statuses += s },
			)
		}

		backgroundScope.launch {
			delay(100)
			server.onDataReceived("COM1", "mac: AA:BB:CC:DD:EE:FF")
			delay(200)
			server.onDataReceived("COM1", "looking for the server")
			delay(300)
			val device = Device.create(
				backgroundScope,
				id = vrServer.nextHandle(),
				address = "192.168.1.100",
				macAddress = "AA:BB:CC:DD:EE:FF",
				origin = DeviceOrigin.UDP,
				protocolVersion = 0,
			)
			vrServer.context.dispatch(VRServerActions.NewDevice(device.context.state.value.id, device))
			device.context.dispatch(DeviceActions.Update { copy(status = TrackerStatus.OK) })
		}

		advanceTimeBy(1000)

		assertEquals(FirmwareUpdateStatus.DONE, statuses.last())
	}
}
