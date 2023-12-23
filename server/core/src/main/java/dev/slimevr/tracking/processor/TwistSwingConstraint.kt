package dev.slimevr.tracking.processor

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3


class TwistSwingConstraint(val twist: Float, val swing: Float) : Constraint()  {
	private fun project(vector: Vector3, onNormal: Vector3): Vector3 {
		val sqrMag = onNormal.lenSq()
		val dot = vector.dot(onNormal)
		return onNormal * (dot / sqrMag)
	}

	private fun decompose(rotation: Quaternion, twistAxis: Vector3): Pair<Quaternion, Quaternion> {
		val vector = Vector3(rotation.x, rotation.y, rotation.z)
		val projection = project(vector, twistAxis)

		val twist = Quaternion(rotation.w, projection.x, projection.y, projection.z).unit()
		val swing = rotation * twist.inv()

		return Pair(swing, twist)
	}

	private fun constrain(rotation: Quaternion, angle: Float): Quaternion {
		val length = FastMath.sin(Math.toRadians(angle.toDouble()).toFloat())
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

	override fun applyConstraint(direction: Vector3, parent: Bone?): Quaternion {
		// if there is no parent or no constraint return the direction
		if (parent == null || (swing.isNaN() && twist.isNaN())) {
			return Quaternion.fromTo(Vector3.NEG_Y, direction).unit()
		}

		// get the local rotation
		val rotationGlobal = Quaternion.fromTo(Vector3.NEG_Y, direction).unit()
		val rotationLocal = parent.getGlobalRotation().inv() * rotationGlobal

		var (swingQ, twistQ) = decompose(rotationLocal, Vector3.NEG_Y)

		// apply the constraints
		if (!swing.isNaN()) {
			swingQ = constrain(swingQ, swing)
		}

		if (!twist.isNaN()) {
			twistQ = constrain(twistQ, twist)
		}

		return parent.getGlobalRotation() * (swingQ * twistQ) * parent.getGlobalRotation().inv()
	}
}