package dev.slimevr.tracking.processor

import com.jme3.math.FastMath.sign
import com.jme3.math.FastMath.sqrt
import com.jme3.math.FastMath.sin
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

/**
 * Represents the rotational limits of a Bone relative to its parent
 */
abstract class Constraint {
	/**
	 * If false don't allow the rotation of the bone
	 * to be modified except to satisfy a constraint
	 */
	var allowModifications = true

	/**
	 * If allowModifications is false this is the
	 * allowed deviation from the original rotation
	 */
	var tolerance = 0.0f
		set(value) {
			field = value
			updateComposedField()
		}

	private var toleranceRad = 0.0f
	var originalRotation = Quaternion.NULL

	private fun updateComposedField() {
		toleranceRad = Math.toRadians(tolerance.toDouble()).toFloat()
	}

	/**
	 * Apply rotational constraints to the direction vector
	 */
	protected abstract fun constraintRotation(rotation: Quaternion, thisBone: Bone): Quaternion

	/**
	 * Apply rotational constraints and if applicable force the rotation
	 * to be unchanged unless it violates the constraints
	 */
	fun applyConstraint(direction: Vector3, thisBone: Bone): Quaternion {
		val rotation = Quaternion.fromTo(Vector3.NEG_Y, direction)
		if (allowModifications) {
			return constraintRotation(rotation, thisBone).unit()
		}

		val constrainedRotation = applyLimits(rotation, originalRotation)
		return constraintRotation(constrainedRotation, thisBone).unit()
	}

	/**
	 * Apply constraints to the direction vector such that when
	 * inverted the vector would be within the constraints.
	 * This is used for constraining direction vectors on the backwards pass
	 * of the FABRIK solver.
	 */
	fun applyConstraintInverse(direction: Vector3, thisBone: Bone): Vector3 =
		-applyConstraint(-direction, thisBone).sandwich(Vector3.NEG_Y)

	/**
	 * Limit the rotation to tolerance away from the initialRotation
	 */
	protected fun applyLimits(
		rotation: Quaternion,
		initialRotation: Quaternion,
	): Quaternion {
		val localRotation = initialRotation.inv() * rotation

		var (swingQ, twistQ) = decompose(localRotation, Vector3.NEG_Y)

		twistQ = constrain(twistQ, toleranceRad)
		swingQ = constrain(swingQ, toleranceRad)

		return initialRotation * (swingQ * twistQ)
	}

	protected fun decompose(
		rotation: Quaternion,
		twistAxis: Vector3,
	): Pair<Quaternion, Quaternion> {
		val projection = rotation.project(twistAxis)

		val twist = Quaternion(rotation.w, projection.xyz).unit()
		val swing = rotation * twist.inv()

		return Pair(swing, twist)
	}

	protected fun constrain(rotation: Quaternion, angle: Float): Quaternion {
		val magnitude = sin(angle * 0.5f)
		val magnitudeSqr = magnitude * magnitude
		var vector = rotation.xyz
		var rot = rotation

		if (vector.lenSq() > magnitudeSqr) {
			vector = vector.unit() * magnitude
			rot = Quaternion(
				sqrt(1.0f - magnitudeSqr) * sign(rot.w),
				vector.x,
				vector.y,
				vector.z,
			)
		}

		return rot
	}
}
