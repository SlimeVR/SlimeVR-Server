package dev.slimevr.firmware

import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.buildTestAppContext
import dev.slimevr.buildTestDevicesConfig
import dev.slimevr.buildTestSerial
import dev.slimevr.buildTestVrServerStub
import dev.slimevr.context.Context
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.firmware.behaviours.OtaCallbackBehaviour
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.io.readByteArray
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.FirmwareDeviceIdTable
import solarxr_protocol.rpc.FirmwareUpdateStatus
import java.io.ByteArrayOutputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun addUdpDevice(
	server: VRServer,
	scope: kotlinx.coroutines.CoroutineScope,
	id: Int = server.nextHandle(),
	macAddress: String? = null,
): Device {
	val device = Device.create(
		scope = scope,
		appContext = buildTestAppContext(server),
		id = id,
		address = "192.168.1.100",
		macAddress = macAddress,
		origin = DeviceOrigin.UDP,
		protocolVersion = 0,
	)
	server.context.dispatch(VRServerActions.NewDevice(device.context.state.value.id, device))
	return device
}

private fun freeTcpPort(): Int = ServerSocket(0).use { it.localPort }

private suspend fun buildOtaTestManager(scope: CoroutineScope, port: Int): FirmwareManager {
	val context = Context.create(
		initialState = FirmwareManagerState(jobs = mapOf()),
		scope = scope,
		reducer = ::reduce,
		behaviours = listOf(OtaCallbackBehaviour(port)),
		name = "FirmwareManager[test]",
	)
	val manager = FirmwareManager(
		context = context,
		serialServer = buildTestSerial(scope).server,
		devices = buildTestDevicesConfig(scope),
		flasher = { _, _, _, _ -> },
		scope = scope,
	)
	manager.startObserving()
	awaitListening(port)
	return manager
}

private suspend fun awaitListening(port: Int) = withTimeout(5_000) {
	while (runCatching { withContext(Dispatchers.IO) { Socket("127.0.0.1", port).close() } }.isFailure) {
		delay(20)
	}
}

private fun otaJob(ip: String, status: FirmwareUpdateStatus, id: Int = 1) = FirmwareManagerActions.UpdateJob(
	FirmwareJobStatus(
		portLocation = ip,
		firmwareDeviceId = FirmwareDeviceIdTable(id = id.toUShort()),
		status = status,
	),
)

private fun runOtaTest(block: suspend CoroutineScope.(scope: CoroutineScope) -> Unit) = runBlocking {
	val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
	try {
		withTimeout(30_000) { block(scope) }
	} finally {
		scope.cancel()
	}
}

private class FakeOtaDevice(val ip: String, val udp: DatagramSocket) {
	val received = ByteArrayOutputStream()
	var advertisedPort = -1
	val firstChunk = CompletableDeferred<Unit>()

	suspend fun run(beforeFirstAck: suspend () -> Unit) = withContext(Dispatchers.IO) {
		udp.use {
			udp.soTimeout = 10_000
			val buffer = ByteArray(256)
			val invite = DatagramPacket(buffer, buffer.size)
			udp.receive(invite)
			val args = String(buffer, 0, invite.length).trim().split(" ")
			advertisedPort = args[1].toInt()
			val size = args[2].toInt()
			udp.send(DatagramPacket("OK".toByteArray(), 2, invite.socketAddress))

			Socket().use { socket ->
				socket.soTimeout = 10_000
				socket.bind(InetSocketAddress(ip, 0))
				socket.connect(InetSocketAddress("127.0.0.1", advertisedPort))
				val input = socket.getInputStream()
				val output = socket.getOutputStream()
				while (received.size() < size) {
					val chunk = input.readNBytes(min(2048, size - received.size()))
					received.write(chunk)
					if (!firstChunk.isCompleted) {
						firstChunk.complete(Unit)
						beforeFirstAck()
					}
					output.write(ByteArray(4))
					output.flush()
				}
				output.write("OK".toByteArray())
				output.flush()
			}
		}
	}
}

private fun bindDeviceUdp(ip: String, port: Int): DatagramSocket? = try {
	DatagramSocket(InetSocketAddress(ip, port))
} catch (_: Exception) {
	null
}

class OtaUploadTest {
	@Test
	fun `uploadFirmware writes complete payload across chunk boundaries`() = runTest {
		val firmware = ByteArray(2048 + 17) { index -> index.toByte() }
		val output = ByteChannel(autoFlush = true)
		val input = ByteChannel(autoFlush = true)
		val progress = mutableListOf<Int>()

		repeat(2) {
			input.writeFully(ByteArray(4))
		}
		input.writeFully("OK".encodeToByteArray())
		input.close()

		assertTrue(
			uploadFirmware(
				output = output,
				input = input,
				firmware = firmware,
				onProgress = { value -> progress += value },
			),
		)

		output.close()
		assertContentEquals(firmware, output.readRemaining().readByteArray())
		assertEquals(listOf(0, 99), progress)
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `waitForConnected ignores timed out status`() = runTest {
		val vrServer = buildTestVrServerStub(backgroundScope)
		val device = addUdpDevice(vrServer, backgroundScope, macAddress = "AA:BB:CC:DD:EE:FF")
		var result: Boolean? = null

		val job = backgroundScope.launch {
			result = waitForConnected(vrServer, "AA:BB:CC:DD:EE:FF", timeoutMs = 1_000)
		}

		runCurrent()
		assertFalse(job.isCompleted)

		device.context.dispatch(DeviceActions.Update { copy(status = TrackerStatus.TIMED_OUT) })
		runCurrent()
		assertFalse(job.isCompleted)

		device.context.dispatch(DeviceActions.Update { copy(status = TrackerStatus.OK) })
		runCurrent()

		assertEquals(true, result)
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	@Test
	fun `waitForReconnected requires offline then online transition`() = runTest {
		val vrServer = buildTestVrServerStub(backgroundScope)
		val device = addUdpDevice(vrServer, backgroundScope)
		device.context.dispatch(DeviceActions.Update { copy(status = TrackerStatus.OK) })
		var result: Boolean? = null

		val job = backgroundScope.launch {
			result = waitForReconnected(
				vrServer,
				device.context.state.value.id.toUShort(),
				timeoutMs = 1_000,
			)
		}

		runCurrent()
		assertFalse(job.isCompleted)

		device.context.dispatch(DeviceActions.Update { copy(status = TrackerStatus.TIMED_OUT) })
		runCurrent()
		assertFalse(job.isCompleted)

		device.context.dispatch(DeviceActions.Update { copy(status = TrackerStatus.OK) })
		runCurrent()

		assertEquals(true, result)
	}

	@Test
	fun `routes a connection to the job waiting on its IP`() = runOtaTest { scope ->
		val port = freeTcpPort()
		val manager = buildOtaTestManager(scope, port)
		val received = scope.async(start = CoroutineStart.UNDISPATCHED) { manager.otaConnections.first() }

		manager.context.dispatch(otaJob("127.0.0.1", FirmwareUpdateStatus.AUTHENTICATING))

		withContext(Dispatchers.IO) { Socket("127.0.0.1", port) }.use {
			val connection = withTimeout(5_000) { received.await() }
			assertEquals("127.0.0.1", connection.ip)
			connection.socket.close()
		}
	}

	@Test
	fun `closes a connection from an IP nobody is waiting on`() = runOtaTest { scope ->
		val port = freeTcpPort()
		val manager = buildOtaTestManager(scope, port)
		val received = scope.async(start = CoroutineStart.UNDISPATCHED) { manager.otaConnections.first() }

		manager.context.dispatch(otaJob("10.0.0.1", FirmwareUpdateStatus.AUTHENTICATING))

		withContext(Dispatchers.IO) { Socket("127.0.0.1", port) }.use { client ->
			client.soTimeout = 5_000
			val read = withContext(Dispatchers.IO) { client.getInputStream().read() }
			assertEquals(-1, read)
		}
		assertNull(withTimeoutOrNull(200) { received.await() })
		received.cancel()
	}

	@Test
	fun `two devices update in parallel over the shared port`() = runOtaTest { scope ->
		val udpA = bindDeviceUdp("127.0.0.1", 0)!!
		val devicePort = udpA.localPort
		// Both devices need the same OTA port, on distinct loopback IPs (all of 127/8 on Linux)
		val udpB = bindDeviceUdp("127.0.0.2", devicePort)
		if (udpB == null) {
			udpA.close()
			println("Skipping: 127.0.0.2 is not bindable on this machine")
			return@runOtaTest
		}
		val deviceA = FakeOtaDevice("127.0.0.1", udpA)
		val deviceB = FakeOtaDevice("127.0.0.2", udpB)
		// Sizes not aligned to the chunk size, and different per device to prove routing
		val firmwareA = ByteArray(2048 * 3 + 17) { (it * 7).toByte() }
		val firmwareB = ByteArray(2048 * 2 + 901) { (it * 13 + 1).toByte() }

		val callbackPort = freeTcpPort()
		val manager = buildOtaTestManager(scope, callbackPort)
		manager.context.dispatch(otaJob("127.0.0.1", FirmwareUpdateStatus.AUTHENTICATING, id = 1))
		manager.context.dispatch(otaJob("127.0.0.2", FirmwareUpdateStatus.AUTHENTICATING, id = 2))

		// Neither device acks its first chunk until both have one, so the uploads must overlap
		val bothStarted: suspend () -> Unit = { listOf(deviceA.firstChunk, deviceB.firstChunk).awaitAll() }
		val devices = listOf(
			scope.async { deviceA.run(bothStarted) },
			scope.async { deviceB.run(bothStarted) },
		)

		fun transfer(device: FakeOtaDevice, firmware: ByteArray) = scope.async {
			otaTransfer(
				deviceIp = device.ip,
				firmware = firmware,
				otaConnections = manager.otaConnections,
				onStatus = { _, _ -> },
				callbackPort = callbackPort,
				devicePort = devicePort,
			)
		}
		val results = listOf(transfer(deviceA, firmwareA), transfer(deviceB, firmwareB)).awaitAll()
		devices.awaitAll()

		assertEquals(listOf(true, true), results)
		assertContentEquals(firmwareA, deviceA.received.toByteArray())
		assertContentEquals(firmwareB, deviceB.received.toByteArray())
		assertEquals(callbackPort, deviceA.advertisedPort)
		assertEquals(callbackPort, deviceB.advertisedPort)
	}
}
