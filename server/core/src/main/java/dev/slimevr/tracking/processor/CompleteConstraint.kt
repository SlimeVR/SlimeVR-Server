package dev.slimevr.tracking.processor

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

/**
 * A constraint type that allows no modifications to the rotation
 */
class CompleteConstraint : Constraint() {
	override fun applyConstraint(direction: Vector3, thisBone: Bone): Quaternion {
		return thisBone.getGlobalRotation()
	}
}
