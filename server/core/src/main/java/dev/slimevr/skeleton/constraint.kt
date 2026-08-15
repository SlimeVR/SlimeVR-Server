package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

// TODO Actually rewrite, currently still using original server code
interface Constraint {
	/**
	 * Applies the constraint to a local rotation.
	 * @return The constrained local rotation.
	 */
	fun apply(localRotation: Quaternion): Quaternion

	/**
	 * Applies the constraint to a global rotation using its parent rotation.
	 * @return The constrained global rotation.
	 */
	fun apply(parentRotation: Quaternion, childRotation: Quaternion): Quaternion = parentRotation * apply(parentRotation.inv() * childRotation)
}

class CompleteConstraint : Constraint {
	override fun apply(localRotation: Quaternion): Quaternion = localRotation
}

data class TwistSwingConstraint(
	val twist: Float = 0.0f,
	val swing: Float = 0.0f,
	val allowedDeviation: Float = 0f,
	val maxDeviationFromTracker: Float = 15f,
) : Constraint {
	override fun apply(localRotation: Quaternion): Quaternion {
		var (swingQ, twistQ) = decompose(localRotation, Vector3.NEG_Y)
		swingQ = constrain(swingQ, swing)
		twistQ = constrain(twistQ, twist)

		return swingQ * twistQ
	}
}

data class HingeConstraint(
	val min: Float = 0.0f,
	val max: Float = 0.0f,
	val allowedDeviation: Float = 0f,
	val maxDeviationFromTracker: Float = 15f,
) : Constraint {
	override fun apply(localRotation: Quaternion): Quaternion {
		val (_, hingeAxisRot) = decompose(localRotation, Vector3.NEG_X)
		return constrain(hingeAxisRot, min, max, Vector3.NEG_X)
	}
}

data class LooseHingeConstraint(
	val min: Float = 0.0f,
	val max: Float = 0.0f,
	val allowedDeviation: Float = 0f,
	val maxDeviationFromTracker: Float = 15f,
) : Constraint {
	override fun apply(localRotation: Quaternion): Quaternion {
		var (nonHingeRot, hingeAxisRot) = decompose(localRotation, Vector3.NEG_X)
		hingeAxisRot = constrain(hingeAxisRot, min, max, Vector3.NEG_X)
		nonHingeRot = constrain(nonHingeRot, allowedDeviation)
		return nonHingeRot * hingeAxisRot
	}
}

private fun decompose(
	rotation: Quaternion,
	twistAxis: Vector3,
): Pair<Quaternion, Quaternion> {
	val projection = rotation.project(twistAxis).unit()
	val twist = Quaternion(sqrt(1.0f - projection.xyz.lenSq()) * if (rotation.w >= 0f) 1f else -1f, projection.xyz).unit()
	val swing = (rotation * twist.inv()).unit()
	return Pair(swing, twist)
}

private fun constrain(rotation: Quaternion, angle: Float): Quaternion {
	// Use angle to get the maximum magnitude the vector part of rotation can be
	// before it has violated a constraint.
	// Multiplying by 0.5 uniquely maps angles 0-180 degrees to 0-1 which works
	// nicely with unit quaternions.
	val magnitude = sin(angle * 0.5f)
	val magnitudeSqr = magnitude * magnitude
	val sign = if (rotation.w >= 0f) 1f else -1f
	var vector = rotation.xyz
	var rot = rotation

	if (vector.lenSq() > magnitudeSqr) {
		vector = vector.unit() * magnitude
		rot = Quaternion(sqrt(1f - magnitudeSqr) * sign, vector)
	}

	return rot.unit()
}

private fun constrain(rotation: Quaternion, minAngle: Float, maxAngle: Float, axis: Vector3): Quaternion {
	val magnitudeMin = sin(minAngle * 0.5f)
	val magnitudeMax = sin(maxAngle * 0.5f)
	val magnitudeSqrMin = magnitudeMin * magnitudeMin * if (minAngle >= 0f) 1f else -1f
	val magnitudeSqrMax = magnitudeMax * magnitudeMax * if (maxAngle >= 0f) 1f else -1f
	var vector = rotation.xyz
	var rot = rotation

	val rotMagnitude = vector.lenSq() * if (vector.dot(axis) * sign(rot.w) < 0) -1f else 1f
	if (rotMagnitude !in magnitudeSqrMin..magnitudeSqrMax) {
		val distToMin = min(abs(rotMagnitude - magnitudeSqrMin), abs(rotMagnitude + magnitudeSqrMin))
		val distToMax = min(abs(rotMagnitude - magnitudeSqrMax), abs(rotMagnitude + magnitudeSqrMax))

		val magnitude = if (distToMin < distToMax) magnitudeMin else magnitudeMax
		val magnitudeSqr = abs(if (distToMin < distToMax) magnitudeSqrMin else magnitudeSqrMax)
		vector = vector.unit() * -magnitude

		rot = Quaternion(sqrt(1.0f - magnitudeSqr), vector)
	}

	return rot.unit()
}
