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
	fun radYaw(rot: Quaternion): Float = posRad(rot.toEulerAngles(EulerOrder.YZX).y)

	/**
	 * Converts radians to degrees
	 */
	fun radToDeg(rot: Float): Float = rot * FastMath.RAD_TO_DEG

	fun degYaw(rot: Quaternion): Float = radToDeg(radYaw(rot))

	private fun angleApproxEqual(a: Float, b: Float, tolerance: Float = FastMath.ZERO_TOLERANCE): Boolean = FastMath.isApproxEqual(a, b, tolerance) ||
		FastMath.isApproxEqual(a - FastMath.TWO_PI, b, tolerance) ||
		FastMath.isApproxEqual(a, b - FastMath.TWO_PI, tolerance)

	fun assertAngleEquals(expected: Float, actual: Float, tolerance: Float = FastMath.ZERO_TOLERANCE, message: String? = null) {
		if (!angleApproxEqual(expected, actual, tolerance)) {
			AssertionFailureBuilder.assertionFailure().message(message)
				.expected(expected).actual(actual).buildAndThrow()
		}
	}

	fun assertAngleNotEquals(expected: Float, actual: Float, tolerance: Float = FastMath.ZERO_TOLERANCE, message: String? = null) {
		if (angleApproxEqual(expected, actual, tolerance)) {
			AssertionFailureBuilder.assertionFailure().message(message)
				.expected(expected).actual(actual).buildAndThrow()
		}
	}

	/**
	 * True if the quaternions are approximately equal in quaternion space, not rotation
	 * space.
	 */
	fun quatApproxEqual(q1: Quaternion, q2: Quaternion, tolerance: Float = FastMath.ZERO_TOLERANCE): Boolean = FastMath.isApproxEqual(q1.w, q2.w, tolerance) &&
		FastMath.isApproxEqual(q1.x, q2.x, tolerance) &&
		FastMath.isApproxEqual(q1.y, q2.y, tolerance) &&
		FastMath.isApproxEqual(q1.z, q2.z, tolerance)

	fun assertQuatEquals(expected: Quaternion, actual: Quaternion, tolerance: Float = FastMath.ZERO_TOLERANCE, message: String? = null) {
		if (!quatApproxEqual(expected, actual, tolerance)) {
			AssertionFailureBuilder.assertionFailure().message(message)
				.expected(expected).actual(actual).buildAndThrow()
		}
	}

	fun assertQuatNotEquals(expected: Quaternion, actual: Quaternion, tolerance: Float = FastMath.ZERO_TOLERANCE, message: String? = null) {
		if (quatApproxEqual(expected, actual, tolerance)) {
			AssertionFailureBuilder.assertionFailure().message(message)
				.expected(expected).actual(actual).buildAndThrow()
		}
	}

	fun vectorApproxEqual(v1: Vector3, v2: Vector3, tolerance: Float = FastMath.ZERO_TOLERANCE): Boolean = FastMath.isApproxEqual(v1.x, v2.x, tolerance) &&
		FastMath.isApproxEqual(v1.y, v2.y, tolerance) &&
		FastMath.isApproxEqual(v1.z, v2.z, tolerance)

	fun assertVectorEquals(expected: Vector3, actual: Vector3, tolerance: Float = FastMath.ZERO_TOLERANCE, message: String? = null) {
		if (!vectorApproxEqual(expected, actual, tolerance)) {
			AssertionFailureBuilder.assertionFailure().message(message)
				.expected(expected).actual(actual).buildAndThrow()
		}
	}

	fun assertVectorNotEquals(expected: Vector3, actual: Vector3, tolerance: Float = FastMath.ZERO_TOLERANCE, message: String? = null) {
		if (vectorApproxEqual(expected, actual, tolerance)) {
			AssertionFailureBuilder.assertionFailure().message(message)
				.expected(expected).actual(actual).buildAndThrow()
		}
	}
}
