package dev.slimevr.tracker

import io.github.axisangles.ktmath.Quaternion

fun applyCalibration(
	q: Quaternion,
	headingCorrect: Quaternion = Quaternion.IDENTITY,
	attitudeAlign: Quaternion = Quaternion.IDENTITY,
	headingAlign: Quaternion = Quaternion.IDENTITY,
): Quaternion = headingCorrect * headingAlign.inv() * q * attitudeAlign * headingAlign

// We reverse the order of headingAlign and attitudeAlign here since our
//  attitude alignment is within the raw heading frame of reference, so we must
//  bring the orientation back into that frame of reference first. Whatever is
//  applied last must be taken off first.
fun undoCalibration(
	q: Quaternion,
	headingCorrect: Quaternion = Quaternion.IDENTITY,
	attitudeAlign: Quaternion = Quaternion.IDENTITY,
	headingAlign: Quaternion = Quaternion.IDENTITY,
): Quaternion = headingAlign * headingCorrect.inv() * q * headingAlign.inv() * attitudeAlign.inv()
