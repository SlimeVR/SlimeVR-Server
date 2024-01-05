package dev.slimevr.tracking.processor

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

/**
 * A constraint type that allows for a rotation to be constrained relative to the parent
 * of the supplied bone
 */
class TwistSwingConstraint(private val twist: Float, private val swing: Float) : Constraint() {
	private val twistRad = Math.toRadians(twist.toDouble()).toFloat()
	private val swingRad = Math.toRadians(swing.toDouble()).toFloat()

	private fun decompose(rotation: Quaternion, twistAxis: Vector3): Pair<Quaternion, Quaternion> {
		val projection = rotation.project(twistAxis)

		val twist = Quaternion(rotation.w, projection.xyz).unit()
		val swing = rotation * twist.inv()

		return Pair(swing, twist)
	}

	private fun constrain(rotation: Quaternion, angle: Float): Quaternion {
		val magnitude = FastMath.sin(angle * 0.5f)
		val magnitudeSqr = magnitude * magnitude
		var vector = rotation.xyz
		var rot = rotation

		if (vector.lenSq() > magnitudeSqr) {
			vector = vector.unit() * magnitude
			rot = Quaternion(
				FastMath.sqrt(1.0f - magnitudeSqr) * FastMath.sign(rot.w),
				vector.x,
				vector.y,
				vector.z
			)
		}

		return rot
	}

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
