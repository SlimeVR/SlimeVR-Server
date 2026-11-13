package dev.slimevr.stayaligned

import dev.slimevr.config.StayAlignedPoseConfig
import dev.slimevr.config.UserConfigData
import dev.slimevr.math.angle.Angle
import dev.slimevr.tracker.Motion
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.stayaligned.TrackerYawCorrection
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.StayAlignedRelaxedPose
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests the entry point of Stay Aligned
 */
class ComputeYawCorrectionTest {

	val recordedPose = StayAlignedPoseConfig(recorded = true, enabled = true)

	val userConfigWithPoses = UserConfigData(
		stayAlignedPoses = mapOf(
			StayAlignedRelaxedPose.STANDING to recordedPose,
			StayAlignedRelaxedPose.SITTING to recordedPose,
			StayAlignedRelaxedPose.FLAT to recordedPose,
		),
	)

	val pointFiveYaw = EulerAngles(
		EulerOrder.YZX,
		0f,
		0.5f,
		0f,
	).toQuaternion()

	val fiveYaw = EulerAngles(
		EulerOrder.YZX,
		0f,
		5f,
		0f,
	).toQuaternion()

	val lockedTrackerState = Tracker.DEFAULT_STATE.copy(
		motion = Motion.RESTING,
		bodyPart = BodyPart.HIP,
		status = TrackerStatus.OK,
		stayAlignedData = Tracker.DEFAULT_STATE.stayAlignedData.copy(lockedRotation = pointFiveYaw),
	)

	val primarilyAtRestTrackerState = lockedTrackerState.copy(motion = Motion.STARTED_ROTATING)

	val movingTrackerState = lockedTrackerState.copy(
		motion = Motion.ROTATING,
	)

	@Test
	fun `computeYawCorrection with locked tracker OK`() = runTest {
		val result = TrackerYawCorrection.computeYawCorrection(
			lockedTrackerState,
			listOf(lockedTrackerState),
			Angle(0.002f),
			userConfigWithPoses,
		)

		assertNotNull(result)
		assertNotEquals(0f, result.toDeg())
	}

	@Test
	fun `computeYawCorrection applyYawCorrection 0`() = runTest {
		val result = TrackerYawCorrection.computeYawCorrection(
			lockedTrackerState,
			listOf(lockedTrackerState),
			Angle(0f),
			userConfigWithPoses,
		)

		assertNotNull(result)
		assertEquals(0f, result.toDeg())
	}

	@Test
	fun `computeYawCorrection with tracker primarily at rest`() = runTest {
		val result = TrackerYawCorrection.computeYawCorrection(
			primarilyAtRestTrackerState,
			listOf(primarilyAtRestTrackerState),
			Angle(0.002f),
			userConfigWithPoses,
		)

		assertNull(result)
	}

	@Test
	fun `computeYawCorrection with moving tracker OK`() = runTest {
		val result = TrackerYawCorrection.computeYawCorrection(
			movingTrackerState,
			listOf(
				movingTrackerState,
				movingTrackerState.copy(bodyPart = BodyPart.LEFT_UPPER_LEG, rotation = fiveYaw),
				movingTrackerState.copy(bodyPart = BodyPart.RIGHT_UPPER_LEG, rotation = fiveYaw),
				movingTrackerState.copy(bodyPart = BodyPart.LEFT_LOWER_LEG, rotation = fiveYaw),
				movingTrackerState.copy(bodyPart = BodyPart.RIGHT_LOWER_LEG, rotation = fiveYaw),
			),
			Angle(0.002f),
			userConfigWithPoses,
		)

		assertNotNull(result)
		assertNotEquals(0f, result.toDeg())
	}
}
