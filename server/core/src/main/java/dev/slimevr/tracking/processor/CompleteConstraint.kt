package dev.slimevr.tracking.processor

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

/**
 * A constraint type that allows no modifications to the rotation
 */
class CompleteConstraint : Constraint() {
	override fun constraintRotation(rotation: Quaternion, thisBone: Bone): Quaternion {
		return thisBone.getGlobalRotation()
	}
}
