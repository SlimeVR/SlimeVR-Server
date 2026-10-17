package dev.slimevr.resets

import dev.slimevr.VRServerActions
import dev.slimevr.buildTestAppContext
import dev.slimevr.buildTestResetsManager
import dev.slimevr.buildTestSettings
import dev.slimevr.buildTestTracker
import dev.slimevr.buildTestVrServer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.data_feed.server.ResetAvailability
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.ResetLifecycle
import solarxr_protocol.rpc.ResetType
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ResetsTest {
	@Test
	fun `cancelling stops the running reset`() = runTest {
		val server = buildTestVrServer(backgroundScope)
		val resets = buildTestResetsManager(server, buildTestSettings(backgroundScope), backgroundScope)

		resets.scheduleReset("test", ResetType.FULL, delay = 5f)
		runCurrent()
		assertEquals(ResetLifecycle.RUNNING, resets.context.state.value.status!!.lifecycle)

		resets.cancel()
		assertEquals(ResetLifecycle.CANCELED, resets.context.state.value.status!!.lifecycle)
	}

	@Test
	fun `cancelling with nothing running does nothing`() = runTest {
		val server = buildTestVrServer(backgroundScope)
		val resets = buildTestResetsManager(server, buildTestSettings(backgroundScope), backgroundScope)

		resets.cancel()

		assertEquals(null, resets.context.state.value.status)
	}

	@Test
	fun `a new reset cancels the running one`() = runTest {
		val server = buildTestVrServer(backgroundScope)
		val resets = buildTestResetsManager(server, buildTestSettings(backgroundScope), backgroundScope)

		resets.scheduleReset("test", ResetType.FULL, delay = 5f)
		runCurrent()
		resets.scheduleReset("test", ResetType.FULL, delay = 5f, bodyParts = listOf(BodyPart.HEAD))
		runCurrent()

		val status = resets.context.state.value.status!!
		assertEquals(listOf(BodyPart.HEAD), status.bodyParts)
		assertEquals(ResetLifecycle.RUNNING, status.lifecycle)
	}

	@Test
	fun `a yaw reset needs a full reset first`() = runTest {
		val server = buildTestVrServer(backgroundScope)
		val settings = buildTestSettings(backgroundScope)
		val resets = buildTestResetsManager(server, settings, backgroundScope)
		val tracker = buildTestTracker(server.context.scope, buildTestAppContext(server), settings, id = 1, bodyPart = BodyPart.HIP, status = TrackerStatus.OK)
		server.context.dispatch(VRServerActions.NewTracker(1, tracker))
		assertEquals(ResetAvailability.AVAILABLE, resets.availability(ResetType.YAW))

		resets.context.dispatch(ResetsActions.ClearResets(listOf(ResetType.YAW)))
		assertEquals(ResetAvailability.NEEDS_FULL_RESET, resets.availability(ResetType.YAW))

		resets.scheduleReset("test", ResetType.YAW)
		runCurrent()
		assertEquals(null, resets.context.state.value.status)
	}

	@Test
	fun `yaw and mounting resets need a tracker`() = runTest {
		val server = buildTestVrServer(backgroundScope)
		val resets = buildTestResetsManager(server, buildTestSettings(backgroundScope), backgroundScope)

		assertEquals(ResetAvailability.AVAILABLE, resets.availability(ResetType.FULL))
		assertEquals(ResetAvailability.NO_TRACKERS, resets.availability(ResetType.YAW))
		assertEquals(ResetAvailability.NO_TRACKERS, resets.availability(ResetType.MOUNTING))
	}

	@Test
	fun `a mounting reset is ignored until a mounting method is picked`() = runTest {
		val server = buildTestVrServer(backgroundScope)
		val settings = buildTestSettings(backgroundScope)
		val resets = buildTestResetsManager(server, settings, backgroundScope)
		val tracker = buildTestTracker(server.context.scope, buildTestAppContext(server), settings, id = 1, bodyPart = BodyPart.HIP, status = TrackerStatus.OK)
		server.context.dispatch(VRServerActions.NewTracker(1, tracker))
		resets.context.dispatch(ResetsActions.EndReset(ResetType.FULL))

		resets.scheduleReset("test", ResetType.MOUNTING)
		runCurrent()
		assertEquals(null, resets.context.state.value.status)
	}
}
