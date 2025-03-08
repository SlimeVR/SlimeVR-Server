package dev.slimevr.unit

import dev.slimevr.VRServer.Companion.getNextLocalTrackerId
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.udp.IMUType
import io.eiren.util.collections.FastList
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SkeletonResetTests {

	val resetSource = "Unit Test"

	@Test
	fun testSkeletonReset() {
		val hmd = mkTrack(TrackerPosition.HEAD, true, true, false)
		val chest = mkTrack(TrackerPosition.CHEST)
		val hip = mkTrack(TrackerPosition.HIP)

		val upperLeft = mkTrack(TrackerPosition.LEFT_UPPER_LEG)
		val lowerLeft = mkTrack(TrackerPosition.LEFT_LOWER_LEG)

		val upperRight = mkTrack(TrackerPosition.RIGHT_UPPER_LEG)
		val lowerRight = mkTrack(TrackerPosition.RIGHT_LOWER_LEG)

		// Collect all our trackers
		val tracks = arrayOf(chest, hip, upperLeft, lowerLeft, upperRight, lowerRight)
		val tracksWithHmd = tracks.plus(hmd)
		val trackerList = FastList(tracksWithHmd)

		// Initialize skeleton and everything
		val hpm = HumanPoseManager(trackerList)

		val headRot1 = EulerAngles(EulerOrder.YZX, 0f, 90f, 15f).toQuaternion()
		val trackRot1 = EulerAngles(EulerOrder.YZX, 0f, 90f, 0f).toQuaternion()
		hmd.setRotation(headRot1)
		hpm.resetTrackersFull(resetSource)

		for (tracker in tracks) {
			assertEquals(trackRot1.toEulerAngles(EulerOrder.YZX), tracker.getRotation().toEulerAngles(EulerOrder.YZX), "\"${tracker.name}\" did not reset to the reference rotation.")
		}
	}

	fun mkTrack(pos: TrackerPosition, hmd: Boolean = false, computed: Boolean = false, reset: Boolean = true): Tracker {
		val name = "test ${pos.designation}"
		return Tracker(
			null,
			getNextLocalTrackerId(),
			name,
			name,
			pos,
			hasPosition = hmd,
			hasRotation = true,
			isComputed = computed,
			imuType = IMUType.UNKNOWN,
			needsReset = reset,
			needsMounting = reset,
			isHmd = hmd,
			trackRotDirection = false,
		)
	}
}
