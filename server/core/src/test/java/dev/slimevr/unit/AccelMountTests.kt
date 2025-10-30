package dev.slimevr.unit

import com.jme3.math.FastMath
import dev.slimevr.unit.TrackerTestUtils.assertVectorApproxEqual
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.math.atan2

class AccelMountTests {
	@TestFactory
	fun testAccelAlignment(): List<DynamicTest> = testSet.map { t ->
		DynamicTest.dynamicTest(
			"Alignment of accel (Expected: ${t.expected}, reference: ${t.hmd})",
		) {
			checkAlignAccel(t.hmd, t.tracker, t.expected)
		}
	}

	fun angle(vector: Vector3): Quaternion {
		val yaw = atan2(vector.x, vector.z)
		return Quaternion.rotationAroundYAxis(yaw)
	}

	fun checkAlignAccel(hmd: Vector3, tracker: Vector3, expected: Vector3) {
		// All we really care about is the angle difference between hmdRot and trackerRot
		val hmdRot = angle(hmd.unit()).inv()
		val trackerRot = angle(tracker.unit())
		val result = (trackerRot * hmdRot).sandwichUnitZ()

		assertVectorApproxEqual(
			expected,
			result,
			"Resulting vector is not equal to reference vector ($expected vs $result)",
		)
	}

	@Test
	fun testSensorOffset() {
		val accel = Vector3.POS_X
		// 270 deg (default for officials)
		val sensorOffset = Quaternion.rotationAroundZAxis(-FastMath.HALF_PI)
		val actual = sensorOffset.sandwich(accel)

		assertVectorApproxEqual(Vector3.NEG_Y, actual)
	}

	data class AlignTest(val hmd: Vector3, val tracker: Vector3, val expected: Vector3)

	companion object {
		val testSet = arrayOf(
			// Front mount
			AlignTest(Vector3.POS_X, Vector3.POS_X, Vector3.POS_Z),
			AlignTest(Vector3.NEG_X, Vector3.NEG_X, Vector3.POS_Z),
			AlignTest(Vector3.POS_Z, Vector3.POS_Z, Vector3.POS_Z),
			AlignTest(Vector3.NEG_Z, Vector3.NEG_Z, Vector3.POS_Z),
			// Right mount
			AlignTest(Vector3.POS_X, Vector3.NEG_Z, Vector3.POS_X),
			AlignTest(Vector3.NEG_X, Vector3.POS_Z, Vector3.POS_X),
			AlignTest(Vector3.POS_Z, Vector3.POS_X, Vector3.POS_X),
			AlignTest(Vector3.NEG_Z, Vector3.NEG_X, Vector3.POS_X),
			// Back mount
			AlignTest(Vector3.POS_X, Vector3.NEG_X, Vector3.NEG_Z),
			AlignTest(Vector3.NEG_X, Vector3.POS_X, Vector3.NEG_Z),
			AlignTest(Vector3.POS_Z, Vector3.NEG_Z, Vector3.NEG_Z),
			AlignTest(Vector3.NEG_Z, Vector3.POS_Z, Vector3.NEG_Z),
			// Left mount
			AlignTest(Vector3.POS_X, Vector3.POS_Z, Vector3.NEG_X),
			AlignTest(Vector3.NEG_X, Vector3.NEG_Z, Vector3.NEG_X),
			AlignTest(Vector3.POS_Z, Vector3.NEG_X, Vector3.NEG_X),
			AlignTest(Vector3.NEG_Z, Vector3.POS_X, Vector3.NEG_X),
		)
	}
}
