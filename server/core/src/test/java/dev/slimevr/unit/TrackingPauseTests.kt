package dev.slimevr.unit

import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.unit.TrackerTestUtils.quatApproxEqual
import dev.slimevr.unit.TrackerTestUtils.vectorApproxEqual
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class TrackingPauseTests {

	val resetSource = "Unit Test"

	@Test
	fun testTrackingPause() {
		val trackers = TestTrackerSet()

		// Initialize skeleton and everything
		val hpm = HumanPoseManager(trackers.allL)
		// TODO: This being enabled makes waist position infinity initially but not
		//  later? Something weird is going on here...
		hpm.setLegTweaksEnabled(false)
		hpm.update()

		// Store expected tracker data
		val expected = mutableMapOf<Int, Pair<Quaternion, Vector3>>()
		for (tracker in hpm.computedTrackers) {
			expected.put(tracker.id, Pair(tracker.getRotation(), tracker.position))
		}

		// Pause tracking, nothing should move after this
		hpm.setPauseTracking(true, resetSource)

		// Randomize tracker orientations, this should not affect the skeleton!
		for ((i, tracker) in trackers.set.withIndex()) {
			tracker.setRotation(TrackerTestUtils.testRotFromIndex(i))
		}

		// Tick the skeleton with random tracker rotations
		hpm.update()

		// Since we paused before moving anything, the output should still be identity
		for (tracker in hpm.computedTrackers) {
			val trackerExpected = expected[tracker.id]
			assertNotNull(trackerExpected)

			val expectedRot = trackerExpected.first
			val expectedPos = trackerExpected.second

			val actualRot = tracker.getRotation()
			val actualPos = tracker.position

			assert(quatApproxEqual(expectedRot, actualRot)) {
				"\"${tracker.name}\" moved after being paused. Expected <$expectedRot>, actual <$actualRot>."
			}
			assert(vectorApproxEqual(expectedPos, actualPos)) {
				"\"${tracker.name}\" moved after being paused. Expected <$expectedPos>, actual <$actualPos>."
			}
		}
	}
}
