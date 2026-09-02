package dev.slimevr.hid

import dev.slimevr.VRServer
import dev.slimevr.buildTestAppConfig
import dev.slimevr.buildTestAppContext
import dev.slimevr.buildTestVrServerStub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import solarxr_protocol.data_feed.dongle_data.DongleStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val RECEIVER_VID = 0x1209
private const val RECEIVER_PID = 0x7690
private const val TRACKER_PID = 0x7692

private const val PATH = "/dev/hidraw0"

private fun descriptor(key: String, productId: Int, serial: String?) = HidDeviceDescriptor(
	key = key,
	vendorId = RECEIVER_VID,
	productId = productId,
	serialNumber = serial,
)

private fun registerFrame(hidId: Int, address: Long): ByteArray {
	val frame = ByteArray(16)
	frame[0] = 0xFF.toByte()
	frame[1] = hidId.toByte()
	for (byte in 0 until 6) frame[2 + byte] = (address shr (byte * 8) and 0xFF).toByte()
	return frame
}

private class FakeHidConnection(private val onClose: () -> Unit) : HidConnection {
	val frames = ArrayDeque<ByteArray>()

	override suspend fun read(buffer: ByteArray, timeoutMs: Int): Int {
		val frame = frames.removeFirstOrNull()
		if (frame == null) {
			delay(timeoutMs.toLong())
			return 0
		}
		frame.copyInto(buffer)
		return frame.size
	}

	override suspend fun write(data: ByteArray): Int = data.size

	override suspend fun close() = onClose()
}

private class FakeHidTransport : HidTransport {
	val present = mutableMapOf<String, HidDeviceDescriptor>()
	val connections = mutableMapOf<String, FakeHidConnection>()
	val closed = mutableSetOf<String>()

	var openCount = 0
		private set

	var shutdownCalled = false
		private set

	override val wakeSignal: ReceiveChannel<Unit>? = null

	override suspend fun enumerate(directTrackersEnabled: Boolean): Map<String, HidDeviceDescriptor> = present.toMap()

	override suspend fun open(descriptor: HidDeviceDescriptor): HidConnection {
		openCount++
		return FakeHidConnection { closed += descriptor.key }.also { connections[descriptor.key] = it }
	}

	override suspend fun shutdown() {
		shutdownCalled = true
	}
}

private class Harness(val server: VRServer, val job: Job)

private fun startHidManager(transport: FakeHidTransport, scope: CoroutineScope): Harness {
	val server = buildTestVrServerStub(scope)
	val appContext = buildTestAppContext(server, buildTestAppConfig(scope))
	return Harness(server, runHidManager(appContext, transport, scope))
}

private fun dongles(server: VRServer) = server.context.state.value.dongles.values.toList()

@OptIn(ExperimentalCoroutinesApi::class)
class HidManagerTest {
	@Test
	fun `opens a detected receiver and forwards its packets`() = runTest {
		val fake = FakeHidTransport()
		fake.present[PATH] = descriptor(PATH, RECEIVER_PID, "DONGLE1")

		val harness = startHidManager(fake, backgroundScope)
		advanceTimeBy(100)

		val dongle = dongles(harness.server).single()
		assertEquals("DONGLE1", dongle.context.state.value.serialNumber)
		assertEquals(DongleStatus.CONNECTED, dongle.context.state.value.status)

		fake.connections.getValue(PATH).frames += registerFrame(3, 0xAABBCCDDEEFFL)
		advanceTimeBy(500)

		assertEquals("AABBCCDDEEFF", dongle.context.state.value.trackers[3]?.address)
	}

	@Test
	fun `falls back to the device key when it reports no serial`() = runTest {
		val fake = FakeHidTransport()
		fake.present[PATH] = descriptor(PATH, RECEIVER_PID, null)

		val harness = startHidManager(fake, backgroundScope)
		advanceTimeBy(100)

		assertEquals(PATH, dongles(harness.server).single().context.state.value.serialNumber)
	}

	@Test
	fun `marks the dongle disconnected once it stops being enumerated`() = runTest {
		val fake = FakeHidTransport()
		fake.present[PATH] = descriptor(PATH, RECEIVER_PID, "DONGLE1")

		val harness = startHidManager(fake, backgroundScope)
		advanceTimeBy(100)
		val dongle = dongles(harness.server).single()

		fake.present.remove(PATH)
		advanceTimeBy(4_000)

		assertEquals(DongleStatus.DISCONNECTED, dongle.context.state.value.status)
		assertTrue(PATH in fake.closed)
	}

	@Test
	fun `reuses the existing receiver when the same serial comes back`() = runTest {
		val fake = FakeHidTransport()
		fake.present[PATH] = descriptor(PATH, RECEIVER_PID, "DONGLE1")

		val harness = startHidManager(fake, backgroundScope)
		advanceTimeBy(100)
		val first = dongles(harness.server).single()

		fake.present.remove(PATH)
		advanceTimeBy(4_000)

		// Same dongle on a different port, so the key changes but the serial does not
		fake.present["/dev/hidraw1"] = descriptor("/dev/hidraw1", RECEIVER_PID, "DONGLE1")
		advanceTimeBy(4_000)

		assertEquals(1, dongles(harness.server).size)
		assertEquals(first, dongles(harness.server).single())
		assertEquals(DongleStatus.CONNECTED, first.context.state.value.status)
		assertEquals(2, fake.openCount)
	}

	@Test
	fun `reopens a receiver that stays enumerated but goes silent`() = runTest {
		val fake = FakeHidTransport()
		fake.present[PATH] = descriptor(PATH, RECEIVER_PID, "DONGLE1")

		startHidManager(fake, backgroundScope)
		// 30 empty reads at 100ms is the stall threshold, plus a poll interval to reopen
		advanceTimeBy(7_000)

		assertTrue(fake.openCount > 1, "expected a reopen, opened ${fake.openCount} time(s)")
	}

	@Test
	fun `never reopens a silent direct tracker`() = runTest {
		val fake = FakeHidTransport()
		fake.present[PATH] = descriptor(PATH, TRACKER_PID, "TRACKER1")

		val harness = startHidManager(fake, backgroundScope)
		advanceTimeBy(20_000)

		assertEquals(1, fake.openCount)
		assertTrue(dongles(harness.server).single().context.state.value.isDirect)
	}

	@Test
	fun `closes every device before shutting the transport down`() = runTest {
		val fake = FakeHidTransport()
		fake.present[PATH] = descriptor(PATH, RECEIVER_PID, "DONGLE1")

		val harness = startHidManager(fake, backgroundScope)
		advanceTimeBy(100)

		harness.job.cancelAndJoin()

		assertTrue(PATH in fake.closed)
		assertTrue(fake.shutdownCalled)
	}
}
