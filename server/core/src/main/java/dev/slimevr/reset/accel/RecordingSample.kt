package dev.slimevr.reset.accel

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.time.ComparableTimeMark

data class RecordingSample(
	val time: ComparableTimeMark,
	// Tracker
	val accel: Vector3,
	val rot: Quaternion,
	// HMD
	val hmdPos: Vector3,
)
