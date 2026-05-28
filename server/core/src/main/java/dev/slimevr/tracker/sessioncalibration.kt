package dev.slimevr.tracker

import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

typealias RawRotation = Quaternion
typealias RawAcceleration = Vector3

typealias HeadingCorrection = Quaternion
typealias AttitudeAlignment = Quaternion
typealias HeadingAlignment = Quaternion

typealias AccelerationRotation = Quaternion

typealias CalibratedRotation = Quaternion
typealias CalibratedAcceleration = Vector3

data class SessionCalibration(
	val headingCorrection: HeadingCorrection = Quaternion.IDENTITY,
	val attitudeAlignment: AttitudeAlignment = Quaternion.IDENTITY,
	val headingAlignment: HeadingAlignment = Quaternion.IDENTITY,
)

fun applyCalibration(
	rawRotation: RawRotation,
	headingCorrect: HeadingCorrection = Quaternion.IDENTITY,
	attitudeAlign: AttitudeAlignment = Quaternion.IDENTITY,
	headingAlign: HeadingAlignment = Quaternion.IDENTITY,
): CalibratedRotation = headingAlign.inv() * headingCorrect * rawRotation * attitudeAlign * headingAlign

// We reverse the order of headingAlign and attitudeAlign here since our
//  attitude alignment is within the raw heading frame of reference, so we must
//  bring the orientation back into that frame of reference first. Whatever is
//  applied last must be taken off first.
fun undoCalibration(
	calibratedRotation: CalibratedRotation,
	headingCorrect: HeadingCorrection = Quaternion.IDENTITY,
	attitudeAlign: AttitudeAlignment = Quaternion.IDENTITY,
	headingAlign: HeadingAlignment = Quaternion.IDENTITY,
): RawRotation = headingCorrect.inv() * headingAlign * calibratedRotation * headingAlign.inv() * attitudeAlign.inv()

// Acceleration needs to be rotated by raw rotation with heading corrected
private fun accelerationRotation(
	rawRotation: RawRotation,
	headingCorrect: HeadingCorrection = Quaternion.IDENTITY,
	headingAlign: HeadingAlignment = Quaternion.IDENTITY,
): AccelerationRotation = headingAlign.inv() * headingCorrect * rawRotation

fun applyCalibration(
	rawAcceleration: RawAcceleration,
	rawRotation: RawRotation,
	headingCorrect: HeadingCorrection = Quaternion.IDENTITY,
	headingAlign: HeadingAlignment = Quaternion.IDENTITY,
): CalibratedAcceleration = accelerationRotation(rawRotation, headingCorrect, headingAlign).sandwich(
	rawAcceleration,
)

fun undoCalibration(
	calibratedAcceleration: CalibratedAcceleration,
	rawRotation: RawRotation,
	headingCorrect: HeadingCorrection = Quaternion.IDENTITY,
	headingAlign: HeadingAlignment = Quaternion.IDENTITY,
): RawAcceleration = accelerationRotation(rawRotation, headingCorrect, headingAlign).inv()
	.sandwich(calibratedAcceleration)

private fun eulerHeading(q: Quaternion): Quaternion = Quaternion.rotationAroundYAxis(q.toEulerAngles(EulerOrder.YZX).y).twinNearest(q)

fun estimateHeadingCorrect(
	rawRotation: RawRotation,
	referenceRotation: Quaternion,
): HeadingCorrection = eulerHeading(eulerHeading(referenceRotation).inv() * rawRotation).inv()
	.twinNearest(referenceRotation)

fun estimateAttitudeAlign(
	rawRotation: RawRotation,
	headingCorrect: HeadingCorrection,
): AttitudeAlignment = (headingCorrect * rawRotation).inv()
