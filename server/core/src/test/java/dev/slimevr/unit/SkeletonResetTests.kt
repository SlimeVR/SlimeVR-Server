package dev.slimevr.unit

import com.jme3.math.FastMath
import dev.slimevr.VRServer.Companion.getNextLocalTrackerId
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerStatus
import dev.slimevr.tracking.trackers.udp.IMUType
import io.eiren.util.collections.FastList
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import org.junit.jupiter.api.Test

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
			val actual = tracker.getRotation()
			assert(quatEqual(trackRot1, actual)) {
				"\"${tracker.name}\" did not reset to the reference rotation. Expected <$trackRot1>, actual <$actual>."
			}
		}
	}

	fun mkTrack(pos: TrackerPosition, hmd: Boolean = false, computed: Boolean = false, reset: Boolean = true): Tracker {
		val name = "test ${pos.designation}"
		val tracker = Tracker(
			null,
			getNextLocalTrackerId(),
			name,
			name,
			pos,
			hasPosition = hmd,
			hasRotation = true,
			isComputed = computed,
			imuType = IMUType.UNKNOWN,
			needsReset = true,
			needsMounting = reset,
			isHmd = hmd,
			trackRotDirection = false,
		)
		tracker.status = TrackerStatus.OK
		return tracker
	}

	fun quatEqual(q1: Quaternion, q2: Quaternion, tolerance: Float = FastMath.ZERO_TOLERANCE): Boolean =
		FastMath.isApproxEqual(q1.w, q2.w, tolerance) &&
			FastMath.isApproxEqual(q1.x, q2.x, tolerance) &&
			FastMath.isApproxEqual(q1.y, q2.y, tolerance) &&
			FastMath.isApproxEqual(q1.z, q2.z, tolerance)
}
