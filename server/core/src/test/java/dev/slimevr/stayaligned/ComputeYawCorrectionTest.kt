package dev.slimevr.stayaligned

import dev.slimevr.config.StayAlignedConfig
import dev.slimevr.config.StayAlignedRelaxedPoseConfig
import dev.slimevr.math.angle.Angle
import dev.slimevr.skeleton.BoneRegistry
import dev.slimevr.skeleton.boneId
import dev.slimevr.tracker.Motion
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.stayaligned.TrackerYawCorrection
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests the entry point of Stay Aligned
 */
class ComputeYawCorrectionTest {

	val registry = BoneRegistry.standard()

	val enabledStayAlignedConfig = StayAlignedConfig(
		enabled = true,
		standingRelaxedPose = StayAlignedRelaxedPoseConfig(true),
		sittingRelaxedPose = StayAlignedRelaxedPoseConfig(true),
		flatRelaxedPose = StayAlignedRelaxedPoseConfig(true),
		setupComplete = true,
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
		boneId = BodyPart.HIP.boneId,
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
			enabledStayAlignedConfig,
			registry,
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
			enabledStayAlignedConfig,
			registry,
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
			enabledStayAlignedConfig,
			registry,
		)

		assertNull(result)
	}

	@Test
	fun `computeYawCorrection with moving tracker OK`() = runTest {
		val result = TrackerYawCorrection.computeYawCorrection(
			movingTrackerState,
			listOf(
				movingTrackerState,
				movingTrackerState.copy(boneId = BodyPart.LEFT_UPPER_LEG.boneId, rotation = fiveYaw),
				movingTrackerState.copy(boneId = BodyPart.RIGHT_UPPER_LEG.boneId, rotation = fiveYaw),
				movingTrackerState.copy(boneId = BodyPart.LEFT_LOWER_LEG.boneId, rotation = fiveYaw),
				movingTrackerState.copy(boneId = BodyPart.RIGHT_LOWER_LEG.boneId, rotation = fiveYaw),
			),
			Angle(0.002f),
			enabledStayAlignedConfig,
			registry,
		)

		assertNotNull(result)
		assertNotEquals(0f, result.toDeg())
	}
}
