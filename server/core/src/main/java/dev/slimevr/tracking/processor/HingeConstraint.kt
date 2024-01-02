package dev.slimevr.tracking.processor

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

class HingeConstraint(val min: Float, val max: Float) : Constraint() {
	override fun constraintRotation(rotation: Quaternion, thisBone: Bone): Quaternion {
		return rotation
	}
}
