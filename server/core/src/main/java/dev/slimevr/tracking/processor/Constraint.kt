package dev.slimevr.tracking.processor

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
	 * Apply rotational constraints to the direction vector
	 */
	protected abstract fun constraintRotation(rotation: Quaternion, thisBone: Bone): Quaternion

	/**
	 * Apply rotational constraints and if applicable force the rotation
	 * to be unchanged unless it violates the constraints
	 */
	fun applyConstraint(direction: Vector3, thisBone: Bone): Quaternion {
		if (!allowModifications) {
			return constraintRotation(thisBone.getGlobalRotation(), thisBone)
		}

		return constraintRotation(Quaternion.fromTo(Vector3.NEG_Y, direction), thisBone)
	}

	/**
	 * Apply constraints to the direction vector such that when
	 * inverted the vector would be within the constraints.
	 * This is used for constraining direction vectors on the backwards pass
	 * of the FABRIK solver.
	 */
	fun applyConstraintInverse(direction: Vector3, thisBone: Bone): Vector3 {
		return -applyConstraint(-direction, thisBone).sandwich(Vector3.NEG_Y)
	}
}
