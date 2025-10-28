package dev.slimevr.unit

import com.jme3.math.FastMath
import dev.slimevr.tracking.processor.HumanPoseManager
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
}
