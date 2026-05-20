package dev.slimevr.tracker

import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

data class SessionCalibration(
	val headingCorrection: Quaternion,
	val attitudeAlignment: Quaternion,
	val headingAlignment: Quaternion,
)

fun applyCalibration(
	rawRotation: Quaternion,
	headingCorrect: Quaternion = Quaternion.IDENTITY,
	attitudeAlign: Quaternion = Quaternion.IDENTITY,
	headingAlign: Quaternion = Quaternion.IDENTITY,
): Quaternion =
	headingCorrect * headingAlign.inv() * rawRotation * attitudeAlign * headingAlign

// We reverse the order of headingAlign and attitudeAlign here since our
//  attitude alignment is within the raw heading frame of reference, so we must
//  bring the orientation back into that frame of reference first. Whatever is
//  applied last must be taken off first.
fun undoCalibration(
	calibratedRotation: Quaternion,
	headingCorrect: Quaternion = Quaternion.IDENTITY,
	attitudeAlign: Quaternion = Quaternion.IDENTITY,
	headingAlign: Quaternion = Quaternion.IDENTITY,
): Quaternion =
	headingAlign * headingCorrect.inv() * calibratedRotation * headingAlign.inv() * attitudeAlign.inv()

// Acceleration needs to be rotated by raw rotation with heading corrected
private fun accelerationRotation(
	rawRotation: Quaternion,
	headingCorrect: Quaternion = Quaternion.IDENTITY,
	headingAlign: Quaternion = Quaternion.IDENTITY,
): Quaternion = headingAlign.inv() * headingCorrect * rawRotation

fun applyCalibration(
	rawAcceleration: Vector3,
	rawRotation: Quaternion,
	headingCorrect: Quaternion = Quaternion.IDENTITY,
	headingAlign: Quaternion = Quaternion.IDENTITY,
): Vector3 = accelerationRotation(rawRotation, headingCorrect, headingAlign).sandwich(
	rawAcceleration
)

fun undoCalibration(
	calibratedAcceleration: Vector3,
	rawRotation: Quaternion,
	headingCorrect: Quaternion = Quaternion.IDENTITY,
	headingAlign: Quaternion = Quaternion.IDENTITY,
): Vector3 = accelerationRotation(rawRotation, headingCorrect, headingAlign).inv()
	.sandwich(calibratedAcceleration)

private fun eulerHeading(q: Quaternion): Quaternion =
	Quaternion.rotationAroundYAxis(q.toEulerAngles(EulerOrder.YZX).y)

fun estimateHeadingCorrect(
	rawRotation: Quaternion, referenceRotation: Quaternion
): Quaternion = eulerHeading(eulerHeading(referenceRotation).inv() * rawRotation)

fun estimateAttitudeAlign(
	rawRotation: Quaternion, headingCorrect: Quaternion
): Quaternion = headingCorrect.inv() * rawRotation
