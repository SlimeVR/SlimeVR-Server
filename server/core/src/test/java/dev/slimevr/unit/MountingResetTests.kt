package dev.slimevr.unit

import com.jme3.math.FastMath
import dev.slimevr.VRServer.Companion.getNextLocalTrackerId
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.udp.IMUType
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/**
 * Tests [TrackerResetsHandler.resetMounting]
 */
class MountingResetTests {

	@TestFactory
	fun testMounting(): List<DynamicTest> {
		return arrayOf(
			MountTest(Quaternion.SLIMEVR.FRONT, Quaternion.IDENTITY),
			MountTest(Quaternion.SLIMEVR.BACK, Quaternion.IDENTITY),
			MountTest(Quaternion.SLIMEVR.FRONT, q(0f, 90f, 0f)),
			MountTest(Quaternion.SLIMEVR.LEFT, q(0f, 42f, 0f)),
			MountTest(Quaternion.SLIMEVR.FRONT, q(0f, 90f, 0f)),
			MountTest(Quaternion.SLIMEVR.FRONT, q(10f, 90f, 0f)),
			MountTest(Quaternion.SLIMEVR.FRONT, q(0f, 90f, 20f))
		).map { test: MountTest ->
			DynamicTest.dynamicTest(
				"Mounting Reset Test of Tracker (Expected: ${deg(yaw(test.expected))}, reference: ${deg(yaw(test.reference))})"
			) {
				checkMounting(test.expected, test.reference)
			}
		}
	}

	private fun checkMounting(expected: Quaternion, reference: Quaternion) {
		val expectedYaw = yaw(expected)
		// Offset front mounting by the expected mounting
		val mountOffset = reference * Quaternion.SLIMEVR.FRONT.inv() * expected * frontMounting
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

		tracker.setRotation(mountOffset)
		tracker.resetsHandler.resetMounting(reference)
		val resultYaw = yaw(tracker.resetsHandler.mountRotFix)

		Assertions.assertEquals(
			expectedYaw,
			resultYaw,
			FastMath.ZERO_TOLERANCE,
			"Resulting mounting yaw is not equal to reference yaw (${deg(expectedYaw)} vs ${deg(resultYaw)})"
		)
	}

	/**
	 * Makes a radian angle positive
	 */
	private fun posRad(rot: Float): Float {
		return (if (rot < 0f) FastMath.TWO_PI + rot else rot) % FastMath.TWO_PI
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

	companion object {
		val frontMounting = q(90f, 0f, 0f)

		fun q(pitch: Float, yaw: Float, roll: Float): Quaternion {
			return EulerAngles(
				EulerOrder.YZX,
				pitch * FastMath.DEG_TO_RAD,
				yaw * FastMath.DEG_TO_RAD,
				roll * FastMath.DEG_TO_RAD
			).toQuaternion()
		}
	}

	data class MountTest(val expected: Quaternion, val reference: Quaternion)
}
