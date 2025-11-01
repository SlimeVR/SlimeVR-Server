package dev.slimevr.unit

import com.jme3.math.FastMath
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.unit.TrackerTestUtils.assertAnglesApproxEqual
import dev.slimevr.unit.TrackerTestUtils.quatApproxEqual
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import org.junit.jupiter.api.Test

class SkeletonResetTests {

	val resetSource = "Unit Test"

	@Test
	fun testSkeletonFullReset() {
		val trackers = TestTrackerSet()

		// Initialize skeleton and everything
		val hpm = HumanPoseManager(trackers.allL)

		val headRot1 = EulerAngles(EulerOrder.YZX, 0f, FastMath.HALF_PI, FastMath.QUARTER_PI).toQuaternion()
		val expectRot1 = EulerAngles(EulerOrder.YZX, 0f, FastMath.HALF_PI, 0f).toQuaternion()

		// Randomize tracker orientations, these should be zeroed and matched to the
		// headset yaw by full reset
		for ((i, tracker) in trackers.set.withIndex()) {
			tracker.setRotation(TrackerTestUtils.testRotFromIndex(i))
		}
		trackers.head.setRotation(headRot1)
		hpm.resetTrackersFull(resetSource)

		for (tracker in trackers.set) {
			val actual = tracker.getRotation()
			assert(quatApproxEqual(expectRot1, actual)) {
				"\"${tracker.name}\" did not reset to the reference rotation. Expected <$expectRot1>, actual <$actual>."
			}
		}
	}

	@Test
	fun testSkeletonYawReset() {
		val trackers = TestTrackerSet()

		// Initialize skeleton and everything
		val hpm = HumanPoseManager(trackers.allL)

		// Randomize full tracker orientations, these should match the headset yaw but
		// retain orientation otherwise
		for ((i, tracker) in trackers.set.withIndex()) {
			// Offset index so it's different from last reset
			tracker.setRotation(TrackerTestUtils.testRotFromIndex(i))
		}
		trackers.head.setRotation(Quaternion.IDENTITY)
		hpm.resetTrackersYaw(resetSource)

		for (tracker in trackers.set) {
			val yaw = TrackerTestUtils.yaw(tracker.getRotation())
			assertAnglesApproxEqual(0f, yaw, "\"${tracker.name}\" did not reset to the reference rotation.")
		}
	}

	@Test
	fun testSkeletonMountReset() {
		val trackers = TestTrackerSet()

		// Initialize skeleton and everything
		val hpm = HumanPoseManager(trackers.allL)

		// Just a bunch of random mounting orientations
		val expected = arrayOf(
			Pair(trackers.chest, Quaternion.SLIMEVR.FRONT),
			Pair(trackers.hip, Quaternion.SLIMEVR.RIGHT),
			Pair(trackers.leftThigh, Quaternion.SLIMEVR.BACK),
			Pair(trackers.leftCalf, Quaternion.SLIMEVR.LEFT),
			Pair(trackers.rightThigh, Quaternion.SLIMEVR.FRONT),
			Pair(trackers.rightCalf, Quaternion.SLIMEVR.RIGHT),
		)
		// Rotate the tracker to fit the expected mounting orientation
		for ((tracker, mountRot) in expected) {
			tracker.setRotation(mkTrackMount(mountRot))
		}
		// Then perform a mounting reset
		hpm.resetTrackersMounting(resetSource)

		for ((tracker, mountRot) in expected) {
			// Some mounting needs to be inverted (when in a specific pose)
			// TODO: Make this less hardcoded, accept alternative poses
			val expectedMounting = when (tracker.trackerPosition) {
				TrackerPosition.CHEST,
				TrackerPosition.HIP,
				TrackerPosition.LEFT_LOWER_LEG,
				TrackerPosition.RIGHT_LOWER_LEG,
				-> mountRot

				TrackerPosition.LEFT_UPPER_LEG,
				TrackerPosition.RIGHT_UPPER_LEG,
				-> mountRot * Quaternion.SLIMEVR.FRONT

				else -> mountRot * Quaternion.SLIMEVR.FRONT
			}
			val actualMounting = tracker.resetsHandler.mountRotFix

			// Make sure yaw matches
			val expectedY = TrackerTestUtils.yaw(expectedMounting)
			val actualY = TrackerTestUtils.yaw(actualMounting)
			assertAnglesApproxEqual(expectedY, actualY, "\"${tracker.name}\" did not reset to the reference rotation.")

			// X and Z components should be zero for mounting
			assert(FastMath.isApproxZero(actualMounting.x)) {
				"\"${tracker.name}\" did not reset to the reference rotation. Expected <0.0>, actual <${actualMounting.x}>."
			}
			assert(FastMath.isApproxZero(actualMounting.z)) {
				"\"${tracker.name}\" did not reset to the reference rotation. Expected <0.0>, actual <${actualMounting.z}>."
			}
		}
	}

	fun mkTrackMount(rot: Quaternion): Quaternion = rot * TrackerTestUtils.frontRot / rot
}
