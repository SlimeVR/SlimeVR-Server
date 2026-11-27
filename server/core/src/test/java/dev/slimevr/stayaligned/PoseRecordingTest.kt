package dev.slimevr.stayaligned

import dev.slimevr.config.StayAlignedPoseConfig
import dev.slimevr.config.UserConfigData
import dev.slimevr.math.angle.Angle
import dev.slimevr.solarxr.rpc.buildStayAlignedStatusResponse
import dev.slimevr.tracker.Motion
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerState
import dev.slimevr.tracker.stayaligned.checkPoseRecording
import dev.slimevr.tracker.stayaligned.missingRequiredParts
import dev.slimevr.tracker.stayaligned.poses.PlayerPose
import dev.slimevr.tracker.stayaligned.poses.RelaxedPose
import dev.slimevr.tracker.stayaligned.poses.stayAlignedPoseConfig
import dev.slimevr.tracker.stayaligned.recordedPoseConfig
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.StayAlignedDetectedPoseState
import solarxr_protocol.rpc.StayAlignedPart
import solarxr_protocol.rpc.StayAlignedPoseRecordingFailure
import solarxr_protocol.rpc.StayAlignedPoseState
import solarxr_protocol.rpc.StayAlignedRelaxedPose
import solarxr_protocol.rpc.StayAlignedTrackerCorrection
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests what Stay Aligned decides about the trackers: whether a pose can be recorded, which angles
 * it gets, how the poses start, and what the status reports.
 */
class PoseRecordingTest {

	private fun tracker(part: BodyPart): TrackerState = Tracker.DEFAULT_STATE.copy(
		bodyPart = part,
		motion = Motion.RESTING,
		status = TrackerStatus.OK,
		rotation = Quaternion.IDENTITY,
		stayAlignedData = Tracker.DEFAULT_STATE.stayAlignedData.copy(lockedRotation = Quaternion.IDENTITY),
	)

	private fun yaw(radians: Float) = EulerAngles(EulerOrder.YZX, 0f, radians, 0f).toQuaternion()

	private val standingTrackers = listOf(
		BodyPart.HIP,
		BodyPart.LEFT_UPPER_LEG,
		BodyPart.RIGHT_UPPER_LEG,
		BodyPart.LEFT_LOWER_LEG,
		BodyPart.RIGHT_LOWER_LEG,
	).map { tracker(it) }

	private fun withTracker(part: BodyPart, change: (TrackerState) -> TrackerState) = standingTrackers.map { if (it.bodyPart == part) change(it) else it }

	private val unknownPosition = withTracker(BodyPart.LEFT_UPPER_LEG) { it.copy(rotation = Quaternion.rotationAroundZAxis(Math.PI.toFloat() / 2)) }
	private val standingRecorded = UserConfigData(stayAlignedPoses = mapOf(StayAlignedRelaxedPose.STANDING to StayAlignedPoseConfig(recorded = true, enabled = true)))

	@Test
	fun `standing still is ready to record standing`() {
		assertEquals(StayAlignedPoseRecordingFailure.NONE, checkPoseRecording(standingTrackers, StayAlignedRelaxedPose.STANDING).failure)
	}

	@Test
	fun `a tracker that moves is reported before the wrong pose`() {
		val trackers = withTracker(BodyPart.HIP) { it.copy(motion = Motion.ROTATING) }

		assertEquals(StayAlignedPoseRecordingFailure.NOT_STILL, checkPoseRecording(trackers, StayAlignedRelaxedPose.SITTING).failure)
	}

	@Test
	fun `another pose than the requested one is the wrong pose`() {
		assertEquals(StayAlignedPoseRecordingFailure.WRONG_POSE, checkPoseRecording(standingTrackers, StayAlignedRelaxedPose.SITTING).failure)
	}

	@Test
	fun `a position that matches no pose is unknown, not wrong`() {
		assertEquals(StayAlignedPoseRecordingFailure.POSITION_UNKNOWN, checkPoseRecording(unknownPosition, StayAlignedRelaxedPose.STANDING).failure)
	}

	@Test
	fun `a missing required tracker is listed and stops the recording, missing feet are fine`() {
		val trackers = standingTrackers.filter { it.bodyPart != BodyPart.LEFT_LOWER_LEG }

		assertEquals(listOf(StayAlignedPart.LEFT_LOWER_LEG), missingRequiredParts(trackers))
		assertEquals(StayAlignedPoseRecordingFailure.MISSING_TRACKERS, checkPoseRecording(trackers, StayAlignedRelaxedPose.STANDING).failure)
	}

	@Test
	fun `a recorded pose is measured from the locked rotations`() {
		val trackers = standingTrackers.map {
			when (it.bodyPart) {
				BodyPart.LEFT_UPPER_LEG -> it.copy(rotation = yaw(0f), stayAlignedData = it.stayAlignedData.copy(lockedRotation = yaw(0.2f)))
				BodyPart.RIGHT_UPPER_LEG -> it.copy(rotation = yaw(0f), stayAlignedData = it.stayAlignedData.copy(lockedRotation = yaw(-0.2f)))
				else -> it
			}
		}

		assertTrue(abs(recordedPoseConfig(trackers).upperLegAngleInDeg - Math.toDegrees(0.2).toFloat()) < 0.1f)
	}

	@Test
	fun `kneeling starts usable and goes back to that on reset`() {
		val recorded = UserConfigData(stayAlignedPoses = mapOf(StayAlignedRelaxedPose.KNEELING to StayAlignedPoseConfig(recorded = true, enabled = true, upperLegAngleInDeg = 12f)))
		val reset = recorded.copy(stayAlignedPoses = recorded.stayAlignedPoses - StayAlignedRelaxedPose.KNEELING)

		assertEquals(StayAlignedPoseConfig(enabled = true), stayAlignedPoseConfig(UserConfigData(), StayAlignedRelaxedPose.KNEELING))
		assertEquals(StayAlignedPoseConfig(enabled = true), stayAlignedPoseConfig(reset, StayAlignedRelaxedPose.KNEELING))
	}

	@Test
	fun `a pose that is turned off has no relaxed angles`() {
		val config = UserConfigData(stayAlignedPoses = mapOf(StayAlignedRelaxedPose.STANDING to StayAlignedPoseConfig(recorded = true, enabled = false)))

		assertNull(RelaxedPose.forPose(PlayerPose.STANDING, config))
	}

	@Test
	fun `status reports the tracker turned the most, rounded`() {
		val trackers = withTracker(BodyPart.LEFT_LOWER_LEG) { it.copy(stayAlignedData = it.stayAlignedData.copy(yawCorrection = Angle.ofDeg(-4.24f))) }

		assertEquals(StayAlignedTrackerCorrection(BodyPart.LEFT_LOWER_LEG, 4.2f), buildStayAlignedStatusResponse(true, trackers, standingRecorded).largestCorrection)
		assertNull(buildStayAlignedStatusResponse(false, trackers, standingRecorded).largestCorrection)
		assertNull(buildStayAlignedStatusResponse(true, standingTrackers, standingRecorded).largestCorrection)
	}
}
