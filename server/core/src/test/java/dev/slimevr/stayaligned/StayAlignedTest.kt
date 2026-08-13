package dev.slimevr.stayaligned

import dev.slimevr.buildTestAppContext
import dev.slimevr.buildTestSettings
import dev.slimevr.buildTestTracker
import dev.slimevr.buildTestVrServerStub
import dev.slimevr.config.StayAlignedConfig
import dev.slimevr.config.StayAlignedRelaxedPoseConfig
import dev.slimevr.math.angle.Angle
import dev.slimevr.tracker.Motion
import dev.slimevr.tracker.SessionCalibration
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.behaviours.TrackerStayAlignedBehaviour
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class StayAlignedTest {

	val enabledStayAlignedConfig = StayAlignedConfig(
		enabled = true,
		standingRelaxedPose = StayAlignedRelaxedPoseConfig(true),
		sittingRelaxedPose = StayAlignedRelaxedPoseConfig(true),
		flatRelaxedPose = StayAlignedRelaxedPoseConfig(true),
		setupComplete = true,
	)

	private fun buildLockedTracker(scope: CoroutineScope): Tracker {
		val server = buildTestVrServerStub(scope)
		val appContext = buildTestAppContext(server)
		val settings = buildTestSettings(scope)
		val tracker = buildTestTracker(
			scope,
			appContext,
			settings,
			id = 0,
			bodyPart = BodyPart.HIP,
			status = TrackerStatus.OK,
			additionalBehaviours = listOf(TrackerStayAlignedBehaviour(settings)),
			sessionCalibration = SessionCalibration(),
			motion = Motion.RESTING,
			stayAlignedData = Tracker.DEFAULT_STATE.stayAlignedData.copy(lockedRotation = EulerAngles(EulerOrder.YZX, 0f, 0.5f, 0f).toQuaternion()),
		)
		tracker.startObserving()
		return tracker
	}

	val aTrackerState = Tracker.DEFAULT_STATE.copy(
		bodyPart = BodyPart.LEFT_UPPER_LEG,
		status = TrackerStatus.OK,
	)

	val bTrackerState = Tracker.DEFAULT_STATE.copy(
		bodyPart = BodyPart.RIGHT_UPPER_LEG,
		status = TrackerStatus.OK,
	)

	@Test
	fun `CorrectTrackerYaw adjust with a locked tracker`() = runTest {
		val tracker = buildLockedTracker(backgroundScope)
		CorrectTrackerYaw.adjust(
			tracker,
			listOf(tracker.context.state.value, aTrackerState, bTrackerState),
			Angle(0.007f),
			enabledStayAlignedConfig,
		)

//		assertNotEquals(0f, tracker.context.state.value.stayAlignedData.yawCorrection.toDeg())
	}
}
