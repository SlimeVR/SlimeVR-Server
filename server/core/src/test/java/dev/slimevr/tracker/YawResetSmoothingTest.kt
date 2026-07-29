@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package dev.slimevr.tracker

import dev.slimevr.buildTestAppContext
import dev.slimevr.buildTestSettings
import dev.slimevr.buildTestVrServerStub
import dev.slimevr.context.Context
import dev.slimevr.degreeToRadian
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.quaternionAssertEquals
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.ImuType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class YawResetSmoothingTest {
	private val raw = Quaternion.rotationAroundYAxis(degreeToRadian(30f))
	private val prevHeading = Quaternion.rotationAroundYAxis(degreeToRadian(90f))

	private fun TestScope.buildTracker(withCalibration: Boolean): Tracker {
		val server = buildTestVrServerStub(backgroundScope)
		val appContext = buildTestAppContext(server)
		val settings = buildTestSettings(backgroundScope)
		val state = TrackerState(
			id = 0,
			hardwareId = "test-0",
			name = "Tracker 0",
			restOrientation = Quaternion.IDENTITY,
			rawRotation = raw,
			rotation = Quaternion.IDENTITY,
			rawAcceleration = Vector3.NULL,
			acceleration = Vector3.NULL,
			rawMagnetometer = Vector3.NULL,
			bodyPart = BodyPart.CHEST,
			mountingOrientation = Quaternion.IDENTITY,
			origin = DeviceOrigin.UDP,
			deviceId = 0,
			customName = null,
			sensorType = ImuType.BNO085,
			position = null,
			tps = 0u,
			imuTemp = null,
			status = TrackerStatus.OK,
			completedRestCalibration = true,
			magStatus = MagnetometerStatus.NOT_SUPPORTED,
			sessionCalibration = if (withCalibration) SessionCalibration(headingCorrection = prevHeading) else null,
		)
		val context = Context.create(
			initialState = state,
			scope = backgroundScope,
			behaviours = listOf(
				TrackerBasicBehaviour(),
				TrackerYawResetSmoothingBehaviour(),
			),
			name = "YawResetSmoothingTest",
		)
		val tracker = Tracker(context, appContext, settings)
		tracker.startObserving()
		return tracker
	}

	private fun heading(tracker: Tracker): Quaternion = assertNotNull(tracker.context.state.value.sessionCalibration).headingCorrection

	@Test
	fun `yaw reset with smoothTime seeds and leaves applied heading untouched`() = runTest {
		val tracker = buildTracker(withCalibration = true)
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
		val tracker = buildTracker(withCalibration = true)
		val newHeading = estimateHeadingCorrect(raw, Quaternion.IDENTITY)

		tracker.context.dispatch(TrackerActions.YawReset(Quaternion.IDENTITY, Duration.ZERO))

		assertNull(tracker.context.state.value.yawResetSmoothing)
		quaternionAssertEquals(newHeading, heading(tracker))
	}

	@Test
	fun `yaw reset without prior calibration snaps`() = runTest {
		val tracker = buildTracker(withCalibration = false)
		val newHeading = estimateHeadingCorrect(raw, Quaternion.IDENTITY)

		tracker.context.dispatch(TrackerActions.YawReset(Quaternion.IDENTITY, 500.milliseconds))

		assertNull(tracker.context.state.value.yawResetSmoothing)
		quaternionAssertEquals(newHeading, heading(tracker))
	}

	@Test
	fun `full reset cancels in-progress smoothing`() = runTest {
		val tracker = buildTracker(withCalibration = true)
		tracker.context.dispatch(TrackerActions.YawReset(Quaternion.IDENTITY, 500.milliseconds))
		assertNotNull(tracker.context.state.value.yawResetSmoothing)

		tracker.context.dispatch(TrackerActions.FullReset(Quaternion.IDENTITY))

		assertNull(tracker.context.state.value.yawResetSmoothing)
	}

	@Test
	fun `mounting reset leaves smoothing running untouched`() = runTest {
		val tracker = buildTracker(withCalibration = true)
		tracker.context.dispatch(TrackerActions.YawReset(Quaternion.IDENTITY, 500.milliseconds))
		val seed = assertNotNull(tracker.context.state.value.yawResetSmoothing)

		tracker.context.dispatch(TrackerActions.MountingReset(Quaternion.IDENTITY, 0f))

		assertEquals(seed, tracker.context.state.value.yawResetSmoothing)
	}
}
