package dev.slimevr.unit

import com.jme3.math.FastMath
import dev.slimevr.VRServer.Companion.getNextLocalTrackerId
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerStatus
import dev.slimevr.tracking.trackers.udp.IMUType
import dev.slimevr.unit.TrackerUtils.assertAnglesApproxEqual
import dev.slimevr.unit.TrackerUtils.quatApproxEqual
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

		// Randomize tracker orientations, these should be zeroed and matched to the
		// headset yaw by full reset
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
			assert(quatApproxEqual(expectRot1, actual)) {
				"\"${tracker.name}\" did not reset to the reference rotation. Expected <$expectRot1>, actual <$actual>."
			}
		}

		// Randomize full tracker orientations, these should match the headset yaw but
		// retain orientation otherwise
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
			val yaw = TrackerUtils.yaw(tracker.getRotation())
			assertAnglesApproxEqual(0f, yaw, "\"${tracker.name}\" did not reset to the reference rotation.")
		}
	}

	@Test
	fun testSkeletonMount() {
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

		// Just a bunch of random mounting orientations
		val expected = arrayOf(
			Pair(chest, Quaternion.SLIMEVR.FRONT),
			Pair(hip, Quaternion.SLIMEVR.RIGHT),
			Pair(upperLeft, Quaternion.SLIMEVR.BACK),
			Pair(lowerLeft, Quaternion.SLIMEVR.LEFT),
			Pair(upperRight, Quaternion.SLIMEVR.FRONT),
			Pair(lowerRight, Quaternion.SLIMEVR.RIGHT),
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
				-> mountRot * Quaternion.SLIMEVR.FRONT

				TrackerPosition.LEFT_UPPER_LEG,
				TrackerPosition.RIGHT_UPPER_LEG,
				-> mountRot

				else -> mountRot
			}
			val actualMounting = tracker.resetsHandler.mountRotFix

			// Make sure yaw matches
			val expectedY = TrackerUtils.yaw(expectedMounting)
			val actualY = TrackerUtils.yaw(actualMounting)
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

	fun mkTrackMount(rot: Quaternion): Quaternion = rot * (TrackerUtils.frontRot / rot)
}
