package dev.slimevr.tracker

import com.jme3.math.FastMath
import com.jme3.math.FastMath.ZERO_TOLERANCE
import com.jme3.math.FastMath.isApproxZero
import dev.slimevr.angularAssertEquals
import dev.slimevr.degreeToRadian
import dev.slimevr.quaternionApproxEqual
import dev.slimevr.quaternionAssertEquals
import dev.slimevr.quaternionAssertNotEquals
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SessionCalibrationTest {
	@TestFactory
	fun makeRawOrientationTests(): List<DynamicTest> = heading.flatMap { hC ->
		attitude.flatMap { aA ->
			heading.map { hA ->
				DynamicTest.dynamicTest(
					"( hC: $hC, aA: $aA, hA: $hA )",
				) {
					// We can just use identity for the target orientation as only the
					//  calibration quaternions themselves matter.
					testMakeRawOrientation(Quaternion.IDENTITY, hC, aA, hA)
				}
			}
		}
	}

	/**
	 * We're trying to prove stuff here using this, so let's at least prove that we can
	 * make a raw orientation and then bring it back to the bone frame of reference.
	 */
	fun testMakeRawOrientation(
		boneOrientation: Quaternion,
		headingCorrect: Quaternion,
		attitudeAlign: Quaternion,
		headingAlign: Quaternion,
	) {
		val rawOrientation = undoCalibration(
			boneOrientation,
			headingCorrect,
			attitudeAlign,
			headingAlign,
		)
		val newBoneOrientation = applyCalibration(
			rawOrientation,
			headingCorrect,
			attitudeAlign,
			headingAlign,
		)
		// Now that we re-applied the calibrations, let's see if it matches!
		quaternionAssertEquals(boneOrientation, newBoneOrientation)
	}

	@TestFactory
	fun headingCorrectTimingTests(): List<DynamicTest> = heading.flatMap { hC ->
		attitude.flatMap { aA ->
			heading.map { hA ->
				DynamicTest.dynamicTest(
					"( hC: $hC, aA: $aA, hA: $hA )",
				) {
					// We can just use identity for the target orientation as only the
					//  calibration quaternions themselves matter.
					testHeadingCorrectTiming(Quaternion.IDENTITY, hC, aA, hA)
				}
			}
		}
	}

	/**
	 * It doesn't actually matter *when* you add heading correction, just as long as
	 * it's on the left side.
	 */
	fun testHeadingCorrectTiming(
		rawOrientation: Quaternion,
		headingCorrect: Quaternion,
		attitudeAlign: Quaternion,
		headingAlign: Quaternion,
	) {
		val boneOrientationA =
			headingCorrect * rawOrientation * attitudeAlign * headingAlign
		val boneOrientationB =
			headingCorrect * (rawOrientation * attitudeAlign * headingAlign)
		val boneOrientationC =
			headingCorrect * (rawOrientation * attitudeAlign) * headingAlign
		quaternionAssertEquals(boneOrientationA, boneOrientationB)
		quaternionAssertEquals(boneOrientationA, boneOrientationC)
	}

	@TestFactory
	fun headingCorrectAttitudeAlignTests(): List<DynamicTest> = heading.flatMap { hC ->
		attitude.map { aA ->
			DynamicTest.dynamicTest(
				"( hC: $hC, aA: $aA )",
			) {
				// We can just use identity for the target orientation as only the
				//  calibration quaternions themselves matter.
				testHeadingCorrectAttitudeAlign(Quaternion.IDENTITY, hC, aA)
			}
		}
	}

	/**
	 * Heading correction also does not affect the calculation of the attitude alignment
	 * using Euler angles.
	 */
	fun testHeadingCorrectAttitudeAlign(
		rawOrientation: Quaternion,
		headingCorrect: Quaternion,
		attitudeAlign: Quaternion,
	) {
		val boneOrientationA =
			(rawOrientation * attitudeAlign).toEulerAngles(EulerOrder.YZX)
		val boneOrientationB =
			(headingCorrect * rawOrientation * attitudeAlign).toEulerAngles(EulerOrder.YZX)
		angularAssertEquals(boneOrientationA.x, boneOrientationB.x, ZERO_TOLERANCE)
		angularAssertEquals(boneOrientationA.z, boneOrientationB.z, ZERO_TOLERANCE)
		// We can also show that we're calculating the right attitude alignment.
		val attitudeAlignEul = attitudeAlign.toEulerAngles(EulerOrder.YZX)
		angularAssertEquals(attitudeAlignEul.x, boneOrientationA.x, ZERO_TOLERANCE)
		angularAssertEquals(attitudeAlignEul.z, boneOrientationA.z, ZERO_TOLERANCE)
	}

	@TestFactory
	fun attitudeHeadingAlignDependenceTests(): List<DynamicTest> {
		// Order doesn't matter if the attitude alignment has no attitude.
		return attitude.filterNot { isApproxZero(it.x) && isApproxZero(it.z) }
			.flatMap { aA ->
				// Same for if heading alignment is the quaternion identity.
				heading.filterNot {
					quaternionApproxEqual(
						it,
						Quaternion.IDENTITY,
					)
				}.map { hA ->
					DynamicTest.dynamicTest(
						"( aA: $aA, hA: $hA )",
					) {
						// We can just use identity for the target orientation as only the
						//  calibration quaternions themselves matter.
						testAttitudeHeadingAlignDependence(Quaternion.IDENTITY, aA, hA)
					}
				}
			}
	}

	/**
	 * It *does* matter what order you apply attitude and heading alignment.
	 */
	fun testAttitudeHeadingAlignDependence(
		rawOrientation: Quaternion,
		attitudeAlign: Quaternion,
		headingAlign: Quaternion,
	) {
		val boneOrientationA = rawOrientation * attitudeAlign * headingAlign
		val boneOrientationB = rawOrientation * headingAlign * attitudeAlign
		quaternionAssertNotEquals(boneOrientationA, boneOrientationB)
	}

	@TestFactory
	fun attitudeHeadingAlignOrderTests(): List<DynamicTest> {
		// We're not proving anything if both attitude axes are of equal magnitude.
		return attitude.filterNot { FastMath.isApproxEqual(abs(it.x), abs(it.z)) }
			.flatMap { aA ->
				heading.map { hA ->
					DynamicTest.dynamicTest(
						"( aA: $aA, hA: $hA )",
					) {
						// We can just use identity for the target orientation as only the
						//  calibration quaternions themselves matter.
						testAttitudeHeadingAlignOrder(Quaternion.IDENTITY, aA, hA)
					}
				}
			}
	}

	/**
	 * If we want to modify heading alignment but keep a constant attitude alignment,
	 * we need to apply heading alignment *after* attitude.
	 */
	fun testAttitudeHeadingAlignOrder(
		rawOrientation: Quaternion,
		attitudeAlign: Quaternion,
		headingAlign: Quaternion,
	) {
		// Perpendicular heading alignment (rotated by 90 deg), makes it easy to check
		//  our results.
		val headingAlignB =
			headingAlign * Quaternion.rotationAroundYAxis(FastMath.HALF_PI)

		// We must also apply an inverse of the heading alignment to make our
		//  quaternions comparable; we just want to affect the axes, not add to the
		//  orientation.
		val boneOrientationA = applyCalibration(
			rawOrientation,
			Quaternion.IDENTITY,
			attitudeAlign,
			headingAlign,
		)
		val boneOrientationB = applyCalibration(
			rawOrientation,
			Quaternion.IDENTITY,
			attitudeAlign,
			headingAlignB,
		)
		assertEquals(abs(boneOrientationA.x), abs(boneOrientationB.z), ZERO_TOLERANCE)
		assertEquals(abs(boneOrientationA.z), abs(boneOrientationB.x), ZERO_TOLERANCE)

		// Since it's required for this test, we can also show that by applying the
		//  inverse of heading alignment as a heading correction, we can retain the same
		//  heading orientation despite changing alignment. By doing this, we remove
		//  dependence between correction and alignment; they can resolve to definitive
		//  values.
		assertEquals(boneOrientationA.y, boneOrientationB.y, ZERO_TOLERANCE)

		// We can also show that this does not work when heading alignment comes before
		//  attitude alignment.
		val boneOrientationC =
			headingAlign.inv() * (rawOrientation * headingAlign * attitudeAlign)
		val boneOrientationD =
			headingAlignB.inv() * (rawOrientation * headingAlignB * attitudeAlign)
		assertNotEquals(
			abs(boneOrientationC.x),
			abs(boneOrientationD.z),
			ZERO_TOLERANCE,
		)
		assertNotEquals(
			abs(boneOrientationC.z),
			abs(boneOrientationD.x),
			ZERO_TOLERANCE,
		)
	}

	@TestFactory
	fun estimateSessionCalibrationTests(): List<DynamicTest> {
		// We can only estimate session calibration with yaw and pitch, roll cannot be
		//  compensated for
		return heading.flatMap { hC ->
			pitch.map { aA ->
				DynamicTest.dynamicTest(
					"( hC: $hC, aA: $aA )",
				) {
					// We can just use identity for the target orientation as only the
					//  calibration quaternions themselves matter.
					testEstimateSessionCalibration(Quaternion.IDENTITY, hC, aA)
				}
			}
		}
	}

	fun testEstimateSessionCalibration(
		calibratedRotation: CalibratedRotation,
		headingCorrect: HeadingCorrection,
		attitudeAlign: AttitudeAlignment,
	) {
		val rawRotation =
			undoCalibration(calibratedRotation, headingCorrect, attitudeAlign)

		// TODO: Can we avoid needing to use twinNearest? It would be best if it was
		//  just inherently in the right quaternion space (for both heading & attitude).
		//  This might also just not be a problem, I'm not sure.
		val estimatedHeadingCorrect =
			estimateHeadingCorrect(rawRotation, Quaternion.IDENTITY)
		quaternionAssertEquals(
			headingCorrect,
			estimatedHeadingCorrect.twinNearest(headingCorrect),
			message = "Estimated heading correction is wrong"
		)

		val estimatedAttitudeAlign =
			estimateAttitudeAlign(rawRotation, estimatedHeadingCorrect)
		quaternionAssertEquals(
			attitudeAlign,
			estimatedAttitudeAlign.twinNearest(attitudeAlign),
			message = "Estimated attitude alignment is wrong"
		)
	}

	companion object {
		// 5 steps
		val step = (-180..180 step 72).map { it.toFloat() }

		// 12 steps
		val fineStep = (-180..180 step 30).map { it.toFloat() }

		// Will not work when we don't know the heading correction or heading alignment,
		//  we will need Euler angles to calculate those, and it needs to sacrifice one
		//  axis for Euler angles to work
		val attitude = step.flatMap { x ->
			step.map { z ->
				EulerAngles(
					EulerOrder.YZX,
					degreeToRadian(x),
					0f,
					degreeToRadian(z),
				).toQuaternion()
			}
		}

		val heading = step.map { y ->
			Quaternion.rotationAroundYAxis(degreeToRadian(y))
		}

		val pitch = step.map { x ->
			Quaternion.rotationAroundXAxis(degreeToRadian(x))
		}
	}
}
