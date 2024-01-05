package dev.slimevr.tracking.processor

import io.github.axisangles.ktmath.Quaternion

/**
 * A constraint type that allows no modifications to the rotation
 */
class CompleteConstraint : Constraint() {
	override fun constraintRotation(rotation: Quaternion, thisBone: Bone): Quaternion {
		return thisBone.getGlobalRotation()
	}
}
