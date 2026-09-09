package dev.slimevr.skeleton

import dev.slimevr.util.inFloatingSeconds
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.time.ComparableTimeMark

// TODO Where are these numbers from?
val BODY_PART_MASSES = mapOf(
	BodyPart.HEAD to 0.0827f,
	BodyPart.LEFT_UPPER_ARM to 0.0263f,
	BodyPart.RIGHT_UPPER_ARM to 0.0263f,
	BodyPart.LEFT_LOWER_ARM to 0.0224f,
	BodyPart.RIGHT_LOWER_ARM to 0.0224f,
	BodyPart.CHEST to 0.1870f,
	BodyPart.WAIST to 0.1320f,
	BodyPart.HIP to 0.1530f,
	BodyPart.LEFT_UPPER_LEG to 0.1122f,
	BodyPart.RIGHT_UPPER_LEG to 0.1122f,
	BodyPart.LEFT_LOWER_LEG to 0.0620f,
	BodyPart.RIGHT_LOWER_LEG to 0.0620f,
)

data class COMState(
	val time: ComparableTimeMark,
	val position: Vector3,
	val velocity: Vector3,
	val acceleration: Vector3,
)

fun centreOfMass(
	bones: ComputedSkeleton,
): Vector3 = BODY_PART_MASSES.entries.fold(Vector3.ZERO) { acc: Vector3, massEntry ->
	val bone = bones[massEntry.key] ?: return@fold acc
	val boneCentre = (bone.headPosition + bone.tailPosition) / 2f
	return@fold acc + (boneCentre * massEntry.value)
}

fun computeComState(time: ComparableTimeMark, last: COMState?, com: Vector3): COMState = if (last != null) {
	val deltaTime = (time - last.time).inFloatingSeconds
	val comVelocity = (com - last.position) / deltaTime
	val comAcceleration = (comVelocity - last.velocity) / deltaTime
	COMState(
		time,
		com,
		comVelocity,
		comAcceleration,
	)
} else {
	COMState(
		time,
		com,
		Vector3.ZERO,
		Vector3.ZERO,
	)
}
