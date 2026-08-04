@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package dev.slimevr.tracker

import dev.slimevr.buildTestAppContext
import dev.slimevr.buildTestSettings
import dev.slimevr.buildTestTracker
import dev.slimevr.buildTestVrServerStub
import dev.slimevr.degreeToRadian
import dev.slimevr.quaternionAssertEquals
import dev.slimevr.tracker.behaviours.TrackerYawResetSmoothingBehaviour
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class YawResetSmoothingTest {
	private val raw = Quaternion.rotationAroundYAxis(degreeToRadian(30f))
	private val prevHeading = Quaternion.rotationAroundYAxis(degreeToRadian(90f))

	private fun buildTracker(scope: CoroutineScope, withCalibration: Boolean): Tracker {
		val server = buildTestVrServerStub(scope)
		val appContext = buildTestAppContext(server)
		val settings = buildTestSettings(scope)
		return buildTestTracker(
			scope,
			appContext,
			settings,
			id = 0,
			BodyPart.CHEST,
			TrackerStatus.OK,
			rawRotation = raw,
			additionalBehaviours = listOf(TrackerYawResetSmoothingBehaviour()),
			sessionCalibration = if (withCalibration) SessionCalibration(headingCorrection = prevHeading) else null,
		)
	}

	private fun heading(tracker: Tracker): Quaternion = assertNotNull(tracker.context.state.value.sessionCalibration).headingCorrection

	@Test
	fun `yaw reset with smoothTime seeds and leaves applied heading untouched`() = runTest {
		val tracker = buildTracker(backgroundScope, true)
		val newHeading = estimateHeadingCorrect(raw, Quaternion.IDENTITY)

		tracker.context.dispatch(TrackerActions.YawReset(Quaternion.IDENTITY, 500.milliseconds))

		val smoothing = assertNotNull(tracker.context.state.value.yawResetSmoothing)
		quaternionAssertEquals(prevHeading, smoothing.from)
		quaternionAssertEquals(newHeading, smoothing.to)
		assertEquals(500.milliseconds, smoothing.duration)
		quaternionAssertEquals(prevHeading, heading(tracker))
	}

	@Test
	fun `yaw reset with zero smoothTime snaps and does not seed`() = runTest {
		val tracker = buildTracker(backgroundScope, true)
		val newHeading = estimateHeadingCorrect(raw, Quaternion.IDENTITY)

		tracker.context.dispatch(TrackerActions.YawReset(Quaternion.IDENTITY, Duration.ZERO))

		assertNull(tracker.context.state.value.yawResetSmoothing)
		quaternionAssertEquals(newHeading, heading(tracker))
	}

	@Test
	fun `yaw reset without prior calibration snaps`() = runTest {
		val tracker = buildTracker(backgroundScope, false)
		val newHeading = estimateHeadingCorrect(raw, Quaternion.IDENTITY)

		tracker.context.dispatch(TrackerActions.YawReset(Quaternion.IDENTITY, 500.milliseconds))

		assertNull(tracker.context.state.value.yawResetSmoothing)
		quaternionAssertEquals(newHeading, heading(tracker))
	}

	@Test
	fun `full reset cancels in-progress smoothing`() = runTest {
		val tracker = buildTracker(backgroundScope, true)
		tracker.context.dispatch(TrackerActions.YawReset(Quaternion.IDENTITY, 500.milliseconds))
		assertNotNull(tracker.context.state.value.yawResetSmoothing)

		tracker.context.dispatch(TrackerActions.FullReset(Quaternion.IDENTITY))

		assertNull(tracker.context.state.value.yawResetSmoothing)
	}

	@Test
	fun `mounting reset leaves smoothing running untouched`() = runTest {
		val tracker = buildTracker(backgroundScope, true)
		tracker.context.dispatch(TrackerActions.YawReset(Quaternion.IDENTITY, 500.milliseconds))
		val seed = assertNotNull(tracker.context.state.value.yawResetSmoothing)

		tracker.context.dispatch(TrackerActions.MountingReset(Quaternion.IDENTITY, 0f))

		assertEquals(seed, tracker.context.state.value.yawResetSmoothing)
	}
}
