package dev.slimevr.skeleton

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.withSign

fun applyLocalOperation(
	parentRotation: Quaternion,
	childRotation: Quaternion,
	operation: (localRotation: Quaternion) -> Quaternion,
): Quaternion = parentRotation * operation(parentRotation.inv() * childRotation)

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
	fun apply(
		parentRotation: Quaternion,
		childRotation: Quaternion,
	): Quaternion = applyLocalOperation(parentRotation, childRotation, ::apply)
}

class CompleteConstraint : Constraint {
	override fun apply(localRotation: Quaternion): Quaternion = localRotation
}

data class TwistSwingConstraint(
	/**
	 * Twist range in radians.
	 */
	val twist: Float = 0.0f,
	/**
	 * Swing range in radians.
	 */
	val swing: Float = 0.0f,
	val allowedDeviation: Float = 0f,
	val maxDeviationFromTracker: Float = 15f,
) : Constraint {
	override fun apply(localRotation: Quaternion): Quaternion {
		var (swingQ, twistQ) = decomposeToSwingTwist(localRotation, Vector3.NEG_Y)
		swingQ = constrain(swingQ, swing)
		twistQ = constrain(twistQ, twist)
		return swingQ * twistQ
	}
}

data class HingeConstraint(
	/**
	 * Minimum rotation in radians.
	 */
	val min: Float = 0.0f,
	/**
	 * Maximum rotation in radians.
	 */
	val max: Float = 0.0f,
	val maxDeviationFromTracker: Float = 15f,
	val hingeAxis: Vector3 = Vector3.NEG_X,
) : Constraint {
	override fun apply(localRotation: Quaternion): Quaternion {
		val (_, hingeAxisRot) = decomposeToSwingTwist(localRotation, hingeAxis)
		return constrainOnAxis(hingeAxisRot, min, max, hingeAxis)
	}
}

data class LooseHingeConstraint(
	/**
	 * Minimum rotation in radians.
	 */
	val min: Float = 0.0f,
	/**
	 * Maximum rotation in radians.
	 */
	val max: Float = 0.0f,
	val allowedDeviation: Float = 0f,
	val maxDeviationFromTracker: Float = 15f,
	val hingeAxis: Vector3 = Vector3.NEG_X,
) : Constraint {
	override fun apply(localRotation: Quaternion): Quaternion {
		val (nonHingeRot, hingeAxisRot) = decomposeToSwingTwist(localRotation, hingeAxis)
		return constrain(nonHingeRot, allowedDeviation) *
			constrainOnAxis(hingeAxisRot, min, max, hingeAxis)
	}
}

fun decomposeToSwingTwist(
	rotation: Quaternion,
	twistAxis: Vector3,
): Pair<Quaternion, Quaternion> {
	val projection = rotation.project(twistAxis).unit()
	val twist = Quaternion(
		sqrt(1.0f - projection.xyz.lenSq()).withSign(rotation.w),
		projection.xyz,
	)
	val swing = (rotation * twist.inv())
	return Pair(swing, twist)
}

fun constrain(rotation: Quaternion, angle: Float): Quaternion {
	// Use angle to get the maximum magnitude the vector part of rotation can be
	// before it has violated a constraint.
	// Multiplying by 0.5 uniquely maps angles 0-180 degrees to 0-1 which works
	// nicely with unit quaternions.
	val magnitude = sin(angle * 0.5f)
	val magnitudeSqr = magnitude * magnitude
	val vector = rotation.xyz

	return if (vector.lenSq() > magnitudeSqr) {
		Quaternion(
			sqrt(1f - magnitudeSqr).withSign(rotation.w),
			vector.unit() * magnitude,
		)
	} else {
		rotation.unit()
	}
}

fun constrainOnAxis(
	rotation: Quaternion,
	minAngle: Float,
	maxAngle: Float,
	axis: Vector3,
): Quaternion {
	val magnitudeMin = sin(minAngle * 0.5f)
	val magnitudeMax = sin(maxAngle * 0.5f)
	val magnitudeSqrMin = (magnitudeMin * magnitudeMin).withSign(minAngle)
	val magnitudeSqrMax = (magnitudeMax * magnitudeMax).withSign(maxAngle)
	val vector = rotation.xyz

	val rotMagnitudeSqr = vector.lenSq().withSign(vector.dot(axis) * rotation.w)
	return if (rotMagnitudeSqr !in magnitudeSqrMin..magnitudeSqrMax) {
		val distToMin = min(
			abs(rotMagnitudeSqr - magnitudeSqrMin),
			abs(rotMagnitudeSqr + magnitudeSqrMin),
		)
		val distToMax = min(
			abs(rotMagnitudeSqr - magnitudeSqrMax),
			abs(rotMagnitudeSqr + magnitudeSqrMax),
		)

		val magnitude = if (distToMin < distToMax) magnitudeMin else magnitudeMax
		val magnitudeSqr =
			abs(if (distToMin < distToMax) magnitudeSqrMin else magnitudeSqrMax)

		Quaternion(
			sqrt(1.0f - magnitudeSqr),
			vector.unit() * -magnitude,
		).unit()
	} else {
		rotation.unit()
	}
}
