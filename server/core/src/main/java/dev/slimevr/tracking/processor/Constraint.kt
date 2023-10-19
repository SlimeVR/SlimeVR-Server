package dev.slimevr.tracking.processor

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

/**
 * Represents the rotational limits of a Bone relative to its parent
 */
class Constraint(val swing: Float, val twist: Float) {
	val upAxisConstraint = Vector3.POS_Y
	val forwardAxisConstraint = Vector3.POS_X

	private fun project(vector: Vector3, onNormal: Vector3): Vector3 {
		val sqrMag = onNormal.dot(onNormal)
		val dot = vector.dot(onNormal)

		return Vector3(onNormal.x  * dot / sqrMag,
			onNormal.y * dot / sqrMag,
				onNormal.z * dot / sqrMag)

	}

	private fun decompose(rotation: Quaternion, direction: Vector3): Pair<Quaternion, Quaternion> {
		val vector = Vector3(rotation.x, rotation.y, rotation.z)
		val projection = project(vector, direction)

		val twist = Quaternion(rotation.w, projection.x, projection.y, projection.z).unit()
		val swing = rotation * twist.inv()

		return Pair(swing, twist)
	}

	private fun constrain(rotation: Quaternion, angle: Float): Quaternion {
		val length = FastMath.sin(angle)
		val sqrLength = length * length
		var vector = Vector3(rotation.x, rotation.y, rotation.z)
		var rot = rotation

		if (vector.lenSq() > sqrLength) {
			vector = vector.unit() * length
			rot = Quaternion(FastMath.sqrt(1.0f - sqrLength) * FastMath.sign(rot.w),
				vector.x, vector.y, vector.z)
		}

		return rot
	}

	fun applyConstraint(direction: Vector3, parent: Bone?): Quaternion {
		// if there is no parent or no constraint return the direction
		if (parent == null || (swing.isNaN() && twist.isNaN())) {
			return Quaternion.fromTo(Vector3.NEG_Y, direction).unit()
		}

		// get the local rotation
		val rotationGlobal = Quaternion.fromTo(Vector3.NEG_Y, direction).unit()
		val rotationLocal = parent.getGlobalRotation().inv() * rotationGlobal

		var (swingQ, twistQ) = decompose(rotationLocal, direction)

		// apply the constraints
		if (!swing.isNaN()) {
			swingQ = constrain(swingQ, swing)
		}

		if (!twist.isNaN()) {
			twistQ = constrain(twistQ, twist)
		}

		return parent.getGlobalRotation() * swingQ * twistQ
	}
}
