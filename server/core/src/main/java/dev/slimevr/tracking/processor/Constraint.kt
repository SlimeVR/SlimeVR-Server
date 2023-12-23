package dev.slimevr.tracking.processor

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

/**
 * Represents the rotational limits of a Bone relative to its parent
 */
abstract class Constraint {
	abstract fun applyConstraint(direction: Vector3, parent: Bone?): Quaternion
}
