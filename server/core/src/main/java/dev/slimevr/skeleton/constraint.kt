package dev.slimevr.skeleton

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.withSign

fun applyLocalOperation(
	parentRotation: Quaternion,
	childRotation: Quaternion,
	operation: (localRotation: Quaternion) -> Quaternion,
): Quaternion = operation(childRotation.localAbout(parentRotation)).globalAbout(parentRotation)

// TODO Create unit tests to validate behaviour
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

data class TwistSwingConstraint(
	/**
	 * Twist range in radians.
	 */
	val twist: Float = 0f,
	/**
	 * Swing range in radians.
	 */
	val swing: Float,
	val twistAxis: Vector3 = Vector3.POS_Y,
) : Constraint {
	override fun apply(localRotation: Quaternion): Quaternion {
		var (swingQ, twistQ) = decomposeToSwingTwist(localRotation, twistAxis)
		swingQ = constrainAngle(swingQ, swing)
		twistQ = constrainAngle(twistQ, twist)
		return swingQ * twistQ
	}
}

data class TwistConstraint(
	/**
	 * Twist range in radians.
	 */
	val twist: Float = 0f,
	val twistAxis: Vector3 = Vector3.POS_Y,
) : Constraint {
	override fun apply(localRotation: Quaternion): Quaternion {
		var (swingQ, twistQ) = decomposeToSwingTwist(localRotation, twistAxis)
		twistQ = constrainAngle(twistQ, twist)
		return swingQ * twistQ
	}
}

data class HingeConstraint(
	/**
	 * Minimum rotation in radians.
	 */
	val min: Float = 0f,
	/**
	 * Maximum rotation in radians.
	 */
	val max: Float,
	val hingeAxis: Vector3 = Vector3.POS_X,
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
	val min: Float = 0f,
	/**
	 * Maximum rotation in radians.
	 */
	val max: Float,
	/**
	 * Maximum non-hinge axis rotation in radians.
	 */
	val allowedDeviation: Float,
	val hingeAxis: Vector3 = Vector3.POS_X,
) : Constraint {
	override fun apply(localRotation: Quaternion): Quaternion {
		val (nonHingeRot, hingeAxisRot) = decomposeToSwingTwist(localRotation, hingeAxis)
		return constrainAngle(nonHingeRot, allowedDeviation) *
			constrainOnAxis(hingeAxisRot, min, max, hingeAxis)
	}
}

data class FreeHingeConstraint(
	/**
	 * Minimum rotation in radians.
	 */
	val min: Float = 0f,
	/**
	 * Maximum rotation in radians.
	 */
	val max: Float,
	val hingeAxis: Vector3 = Vector3.POS_X,
) : Constraint {
	override fun apply(localRotation: Quaternion): Quaternion {
		val (nonHingeRot, hingeAxisRot) = decomposeToSwingTwist(localRotation, hingeAxis)
		return nonHingeRot * constrainOnAxis(hingeAxisRot, min, max, hingeAxis)
	}
}

data class AxisConstraint(
	val axis: Vector3 = Vector3.POS_X,
) : Constraint {
	override fun apply(
		localRotation: Quaternion,
	): Quaternion = localRotation.project(axis).unit()
}

data class LooseAxisConstraint(
	/**
	 * Maximum non-axis rotation in radians.
	 */
	val allowedDeviation: Float,
	val axis: Vector3 = Vector3.POS_X,
) : Constraint {
	override fun apply(localRotation: Quaternion): Quaternion {
		val (nonAxisRot, axisRot) = decomposeToSwingTwist(localRotation, axis)
		return constrainAngle(nonAxisRot, allowedDeviation) * axisRot
	}
}

data class SwingTwist(
	val swing: Quaternion,
	val twist: Quaternion,
)

fun decomposeToSwingTwist(
	rotation: Quaternion,
	twistAxis: Vector3,
): SwingTwist {
	val twist = rotation.project(twistAxis).unit()
	val swing = rotation / twist
	return SwingTwist(swing, twist)
}

fun constrainAngle(rotation: Quaternion, angle: Float): Quaternion {
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
		rotation
	}
}

// Wraps an angle to -pi to pi
private fun wrapAngle(angle: Float) = angle - FastMath.TWO_PI * floor((angle + FastMath.PI) / FastMath.TWO_PI)

fun constrainOnAxis(
	rotation: Quaternion,
	minAngle: Float,
	maxAngle: Float,
	axis: Vector3,
): Quaternion {
	val angle = rotation.angleAboutR(axis)
	return if (angle !in minAngle..maxAngle) {
		val distToMin = abs(wrapAngle(angle - minAngle))
		val distToMax = abs(wrapAngle(angle - maxAngle))
		val magnitude = if (distToMin < distToMax) minAngle else maxAngle

		// Could do Quaternion.fromRotationVector(axis * magnitude) but this is more optimized
		// and constraints run several times per frame because of IK.
		val halfMagnitude = magnitude * 0.5f
		Quaternion(
			cos(halfMagnitude),
			axis * sin(halfMagnitude),
		)
	} else {
		rotation
	}
}
