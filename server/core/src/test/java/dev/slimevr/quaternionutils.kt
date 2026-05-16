package dev.slimevr

import com.jme3.math.FastMath.DEG_TO_RAD
import com.jme3.math.FastMath.RAD_TO_DEG
import com.jme3.math.FastMath.TWO_PI
import com.jme3.math.FastMath.ZERO_TOLERANCE
import com.jme3.math.FastMath.isApproxEqual
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import org.junit.jupiter.api.AssertionFailureBuilder
import org.opentest4j.AssertionFailedError
import kotlin.math.abs

/**
 * Normalizes a radian angle to be positive and within the range [0, 2π).
 *
 * @param rot The angle in radians to normalize.
 * @return The normalized positive angle in radians.
 */
fun normalizeRadian(rot: Float): Float {
	// Reduce the rotation to the smallest form
	val redRot = rot % TWO_PI
	return abs(if (rot < 0f) TWO_PI + redRot else redRot)
}

/**
 * Extracts the yaw (rotation around the Y-axis) of a quaternion in radians.
 * The result is normalized to be positive.
 *
 * @param rot The quaternion from which to extract the yaw.
 * @return The yaw angle in radians.
 */
fun radianYaw(rot: Quaternion): Float = normalizeRadian(rot.toEulerAngles(EulerOrder.YZX).y)

/**
 * Converts an angle from radians to degrees.
 *
 * @param rot The angle in radians.
 * @return The angle in degrees.
 */
fun radianToDegree(rot: Float): Float = rot * RAD_TO_DEG

/**
 * Converts an angle from degrees to radians.
 *
 * @param rot The angle in degrees.
 * @return The angle in radians.
 */
fun degreeToRadian(rot: Float): Float = rot * DEG_TO_RAD

/**
 * Extracts the yaw of a rotation and returns it in degrees.
 *
 * @param rot The quaternion from which to extract the yaw.
 * @return The yaw angle in degrees.
 */
fun degreeYaw(rot: Quaternion): Float = radianToDegree(radianYaw(rot))

/**
 * Checks if two angles are approximately equal, accounting for the 2π wrap-around.
 *
 * @param a The first angle.
 * @param b The second angle.
 * @param tolerance The allowed difference between the angles.
 * @return True if the angles are approximately equal.
 */
fun angularApproxEqual(
	a: Float,
	b: Float,
	tolerance: Float = ZERO_TOLERANCE,
): Boolean = isApproxEqual(a, b, tolerance) ||
	isApproxEqual(a - TWO_PI, b, tolerance) ||
	isApproxEqual(a, b - TWO_PI, tolerance)

/**
 * Asserts that two angles are approximately equal.
 *
 * @param expected The expected angle.
 * @param actual The actual angle.
 * @param tolerance The allowed difference.
 * @param message Optional failure message.
 * @throws AssertionFailedError if the angles are not approximately equal.
 */
fun angularAssertEquals(
	expected: Float,
	actual: Float,
	tolerance: Float = ZERO_TOLERANCE,
	message: String? = null,
) {
	if (!angularApproxEqual(expected, actual, tolerance)) {
		AssertionFailureBuilder.assertionFailure().message(message)
			.expected(expected).actual(actual).buildAndThrow()
	}
}

/**
 * Asserts that two angles are not approximately equal.
 *
 * @param expected The value that the actual angle should not be equal to.
 * @param actual The actual angle.
 * @param tolerance The allowed difference.
 * @param message Optional failure message.
 * @throws AssertionFailedError if the angles are approximately equal.
 */
fun angularAssertNotEquals(
	expected: Float,
	actual: Float,
	tolerance: Float = ZERO_TOLERANCE,
	message: String? = null,
) {
	if (angularApproxEqual(expected, actual, tolerance)) {
		AssertionFailureBuilder.assertionFailure().message(message)
			.expected(expected).actual(actual).buildAndThrow()
	}
}

/**
 * True if the quaternions are approximately equal in quaternion space, not rotation
 * space.
 *
 * @param q1 The first quaternion.
 * @param q2 The second quaternion.
 * @param tolerance The allowed difference for each component.
 * @return True if all components (w, x, y, z) are within the tolerance.
 */
fun quaternionApproxEqual(
	q1: Quaternion,
	q2: Quaternion,
	tolerance: Float = ZERO_TOLERANCE,
): Boolean = isApproxEqual(q1.w, q2.w, tolerance) &&
	isApproxEqual(q1.x, q2.x, tolerance) &&
	isApproxEqual(q1.y, q2.y, tolerance) &&
	isApproxEqual(q1.z, q2.z, tolerance)

/**
 * Asserts that two quaternions are approximately equal in quaternion space.
 *
 * @param expected The expected quaternion.
 * @param actual The actual quaternion.
 * @param tolerance The allowed difference for each component.
 * @param message Optional failure message.
 * @throws AssertionFailedError if the quaternions are not approximately equal.
 */
fun quaternionAssertEquals(
	expected: Quaternion,
	actual: Quaternion,
	tolerance: Float = ZERO_TOLERANCE,
	message: String? = null,
) {
	if (!quaternionApproxEqual(expected, actual, tolerance)) {
		AssertionFailureBuilder.assertionFailure().message(message)
			.expected(expected).actual(actual).buildAndThrow()
	}
}

/**
 * Asserts that two quaternions are not approximately equal in quaternion space.
 *
 * @param expected The quaternion that the actual value should not be equal to.
 * @param actual The actual quaternion.
 * @param tolerance The allowed difference for each component.
 * @param message Optional failure message.
 * @throws AssertionFailedError if the quaternions are approximately equal.
 */
fun quaternionAssertNotEquals(
	expected: Quaternion,
	actual: Quaternion,
	tolerance: Float = ZERO_TOLERANCE,
	message: String? = null,
) {
	if (quaternionApproxEqual(expected, actual, tolerance)) {
		AssertionFailureBuilder.assertionFailure().message(message)
			.expected(expected).actual(actual).buildAndThrow()
	}
}

/**
 * Checks if two vectors are approximately equal.
 *
 * @param v1 The first vector.
 * @param v2 The second vector.
 * @param tolerance The allowed difference for each component.
 * @return True if all components (x, y, z) are within the tolerance.
 */
fun vectorApproxEqual(
	v1: Vector3,
	v2: Vector3,
	tolerance: Float = ZERO_TOLERANCE,
): Boolean = isApproxEqual(v1.x, v2.x, tolerance) &&
	isApproxEqual(v1.y, v2.y, tolerance) &&
	isApproxEqual(v1.z, v2.z, tolerance)

/**
 * Asserts that two vectors are approximately equal.
 *
 * @param expected The expected vector.
 * @param actual The actual vector.
 * @param tolerance The allowed difference for each component.
 * @param message Optional failure message.
 * @throws AssertionFailedError if the vectors are not approximately equal.
 */
fun vectorAssertEquals(
	expected: Vector3,
	actual: Vector3,
	tolerance: Float = ZERO_TOLERANCE,
	message: String? = null,
) {
	if (!vectorApproxEqual(expected, actual, tolerance)) {
		AssertionFailureBuilder.assertionFailure().message(message)
			.expected(expected).actual(actual).buildAndThrow()
	}
}

/**
 * Asserts that two vectors are not approximately equal.
 *
 * @param expected The vector that the actual value should not be equal to.
 * @param actual The actual vector.
 * @param tolerance The allowed difference for each component.
 * @param message Optional failure message.
 * @throws AssertionFailedError if the vectors are approximately equal.
 */
fun vectorAssertNotEquals(
	expected: Vector3,
	actual: Vector3,
	tolerance: Float = ZERO_TOLERANCE,
	message: String? = null,
) {
	if (vectorApproxEqual(expected, actual, tolerance)) {
		AssertionFailureBuilder.assertionFailure().message(message)
			.expected(expected).actual(actual).buildAndThrow()
	}
}
