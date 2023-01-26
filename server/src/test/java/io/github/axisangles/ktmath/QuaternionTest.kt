package io.github.axisangles.ktmath

import kotlin.math.*
import kotlin.test.Test
import kotlin.test.assertTrue

class QuaternionTest {

	@Test
	fun plus() {
		val q1 = Quaternion(1f, 2f, 3f, 4f)
		val q2 = Quaternion(5f, 6f, 7f, 8f)
		val q3 = Quaternion(6f, 8f, 10f, 12f)
		assertEquals(q3, q1 + q2)
	}

	@Test
	fun times() {
		val q1 = Quaternion(1f, 2f, 3f, 4f)
		val q2 = Quaternion(5f, 6f, 7f, 8f)
		val q3 = Quaternion(-60f, 12f, 30f, 24f)
		assertEquals(q3, q1 * q2)
	}

	@Test
	fun timesScalarRhs() {
		val q1 = Quaternion(1f, 2f, 3f, 4f)
		val q2 = Quaternion(2f, 4f, 6f, 8f)
		assertEquals(q2, q1 * 2f)
	}

	@Test
	fun timesScalarLhs() {
		val q1 = Quaternion(1f, 2f, 3f, 4f)
		val q2 = Quaternion(2f, 4f, 6f, 8f)
		assertEquals(q2, 2f * q1)
	}

	@Test
	fun inverse() {
		val q1 = Quaternion(1f, 2f, 3f, 4f)
		val q2 = Quaternion(1f / 30f, -2f / 30f, -3f / 30f, -4f / 30f)
		assertEquals(q2, q1.inv())
	}

	@Test
	fun rightDiv() {
		val q1 = Quaternion(1f, 2f, 3f, 4f)
		val q2 = Quaternion(5f, 6f, 7f, 8f)
		val q3 = Quaternion(-60f, 12f, 30f, 24f)
		assertEquals(q1, q3 / q2)
	}

	@Test
	fun rightDivFloatRhs() {
		val q1 = Quaternion(1f, 2f, 3f, 4f)
		val q2 = Quaternion(2f, 4f, 6f, 8f)
		assertEquals(q1, q2 / 2f)
	}

	@Test
	fun rightDivFloatLhs() {
		val q1 = Quaternion(1f, 2f, 3f, 4f)
		val q2 = Quaternion(1f / 15f, -2f / 15f, -1f / 5f, -4f / 15f)

		assertEquals(q2, 2f / q1)
	}

	@Test
	fun pow() {
		val q = Quaternion(1f, 2f, 3f, 4f)
		assertEquals(q.pow(1f), q, 1e-5)
		assertEquals(q.pow(2f), q * q, 1e-5)
		assertEquals(q.pow(0f), Quaternion.ONE, 1e-5)
		assertEquals(q.pow(-1f), q.inv(), 1e-5)
	}

	@Test
	fun interp() {
		val q1 = Quaternion(1f, 2f, 3f, 4f)
		val q2 = Quaternion(5f, 6f, 7f, 8f)
		val q3 = Quaternion(2.405691f, 3.5124686f, 4.619246f, 5.7260237f)
		assertEquals(q1.interp(q2, 0.5f), q3, 1e-7)
	}

	@Test
	fun interpR() {
		val q1 = Quaternion(1f, 2f, 3f, 4f)
		val q2 = -Quaternion(5f, 6f, 7f, 8f)
		val q3 = Quaternion(2.405691f, 3.5124686f, 4.619246f, 5.7260237f)
		assertEquals(q1.interpR(q2, 0.5f), q3, 1e-7)
	}

	@Test
	fun lerp() {
		val q1 = Quaternion(1f, 2f, 3f, 4f)
		val q2 = Quaternion(5f, 6f, 7f, 8f)
		val q3 = Quaternion(3f, 4f, 5f, 6f)
		assertEquals(q1.lerp(q2, 0.5f), q3, 1e-7)
	}

	companion object {
		private const val RELATIVE_TOLERANCE = 0.0

		internal fun assertEquals(
			expected: Quaternion,
			actual: Quaternion,
			tolerance: Double = RELATIVE_TOLERANCE
		) {
			val len = (actual - expected).lenSq()
			val squareSum = expected.lenSq() + actual.lenSq()
			assertTrue(
				len <= tolerance * tolerance * squareSum,
				"Expected: $expected but got: $actual"
			)
		}
	}
}

var randSeed = 0
fun randInt(): Int {
	randSeed = (1103515245 * randSeed + 12345).mod(2147483648).toInt()
	return randSeed
}

fun randFloat(): Float {
	return randInt().toFloat() / 2147483648
}

fun randGaussian(): Float {
	var thing = 1f - randFloat()
	while (thing == 0f) {
		// no 0s allowed
		thing = 1f - randFloat()
	}
	return sqrt(-2f * ln(thing)) * cos(PI.toFloat() * randFloat())
}

fun randMatrix(): Matrix3 {
	return Matrix3(
		randGaussian(), randGaussian(), randGaussian(),
		randGaussian(), randGaussian(), randGaussian(),
		randGaussian(), randGaussian(), randGaussian()
	)
}

fun randQuaternion(): Quaternion {
	return Quaternion(randGaussian(), randGaussian(), randGaussian(), randGaussian())
}

fun randRotMatrix(): Matrix3 {
	return randQuaternion().toMatrix()
}

fun randVector(): Vector3 {
	return Vector3(randGaussian(), randGaussian(), randGaussian())
}
