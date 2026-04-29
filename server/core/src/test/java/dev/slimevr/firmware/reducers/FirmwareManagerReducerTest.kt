package dev.slimevr.firmware.reducers

import dev.slimevr.context.Context
import dev.slimevr.firmware.FirmwareJobStatus
import dev.slimevr.firmware.FirmwareManagerActions
import dev.slimevr.firmware.FirmwareManagerBaseBehaviour
import dev.slimevr.firmware.FirmwareManagerState
import kotlinx.coroutines.test.runTest
import solarxr_protocol.rpc.FirmwareUpdateStatus
import solarxr_protocol.rpc.SerialDevicePort
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun serialJob(port: String, status: FirmwareUpdateStatus, progress: Int = 0) = FirmwareManagerActions.UpdateJob(
	FirmwareJobStatus(
		portLocation = port,
		firmwareDeviceId = SerialDevicePort(port = port),
		status = status,
		progress = progress,
	),
)

class FirmwareManagerReducerTest {
	private fun makeContext(scope: kotlinx.coroutines.CoroutineScope) = Context.create(
		initialState = FirmwareManagerState(jobs = mapOf()),
		behaviours = listOf(FirmwareManagerBaseBehaviour),
		scope = scope,
		name = "FirmwareManagerReducerTest",
	)

	@Test
	fun `UpdateJob adds a new job`() = runTest {
		val context = makeContext(this)

		context.dispatch(serialJob("COM1", FirmwareUpdateStatus.UPLOADING, 42))

		val job = context.state.value.jobs["COM1"]
		assertNotNull(job)
		assertEquals(FirmwareUpdateStatus.UPLOADING, job.status)
		assertEquals(42, job.progress)
	}

	@Test
	fun `UpdateJob replaces an existing job`() = runTest {
		val context = makeContext(this)

		context.dispatch(serialJob("COM1", FirmwareUpdateStatus.DOWNLOADING))
		context.dispatch(serialJob("COM1", FirmwareUpdateStatus.UPLOADING, 75))

		val job = context.state.value.jobs["COM1"]
		assertNotNull(job)
		assertEquals(FirmwareUpdateStatus.UPLOADING, job.status)
		assertEquals(75, job.progress)
		assertEquals(1, context.state.value.jobs.size)
	}

	@Test
	fun `UpdateJob tracks multiple ports independently`() = runTest {
		val context = makeContext(this)

		context.dispatch(serialJob("COM1", FirmwareUpdateStatus.UPLOADING, 10))
		context.dispatch(serialJob("COM2", FirmwareUpdateStatus.DOWNLOADING))

		assertEquals(2, context.state.value.jobs.size)
		assertEquals(FirmwareUpdateStatus.UPLOADING, context.state.value.jobs["COM1"]?.status)
		assertEquals(FirmwareUpdateStatus.DOWNLOADING, context.state.value.jobs["COM2"]?.status)
	}

	@Test
	fun `RemoveJob removes an existing job`() = runTest {
		val context = makeContext(this)

		context.dispatch(serialJob("COM1", FirmwareUpdateStatus.UPLOADING, 50))
		context.dispatch(FirmwareManagerActions.RemoveJob("COM1"))

		assertNull(context.state.value.jobs["COM1"])
		assertTrue(context.state.value.jobs.isEmpty())
	}

	@Test
	fun `RemoveJob on unknown port is a no-op`() = runTest {
		val context = makeContext(this)

		context.dispatch(serialJob("COM1", FirmwareUpdateStatus.UPLOADING, 50))
		context.dispatch(FirmwareManagerActions.RemoveJob("COM2"))

		assertEquals(1, context.state.value.jobs.size)
	}
}
