package dev.slimevr.tracking.processor

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

class HingeConstraint(val min: Float, val max: Float) : Constraint()  {
	override fun applyConstraint(direction: Vector3, parent: Bone?): Quaternion {
		return Quaternion.fromTo(Vector3.NEG_Y, direction).unit()
	}
}
