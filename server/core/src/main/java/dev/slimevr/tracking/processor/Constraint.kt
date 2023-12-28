package dev.slimevr.tracking.processor

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

/**
 * Represents the rotational limits of a Bone relative to its parent
 */
abstract class Constraint {
	/**
	 * Apply constraints to the direction vector
	 */
	abstract fun applyConstraint(direction: Vector3, thisBone: Bone): Quaternion

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
