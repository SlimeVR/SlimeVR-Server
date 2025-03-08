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
import kotlin.random.Random

class SkeletonResetTests {

	val resetSource = "Unit Test"

	@Test
	fun testSkeletonReset() {
		val rand = Random(42)

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

		val headRot1 = EulerAngles(EulerOrder.YZX, 0f, FastMath.HALF_PI, FastMath.QUARTER_PI).toQuaternion()
		val expectRot1 = EulerAngles(EulerOrder.YZX, 0f, FastMath.HALF_PI, 0f).toQuaternion()

		for (tracker in tracks) {
			val init = EulerAngles(
				EulerOrder.YZX,
				rand.nextFloat() * FastMath.TWO_PI,
				rand.nextFloat() * FastMath.TWO_PI,
				rand.nextFloat() * FastMath.TWO_PI,
			).toQuaternion()
			tracker.setRotation(init)
		}
		hmd.setRotation(headRot1)
		hpm.resetTrackersFull(resetSource)

		for (tracker in tracks) {
			val actual = tracker.getRotation()
			assert(quatEqual(expectRot1, actual)) {
				"\"${tracker.name}\" did not reset to the reference rotation. Expected <$expectRot1>, actual <$actual>."
			}
		}

		for (tracker in tracks) {
			val init = EulerAngles(
				EulerOrder.YZX,
				rand.nextFloat() * FastMath.TWO_PI,
				rand.nextFloat() * FastMath.TWO_PI,
				rand.nextFloat() * FastMath.TWO_PI,
			).toQuaternion()
			tracker.setRotation(init)
		}
		hmd.setRotation(Quaternion.IDENTITY)
		hpm.resetTrackersYaw(resetSource)

		for (tracker in tracks) {
			val yaw = tracker.getRotation().toEulerAngles(EulerOrder.YZX).y
			assert(FastMath.isApproxZero(yaw)) {
				"\"${tracker.name}\" did not reset to the reference rotation. Expected <0f>, actual <$yaw>."
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
