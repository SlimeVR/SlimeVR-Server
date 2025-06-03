package dev.slimevr.unit

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.math.sign
import kotlin.test.assertEquals

class TwinExtendedBackTests {

	// Bottom back quarter
	val negRange = -269..-180

	// Front two quarters and top back quarter
	val posRange = -179..90

	val pitchRange = negRange.map {
		AngleTest(it.toFloat(), -1f)
	}.plus(
		posRange.map {
			AngleTest(it.toFloat(), 1f)
		},
	)

	@get:TestFactory
	val pitchTests: List<DynamicTest>
		get() = pitchRange
			.map { a: AngleTest ->
				DynamicTest.dynamicTest(
					"Dot product test for ${if (a.sign > 0f) "positive" else "negative"} signs <$a>",
				) {
					testSign(
						Quaternion.IDENTITY,
						EulerAngles(
							EulerOrder.YZX,
							a.pitch * FastMath.DEG_TO_RAD,
							0f,
							0f,
						).toQuaternion(),
						a.sign,
					)
				}
			}

	fun testSign(ref: Quaternion, extended: Quaternion, expectedSign: Float) {
		val result = extended.twinExtendedBack(ref)
		val dot = ref.dot(result)
		assertEquals(expectedSign.sign, dot.sign, "Resulting dot ($dot) does not match the expected sign ($expectedSign)")
	}

	data class AngleTest(val pitch: Float, val sign: Float)
}
