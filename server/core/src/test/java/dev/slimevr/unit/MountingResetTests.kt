package dev.slimevr.unit

import com.jme3.math.FastMath
import dev.slimevr.VRServer.Companion.getNextLocalTrackerId
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.udp.IMUType
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import org.junit.jupiter.api.AssertionFailureBuilder
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.math.*

/**
 * Tests [TrackerResetsHandler.resetMounting]
 *
 * Head rotation does not get reset.
 * Tracker yaw is set to head yaw on reset.
 */
class MountingResetTests {

	@TestFactory
	fun testResetAndMounting(): List<DynamicTest> {
		return directions.flatMap { e ->
			directions.map { m ->
				DynamicTest.dynamicTest(
					"Full and Mounting Reset Test of Tracker (Expected: ${deg(e)}, reference: ${deg(m)})"
				) {
					checkResetMounting(e, m)
				}
			}
		}
	}

	private fun checkResetMounting(expected: Quaternion, mountRef: Quaternion) {
		// Compute the pitch/roll for the expected mounting
		val trackerRot = (expected * (frontRot / expected))
		// Offset tracker rotation by the reference yaw
		val mountOffset = mountRef * trackerRot

		val tracker = Tracker(
			null,
			getNextLocalTrackerId(),
			"test",
			"test",
			null,
			hasRotation = true,
			isInternal = true,
			imuType = IMUType.UNKNOWN,
			needsReset = true,
			needsMounting = true
		)

		// Apply reset and mounting
		tracker.resetsHandler.resetFull(Quaternion.IDENTITY)
		tracker.setRotation(mountOffset)
		tracker.resetsHandler.resetMounting(mountRef)

		val expectedYaw = yaw(expected)
		val resultYaw = yaw(tracker.resetsHandler.mountRotFix)
		assertAnglesApproxEqual(
			expectedYaw,
			resultYaw,
			"Resulting mounting yaw is not equal to reference yaw (${deg(expectedYaw)} vs ${deg(resultYaw)})"
		)
	}

	/**
	 * Makes a radian angle positive
	 */
	private fun posRad(rot: Float): Float {
		// Reduce the rotation to the smallest form
		val redRot = rot % FastMath.TWO_PI
		return abs(if (rot < 0f) FastMath.TWO_PI + redRot else redRot)
	}

	/**
	 * Gets the yaw of a rotation in radians
	 */
	private fun yaw(rot: Quaternion): Float {
		return posRad(rot.toEulerAngles(EulerOrder.YZX).y)
	}

	/**
	 * Converts radians to degrees
	 */
	private fun deg(rot: Float): Float {
		return rot * FastMath.RAD_TO_DEG
	}

	private fun deg(rot: Quaternion): Float {
		return deg(yaw(rot))
	}

	private fun anglesApproxEqual(a: Float, b: Float): Boolean {
		return FastMath.isApproxEqual(a, b) ||
			FastMath.isApproxEqual(a - FastMath.TWO_PI, b) ||
			FastMath.isApproxEqual(a, b - FastMath.TWO_PI)
	}

	private fun assertAnglesApproxEqual(expected: Float, actual: Float, message: String?) {
		if (!anglesApproxEqual(expected, actual)) {
			AssertionFailureBuilder.assertionFailure().message(message)
				.expected(expected).actual(actual).buildAndThrow()
		}
	}

	companion object {
		val directions = arrayOf(
			Quaternion.SLIMEVR.FRONT,
			Quaternion.SLIMEVR.FRONT_LEFT,
			Quaternion.SLIMEVR.LEFT,
			Quaternion.SLIMEVR.BACK_LEFT,
			Quaternion.SLIMEVR.FRONT_RIGHT,
			Quaternion.SLIMEVR.RIGHT,
			Quaternion.SLIMEVR.BACK_RIGHT,
			Quaternion.SLIMEVR.BACK
		)

		val frontRot = q(90f, 0f, 0f)

		private fun q(pitch: Float, yaw: Float, roll: Float): Quaternion {
			return EulerAngles(
				EulerOrder.YZX,
				pitch * FastMath.DEG_TO_RAD,
				yaw * FastMath.DEG_TO_RAD,
				roll * FastMath.DEG_TO_RAD
			).toQuaternion()
		}
	}
}
