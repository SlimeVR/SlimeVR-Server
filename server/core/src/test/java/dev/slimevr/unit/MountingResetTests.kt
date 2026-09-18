package dev.slimevr.unit

import com.jme3.math.FastMath
import dev.slimevr.VRServer.Companion.getNextLocalTrackerId
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.udp.IMUType
import dev.slimevr.unit.TrackerTestUtils.assertAngleEquals
import dev.slimevr.unit.TrackerTestUtils.degYaw
import dev.slimevr.unit.TrackerTestUtils.radToDeg
import dev.slimevr.unit.TrackerTestUtils.radYaw
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * Tests [TrackerResetsHandler.resetMounting]
 *
 * Head rotation does not get reset.
 * Tracker yaw is set to head yaw on reset.
 */
class MountingResetTests {

	@TestFactory
	fun testResetAndMounting(): List<DynamicTest> = TrackerTestUtils.directions.flatMap { e ->
		TrackerTestUtils.directions.map { m ->
			DynamicTest.dynamicTest(
				"Full and Mounting Reset Test of Tracker (Expected: ${degYaw(e)}, reference: ${degYaw(m)})",
			) {
				checkResetMounting(e, m)
			}
		}
	}

	private fun checkResetMounting(expected: Quaternion, reference: Quaternion) {
		// Compute the pitch/roll for the expected mounting
		val trackerRot = expected * TrackerTestUtils.frontRot / expected

		val tracker = Tracker(
			null,
			getNextLocalTrackerId(),
			"test",
			"test",
			null,
			hasRotation = true,
			imuType = IMUType.UNKNOWN,
			allowReset = true,
			allowMounting = true,
			trackRotDirection = false,
		)

		// Apply full reset and mounting
		tracker.setRotation(Quaternion.IDENTITY)
		tracker.resetsHandler.resetFull(Quaternion.IDENTITY)
		tracker.setRotation(trackerRot)
		tracker.resetsHandler.resetMounting(Quaternion.IDENTITY)

		val expectedYaw = radYaw(expected)
		val resultYaw = radYaw(tracker.resetsHandler.mountRotFix)
		assertAngleEquals(
			expectedYaw,
			resultYaw,
			message = "Resulting mounting yaw after full reset is not equal to reference yaw (${radToDeg(expectedYaw)} vs ${radToDeg(resultYaw)})",
		)

		// Apply full reset and mounting plus offset
		tracker.setRotation(Quaternion.IDENTITY)
		tracker.resetsHandler.resetFull(reference)
		// Apply an offset of reference to the rotation
		tracker.setRotation(reference * trackerRot)
		// Since reference is the offset from quat identity (reset) and the rotation,
		// it needs to be applied twice
		tracker.resetsHandler.resetMounting(reference * reference)

		val expectedYaw2 = radYaw(expected)
		val resultYaw2 = radYaw(tracker.resetsHandler.mountRotFix)
		assertAngleEquals(
			expectedYaw2,
			resultYaw2,
			message = "Resulting mounting yaw after full reset with offset is not equal to reference yaw (${radToDeg(expectedYaw2)} vs ${radToDeg(resultYaw2)})",
		)

		// Apply yaw reset and mounting
		tracker.setRotation(Quaternion.IDENTITY)
		tracker.resetsHandler.resetFull(reference)
		tracker.resetsHandler.resetYaw(Quaternion.IDENTITY)
		tracker.setRotation(trackerRot)
		tracker.resetsHandler.resetMounting(Quaternion.IDENTITY)

		val expectedYaw3 = radYaw(expected)
		val resultYaw3 = radYaw(tracker.resetsHandler.mountRotFix)
		assertAngleEquals(
			expectedYaw3,
			resultYaw3,
			message = "Resulting mounting yaw after yaw reset is not equal to reference yaw (${radToDeg(expectedYaw3)} vs ${radToDeg(resultYaw3)})",
		)

		// Apply yaw reset and mounting plus offset
		tracker.setRotation(Quaternion.IDENTITY)
		tracker.resetsHandler.resetFull(Quaternion.IDENTITY)
		tracker.resetsHandler.resetYaw(reference)
		// Apply an offset of reference to the rotation
		tracker.setRotation(reference * trackerRot)
		// Since reference is the offset from quat identity (reset) and the rotation,
		// it needs to be applied twice
		tracker.resetsHandler.resetMounting(reference * reference)

		val expectedYaw4 = radYaw(expected)
		val resultYaw4 = radYaw(tracker.resetsHandler.mountRotFix)
		assertAngleEquals(
			expectedYaw3,
			resultYaw3,
			message = "Resulting mounting yaw after yaw reset with offset is not equal to reference yaw (${radToDeg(expectedYaw4)} vs ${radToDeg(resultYaw4)})",
		)
	}

	@Test
	fun testYawAfter() {
		val expected = Quaternion.SLIMEVR.RIGHT
		val reference = EulerAngles(EulerOrder.YZX, FastMath.PI / 8f, FastMath.HALF_PI, 0f).toQuaternion()
		// Compute the pitch/roll for the expected mounting
		val trackerRot = expected * TrackerTestUtils.frontRot / expected

		val tracker = Tracker(
			null,
			getNextLocalTrackerId(),
			"test",
			"test",
			null,
			hasRotation = true,
			imuType = IMUType.UNKNOWN,
			allowReset = true,
			allowMounting = true,
			trackRotDirection = false,
		)

		// Apply full reset and mounting
		tracker.setRotation(Quaternion.IDENTITY)
		tracker.resetsHandler.resetFull(Quaternion.IDENTITY)
		tracker.setRotation(trackerRot)
		tracker.resetsHandler.resetMounting(Quaternion.IDENTITY)

		val expectedYaw = radYaw(expected)
		val resultYaw = radYaw(tracker.resetsHandler.mountRotFix)
		assertAngleEquals(
			expectedYaw,
			resultYaw,
			message = "Resulting mounting yaw after full reset is not equal to reference yaw (${radToDeg(expectedYaw)} vs ${radToDeg(resultYaw)})",
		)

		tracker.setRotation(reference * reference)
		tracker.resetsHandler.resetYaw(reference)

		val expectedYaw2 = radYaw(reference)
		val resultYaw2 = radYaw(tracker.getRotation())
		assertAngleEquals(
			expectedYaw2,
			resultYaw2,
			message = "Resulting rotation after yaw reset is not equal to reference yaw (${radToDeg(expectedYaw2)} vs ${radToDeg(resultYaw2)})",
		)
	}
}
