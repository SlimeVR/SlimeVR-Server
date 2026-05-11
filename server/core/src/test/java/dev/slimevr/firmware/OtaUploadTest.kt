package dev.slimevr.firmware

import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.buildTestVrServerStub
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.util.safeLaunch
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.io.readByteArray
import solarxr_protocol.datatypes.DeviceId
import solarxr_protocol.datatypes.TrackerStatus
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private fun addUdpDevice(
	server: VRServer,
	scope: kotlinx.coroutines.CoroutineScope,
	id: Int = server.nextHandle(),
	macAddress: String? = null,
): Device {
	val device = Device.create(
		scope = scope,
		id = id,
		address = "192.168.1.100",
		macAddress = macAddress,
		origin = DeviceOrigin.UDP,
		protocolVersion = 0,
	)
	server.context.dispatch(VRServerActions.NewDevice(device.context.state.value.id, device))
	return device
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

		val job = backgroundScope.safeLaunch {
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

		val job = backgroundScope.safeLaunch {
			result = waitForReconnected(
				vrServer,
				DeviceId(device.context.state.value.id.toUByte()),
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
}
