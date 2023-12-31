package dev.slimevr.tracking.processor

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

class HingeConstraint(val min: Float, val max: Float) : Constraint() {
	override fun constraintRotation(direction: Vector3, thisBone: Bone): Quaternion {
		return Quaternion.fromTo(Vector3.NEG_Y, direction).unit()
	}
}
