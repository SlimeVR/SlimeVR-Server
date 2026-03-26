package dev.slimevr.firmware.reducers

import dev.slimevr.context.createContext
import dev.slimevr.firmware.FirmwareManagerActions
import dev.slimevr.firmware.FirmwareManagerBaseBehaviour
import dev.slimevr.firmware.FirmwareManagerState
import kotlinx.coroutines.test.runTest
import solarxr_protocol.rpc.FirmwareUpdateStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FirmwareManagerReducerTest {
	private fun makeContext(scope: kotlinx.coroutines.CoroutineScope) = createContext(
		initialState = FirmwareManagerState(jobs = mapOf()),
		reducers = listOf(FirmwareManagerBaseBehaviour.reducer),
		scope = scope,
	)

	@Test
	fun `UpdateJob adds a new job`() = runTest {
		val context = makeContext(this)

		context.dispatch(FirmwareManagerActions.UpdateJob("COM1", FirmwareUpdateStatus.UPLOADING, 42))

		val job = context.state.value.jobs["COM1"]
		assertNotNull(job)
		assertEquals(FirmwareUpdateStatus.UPLOADING, job.status)
		assertEquals(42, job.progress)
	}

	@Test
	fun `UpdateJob replaces an existing job`() = runTest {
		val context = makeContext(this)

		context.dispatch(FirmwareManagerActions.UpdateJob("COM1", FirmwareUpdateStatus.DOWNLOADING, 0))
		context.dispatch(FirmwareManagerActions.UpdateJob("COM1", FirmwareUpdateStatus.UPLOADING, 75))

		val job = context.state.value.jobs["COM1"]
		assertNotNull(job)
		assertEquals(FirmwareUpdateStatus.UPLOADING, job.status)
		assertEquals(75, job.progress)
		assertEquals(1, context.state.value.jobs.size)
	}

	@Test
	fun `UpdateJob tracks multiple ports independently`() = runTest {
		val context = makeContext(this)

		context.dispatch(FirmwareManagerActions.UpdateJob("COM1", FirmwareUpdateStatus.UPLOADING, 10))
		context.dispatch(FirmwareManagerActions.UpdateJob("COM2", FirmwareUpdateStatus.DOWNLOADING, 0))

		assertEquals(2, context.state.value.jobs.size)
		assertEquals(FirmwareUpdateStatus.UPLOADING, context.state.value.jobs["COM1"]?.status)
		assertEquals(FirmwareUpdateStatus.DOWNLOADING, context.state.value.jobs["COM2"]?.status)
	}

	@Test
	fun `RemoveJob removes an existing job`() = runTest {
		val context = makeContext(this)

		context.dispatch(FirmwareManagerActions.UpdateJob("COM1", FirmwareUpdateStatus.UPLOADING, 50))
		context.dispatch(FirmwareManagerActions.RemoveJob("COM1"))

		assertNull(context.state.value.jobs["COM1"])
		assertTrue(context.state.value.jobs.isEmpty())
	}

	@Test
	fun `RemoveJob on unknown port is a no-op`() = runTest {
		val context = makeContext(this)

		context.dispatch(FirmwareManagerActions.UpdateJob("COM1", FirmwareUpdateStatus.UPLOADING, 50))
		context.dispatch(FirmwareManagerActions.RemoveJob("COM2"))

		assertEquals(1, context.state.value.jobs.size)
	}
}