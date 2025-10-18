package dev.slimevr.unit

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import org.junit.jupiter.api.AssertionFailureBuilder
import kotlin.math.abs

object TrackerTestUtils {
	val directions = arrayOf(
		Quaternion.SLIMEVR.FRONT,
		Quaternion.SLIMEVR.FRONT_LEFT,
		Quaternion.SLIMEVR.LEFT,
		Quaternion.SLIMEVR.BACK_LEFT,
		Quaternion.SLIMEVR.FRONT_RIGHT,
		Quaternion.SLIMEVR.RIGHT,
		Quaternion.SLIMEVR.BACK_RIGHT,
		Quaternion.SLIMEVR.BACK,
	)

	val frontRot = Quaternion(0.707f, 0.707f, 0f, 0f)

	// A diverse range of quaternions to be used for testing where specific rotations
	// do not matter
	val testRots = arrayOf(
		// Various rotations
		frontRot,
		Quaternion(0.707f, 0f, 0f, 0.707f),
		Quaternion(0.854f, 0.354f, 0.146f, 0.354f),
		// Pure yaw rotations
		Quaternion.SLIMEVR.FRONT,
		Quaternion.SLIMEVR.LEFT,
		Quaternion.SLIMEVR.RIGHT,
		// Axes
		Quaternion.I,
		Quaternion.K,
		// Negative axes (same rotations, different sign)
		-Quaternion.I,
		-Quaternion.K,
		// Identity
		Quaternion.IDENTITY,
	)

	fun testRotFromIndex(index: Int): Quaternion = testRots[abs(index) % testRots.size]

	/**
	 * Makes a radian angle positive
	 */
	fun posRad(rot: Float): Float {
		// Reduce the rotation to the smallest form
		val redRot = rot % FastMath.TWO_PI
		return abs(if (rot < 0f) FastMath.TWO_PI + redRot else redRot)
	}

	/**
	 * Gets the yaw of a rotation in radians
	 */
	fun yaw(rot: Quaternion): Float = posRad(rot.toEulerAngles(EulerOrder.YZX).y)

	/**
	 * Converts radians to degrees
	 */
	fun deg(rot: Float): Float = rot * FastMath.RAD_TO_DEG

	fun deg(rot: Quaternion): Float = deg(yaw(rot))

	private fun anglesApproxEqual(a: Float, b: Float): Boolean = FastMath.isApproxEqual(a, b) ||
		FastMath.isApproxEqual(a - FastMath.TWO_PI, b) ||
		FastMath.isApproxEqual(a, b - FastMath.TWO_PI)

	fun assertAnglesApproxEqual(expected: Float, actual: Float, message: String?) {
		if (!anglesApproxEqual(expected, actual)) {
			AssertionFailureBuilder.assertionFailure().message(message)
				.expected(expected).actual(actual).buildAndThrow()
		}
	}

	fun quatApproxEqual(q1: Quaternion, q2: Quaternion, tolerance: Float = FastMath.ZERO_TOLERANCE): Boolean = FastMath.isApproxEqual(q1.w, q2.w, tolerance) &&
		FastMath.isApproxEqual(q1.x, q2.x, tolerance) &&
		FastMath.isApproxEqual(q1.y, q2.y, tolerance) &&
		FastMath.isApproxEqual(q1.z, q2.z, tolerance)

	fun vectorApproxEqual(v1: Vector3, v2: Vector3, tolerance: Float = FastMath.ZERO_TOLERANCE): Boolean = FastMath.isApproxEqual(v1.x, v2.x, tolerance) &&
		FastMath.isApproxEqual(v1.y, v2.y, tolerance) &&
		FastMath.isApproxEqual(v1.z, v2.z, tolerance)
}
