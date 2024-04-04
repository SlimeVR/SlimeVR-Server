package dev.slimevr.tracking.processor

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

/**
 * A constraint type that allows for a rotation to be constrained relative to the parent
 * of the supplied bone
 */
class TwistSwingConstraint(twist: Float, swing: Float) : Constraint() {
	private val twistRad = Math.toRadians(twist.toDouble()).toFloat()
	private val swingRad = Math.toRadians(swing.toDouble()).toFloat()

	override fun constraintRotation(rotation: Quaternion, thisBone: Bone): Quaternion {
		if (thisBone.parent == null) return rotation
		val parent = thisBone.parent!!

		val rotationLocal = (parent.getGlobalRotation() * thisBone.rotationOffset).inv() * rotation
		var (swingQ, twistQ) = decompose(rotationLocal, Vector3.NEG_Y)

		swingQ = constrain(swingQ, swingRad)
		twistQ = constrain(twistQ, twistRad)

		return parent.getGlobalRotation() * thisBone.rotationOffset * (swingQ * twistQ)
	}
}
