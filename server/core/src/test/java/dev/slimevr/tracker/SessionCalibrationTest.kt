package dev.slimevr.tracker

import com.jme3.math.FastMath
import com.jme3.math.FastMath.ZERO_TOLERANCE
import com.jme3.math.FastMath.isApproxZero
import dev.slimevr.angularAssertEquals
import dev.slimevr.degreeToRadian
import dev.slimevr.quaternionApproxEqual
import dev.slimevr.quaternionAssertEquals
import dev.slimevr.quaternionAssertNotEquals
import dev.slimevr.vectorAssertEquals
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SessionCalibrationTest {
	@Test
	fun makeRawOrientationTests() {
		// We can just use identity for the target orientation as only the
		//  calibration quaternions themselves matter.
		val boneOrientation = Quaternion.IDENTITY

		heading.forEach { hC ->
			attitude.forEach { aA ->
				heading.forEach { hA ->
					val rawOrientation = undoCalibration(
						boneOrientation,
						hC,
						aA,
						hA,
					)
					val newBoneOrientation = applyCalibration(
						rawOrientation,
						hC,
						aA,
						hA,
					)
					// Now that we re-applied the calibrations, let's see if it matches!
					quaternionAssertEquals(boneOrientation, newBoneOrientation, message = "( hC: $hC, aA: $aA, hA: $hA )")
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

	/**
	 * It doesn't actually matter *when* you add heading correction, just as long as
	 * it's on the left side.
	 */
	@Test
	fun headingCorrectTimingTests() {
		// We can just use identity for the target orientation as only the
		//  calibration quaternions themselves matter.
		val rawOrientation = Quaternion.IDENTITY

		heading.forEach { hC ->
			attitude.forEach { aA ->
				heading.forEach { hA ->
					val boneOrientationA =
						hC * rawOrientation * aA * hA
					val boneOrientationB =
						hC * (rawOrientation * aA * hA)
					val boneOrientationC =
						hC * (rawOrientation * aA) * hA
					quaternionAssertEquals(boneOrientationA, boneOrientationB, message = "( hC: $hC, aA: $aA, hA: $hA )")
					quaternionAssertEquals(boneOrientationA, boneOrientationC, message = "( hC: $hC, aA: $aA, hA: $hA )")
				}
			}
		}
	}

	/**
	 * Heading correction also does not affect the calculation of the attitude alignment
	 * using Euler angles.
	 */
	@Test
	fun headingCorrectAttitudeAlignTests() {
		// We can just use identity for the target orientation as only the
		//  calibration quaternions themselves matter.
		val rawOrientation = Quaternion.IDENTITY

		heading.forEach { hC ->
			attitude.forEach { aA ->
				val boneOrientationA =
					(rawOrientation * aA).toEulerAngles(EulerOrder.YZX)
				val boneOrientationB =
					(hC * rawOrientation * aA).toEulerAngles(EulerOrder.YZX)
				angularAssertEquals(boneOrientationA.x, boneOrientationB.x, ZERO_TOLERANCE, message = "( hC: $hC, aA: $aA )")
				angularAssertEquals(boneOrientationA.z, boneOrientationB.z, ZERO_TOLERANCE, message = "( hC: $hC, aA: $aA )")
				// We can also show that we're calculating the right attitude alignment.
				val attitudeAlignEul = aA.toEulerAngles(EulerOrder.YZX)
				angularAssertEquals(attitudeAlignEul.x, boneOrientationA.x, ZERO_TOLERANCE, message = "( hC: $hC, aA: $aA )")
				angularAssertEquals(attitudeAlignEul.z, boneOrientationA.z, ZERO_TOLERANCE, message = "( hC: $hC, aA: $aA )")
			}
		}
	}

	/**
	 * It *does* matter what order you apply attitude and heading alignment.
	 */
	@Test
	fun attitudeHeadingAlignDependenceTests() {
		// We can just use identity for the target orientation as only the
		//  calibration quaternions themselves matter.
		val rawOrientation = Quaternion.IDENTITY

		// Order doesn't matter if the attitude alignment has no attitude.
		attitude.filterNot { isApproxZero(it.x) && isApproxZero(it.z) }
			.forEach { aA ->
				// Same for if heading alignment is the quaternion identity.
				heading.filterNot {
					quaternionApproxEqual(
						it,
						Quaternion.IDENTITY,
					)
				}.forEach { hA ->
					val boneOrientationA = rawOrientation * aA * hA
					val boneOrientationB = rawOrientation * hA * aA
					quaternionAssertNotEquals(boneOrientationA, boneOrientationB, message = "aA $aA, hA: $hA ")
				}
			}
	}

	/**
	 * If we want to modify heading alignment but keep a constant attitude alignment,
	 * we need to apply heading alignment *after* attitude.
	 */
	@Test
	fun attitudeHeadingAlignOrderTests() {
		// We can just use identity for the target orientation as only the
		//  calibration quaternions themselves matter.
		val rawOrientation = Quaternion.IDENTITY

		// We're not proving anything if both attitude axes are of equal magnitude.
		attitude.filterNot { FastMath.isApproxEqual(abs(it.x), abs(it.z)) }
			.forEach { aA ->
				heading.forEach { hA ->
					// Perpendicular heading alignment (rotated by 90 deg), makes it easy to check
					//  our results.
					val headingAlignB =
						hA * Quaternion.rotationAroundYAxis(FastMath.HALF_PI)

					// We must also apply an inverse of the heading alignment to make our
					//  quaternions comparable; we just want to affect the axes, not add to the
					//  orientation.
					val boneOrientationA = applyCalibration(
						rawOrientation,
						Quaternion.IDENTITY,
						aA,
						hA,
					)
					val boneOrientationB = applyCalibration(
						rawOrientation,
						Quaternion.IDENTITY,
						aA,
						headingAlignB,
					)
					assertEquals(abs(boneOrientationA.x), abs(boneOrientationB.z), ZERO_TOLERANCE, "( aA: $aA, hA: $hA )")
					assertEquals(abs(boneOrientationA.z), abs(boneOrientationB.x), ZERO_TOLERANCE, "( aA: $aA, hA: $hA )")

					// Since it's required for this test, we can also show that by applying the
					//  inverse of heading alignment as a heading correction, we can retain the same
					//  heading orientation despite changing alignment. By doing this, we remove
					//  dependence between correction and alignment; they can resolve to definitive
					//  values.
					assertEquals(boneOrientationA.y, boneOrientationB.y, ZERO_TOLERANCE)

					// We can also show that this does not work when heading alignment comes before
					//  attitude alignment.
					val boneOrientationC =
						hA.inv() * (rawOrientation * hA * aA)
					val boneOrientationD =
						headingAlignB.inv() * (rawOrientation * headingAlignB * aA)
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
			}
	}

	@Test
	fun estimateSessionCalibrationTests() {
		// We can only estimate session calibration with yaw and pitch, roll cannot be compensated for
		heading.forEach { hC ->
			pitch.forEach { aA ->
				heading.forEach { rR ->
					val rawRotation =
						undoCalibration(rR * Quaternion.IDENTITY, rR, aA)

					val estimatedHeadingCorrect =
						estimateHeadingCorrect(rawRotation, rR)
					quaternionAssertEquals(
						rR,
						estimatedHeadingCorrect,
						message = "Estimated heading correction is wrong ( hC: $hC, aA: $aA, rR: $rR )",
					)

					val estimatedAttitudeAlign =
						estimateAttitudeAlign(rawRotation, estimatedHeadingCorrect, rR)
					quaternionAssertEquals(
						aA,
						estimatedAttitudeAlign,
						message = "Estimated attitude alignment is wrong ( hC: $hC, aA: $aA, rR: $rR )",
					)
				}
			}
		}
	}

	@Test
	fun estimateHeadingAlignTests() {
		val frontRot = Quaternion(0.707f, 0.707f, 0f, 0f)
		heading.forEach { hA ->
			heading.forEach { ref ->
				radians.forEach { yawOffset ->
					// To undo the yawOffset that is baked into headingAlign
					val yawOffsetRotation = Quaternion.rotationAroundYAxis(yawOffset)

					// Only undo heading
					val calibratedRotation = ref * frontRot
					val rawRotation = undoCalibration(
						calibratedRotation,
						headingAlign = hA * yawOffsetRotation.inv(),
					)

					val estimateHeadingAlign = estimateHeadingAlign(
						rawRotation,
						ref,
						yawOffset = yawOffset,
					)
					// twinNearest is equivalent since this doesn't care about polarity
					quaternionAssertEquals(
						hA,
						estimateHeadingAlign.twinNearest(hA),
						message = "Estimated heading alignment is wrong ( hA: $hA, ref: $ref, yawOffset: $yawOffset )",
					)
				}
			}
		}
	}

	/**
	 * Prove that we can transform acceleration to world space from tracker space, and
	 * back to tracker space from world space.
	 */
	@Test
	fun rawAccelerationTests() = heading.forEach { hC ->
		attitude.forEach { aA ->
			heading.forEach { hA ->
				val rawOrientation = undoCalibration(
					Quaternion.IDENTITY,
					hC,
					aA,
					hA,
				)
				val rawAcceleration = undoCalibration(
					Vector3.POS_X,
					rawOrientation,
					hC,
					hA,
				)
				val newAcceleration = applyCalibration(
					rawAcceleration,
					rawOrientation,
					hC,
					hA,
				)
				// Now that we re-applied the calibrations, let's see if it matches!
				vectorAssertEquals(Vector3.POS_X, newAcceleration, message = "( hC: $hC, aA: $aA, hA: $hA )")
			}
		}
	}

	companion object {
		// 5 steps
		val step = (-180..180 step 72).map { it.toFloat() }

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

		val radians = step.map { d ->
			degreeToRadian(d)
		}

		val pitch = step.map { x ->
			Quaternion.rotationAroundXAxis(degreeToRadian(x))
		}
	}
}
