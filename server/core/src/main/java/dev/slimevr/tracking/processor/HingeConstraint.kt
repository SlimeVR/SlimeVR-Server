package dev.slimevr.tracking.processor

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

/**
 * Constrain a rotation to one axis and a min and max value for that axis.
 */
class HingeConstraint(val min: Float, val max: Float, val axis: Vector3) : Constraint() {
	override fun constraintRotation(rotation: Quaternion, thisBone: Bone): Quaternion {
		// if there is no parent or no constraint return the direction
		if (thisBone.parent == null) {
			return rotation
		}

		val parent = thisBone.parent!!

		// get the local rotation
		val rotationLocal = (parent.getGlobalRotation() * thisBone.rotationOffset).inv() * rotation

		// project the rotation onto the axis
		val projected = axis.dot(rotationLocal.xyz)

		// clamp the angle to the min and max values
		val clampedAngle = FastMath.clamp(
			projected,
			Math.toRadians(min.toDouble()).toFloat(),
			Math.toRadians(max.toDouble()).toFloat()
		)

		// reconstruct the rotation
		val rot = Quaternion(rotationLocal.w, axis * clampedAngle).unit()

		// return the constrained rotation
		return parent.getGlobalRotation() * thisBone.rotationOffset * rot
	}
}
