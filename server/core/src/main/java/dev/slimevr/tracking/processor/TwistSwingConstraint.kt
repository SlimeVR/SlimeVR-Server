package dev.slimevr.tracking.processor

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

/**
 * A constraint type that allows for a rotation to be constrained relative to the parent
 * of the supplied bone
 */
class TwistSwingConstraint(private val twist: Float, private val swing: Float) : Constraint() {
	private val twistRad = Math.toRadians(twist.toDouble()).toFloat()
	private val swingRad = Math.toRadians(swing.toDouble()).toFloat()

	override fun constraintRotation(rotation: Quaternion, thisBone: Bone): Quaternion {
		// if there is no parent or no constraint return the direction
		if (thisBone.parent == null || (swing.isNaN() && twist.isNaN())) {
			return rotation
		}

		val parent = thisBone.parent!!

		// get the local rotation
		val rotationLocal = (parent.getGlobalRotation() * thisBone.rotationOffset).inv() * rotation

		// decompose in to twist and swing
		var (swingQ, twistQ) = decompose(rotationLocal, Vector3.NEG_Y)

		// apply the constraints
		if (!swing.isNaN()) swingQ = constrain(swingQ, swingRad)
		if (!twist.isNaN()) twistQ = constrain(twistQ, twistRad)

		return parent.getGlobalRotation() * thisBone.rotationOffset * (swingQ * twistQ)
	}
}
