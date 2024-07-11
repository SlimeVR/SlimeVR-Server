package dev.slimevr.tracking.processor

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.abs
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Represents a function that applies a rotational constraint to a direction vector.
 */
typealias ConstraintFunction = (Quaternion, Bone, Float, Float) -> Quaternion

/**
 * Represents the rotational limits of a Bone relative to its parent
 */
class Constraint(
	val constraintFunction: ConstraintFunction,
	twist: Float = 0.0f,
	swing: Float = 0.0f,
) {
	private val twistRad = Math.toRadians(twist.toDouble()).toFloat()
	private val swingRad = Math.toRadians(swing.toDouble()).toFloat()

	/**
	 * If false don't allow the rotation of the bone
	 * to be modified except to satisfy a constraint
	 */
	var allowModifications = true

	/**
	 * If allowModifications is false this is the
	 * allowed deviation from the original rotation
	 */
	var tolerance = 0.0f
		set(value) {
			field = value
			updateComposedFields()
		}

	private var toleranceRad = 0.0f
	var originalRotation = Quaternion.IDENTITY

	private fun updateComposedFields() {
		toleranceRad = Math.toRadians(tolerance.toDouble()).toFloat()
	}

	/**
	 * Apply rotational constraints and if applicable force the rotation
	 * to be unchanged unless it violates the constraints
	 */
	fun applyConstraint(rotation: Quaternion, thisBone: Bone): Quaternion {
		if (allowModifications) {
			return constraintFunction(rotation, thisBone, swingRad, twistRad).unit()
		}

		val constrainedRotation = applyLimits(rotation, originalRotation)
		return constraintFunction(constrainedRotation, thisBone, swingRad, twistRad).unit()
	}

	/**
	 * Limit the rotation to tolerance away from the initialRotation
	 */
	private fun applyLimits(
		rotation: Quaternion,
		initialRotation: Quaternion,
	): Quaternion {
		val localRotation = initialRotation.inv() * rotation

		var (swingQ, twistQ) = decompose(localRotation, Vector3.NEG_Y)

		twistQ = constrain(twistQ, toleranceRad)
		swingQ = constrain(swingQ, toleranceRad)

		return initialRotation * (swingQ * twistQ)
	}

	companion object {
		private fun decompose(
			rotation: Quaternion,
			twistAxis: Vector3,
		): Pair<Quaternion, Quaternion> {
			val projection = rotation.project(twistAxis)

			val twist = Quaternion(rotation.w, projection.xyz).unit()
			val swing = rotation * twist.inv()

			return Pair(swing, twist)
		}

		private fun constrain(rotation: Quaternion, angle: Float): Quaternion {
			val magnitude = sin(angle * 0.5f)
			val magnitudeSqr = magnitude * magnitude
			val sign = if (rotation.w != 0f) sign(rotation.w) else 1f
			var vector = rotation.xyz
			var rot = rotation

			if (vector.lenSq() > magnitudeSqr) {
				vector = vector.unit() * magnitude
				rot = Quaternion(
					sqrt(1.0f - magnitudeSqr) * sign,
					vector.x,
					vector.y,
					vector.z,
				)
			}

			return rot
		}

		private fun constrain(rotation: Quaternion, minAngle: Float, maxAngle: Float, axis: Vector3): Quaternion {
			val magnitudeMin = sin(minAngle * 0.5f)
			val magnitudeMax = sin(maxAngle * 0.5f)
			val magnitudeSqrMin = magnitudeMin * magnitudeMin * if (minAngle != 0f) sign(minAngle) else 1f
			val magnitudeSqrMax = magnitudeMax * magnitudeMax * if (maxAngle != 0f) sign(maxAngle) else 1f
			var vector = rotation.xyz
			var rot = rotation

			val rotMagnitude = vector.lenSq() * if (vector.dot(axis) < 0) -1f else 1f
			if (rotMagnitude < magnitudeSqrMin || rotMagnitude > magnitudeSqrMax) {
				val magnitude = if (rotMagnitude < magnitudeSqrMin) magnitudeMin else magnitudeMax
				val magnitudeSqr = abs(if (rotMagnitude < magnitudeSqrMin) magnitudeSqrMin else magnitudeSqrMax)
				vector = vector.unit() * magnitude
				rot = Quaternion(
					sqrt(1.0f - magnitudeSqr),
					vector.x,
					vector.y,
					vector.z,
				)
			}

			return rot
		}

		// Constraint function for TwistSwingConstraint
		val twistSwingConstraint: ConstraintFunction =
			{ rotation: Quaternion, thisBone: Bone, swingRad: Float, twistRad: Float ->
				if (thisBone.parent == null) {
					rotation
				} else {
					val parent = thisBone.parent!!
					val rotationLocal =
						(parent.getGlobalRotation() * thisBone.rotationOffset).inv() * rotation
					var (swingQ, twistQ) = decompose(rotationLocal, Vector3.NEG_Y)

					swingQ = constrain(swingQ, swingRad)
					twistQ = constrain(twistQ, twistRad)

					parent.getGlobalRotation() * thisBone.rotationOffset * (swingQ * twistQ)
				}
			}

		// Constraint function for a hinge constraint with min and max angles
		val hingeConstraint: ConstraintFunction =
			{ rotation: Quaternion, thisBone: Bone, min: Float, max: Float ->
				if (thisBone.parent == null) {
					rotation
				} else {
					val parent = thisBone.parent!!
					val rotationLocal =
						(parent.getGlobalRotation() * thisBone.rotationOffset).inv() * rotation
					var (_, hingeAxisRot) = decompose(rotationLocal, Vector3.NEG_X)

					hingeAxisRot = constrain(hingeAxisRot, min, max, Vector3.NEG_X)

					parent.getGlobalRotation() * thisBone.rotationOffset * hingeAxisRot
				}
			}

		// Constraint function for CompleteConstraint
		val completeConstraint: ConstraintFunction = { _: Quaternion, thisBone: Bone, _: Float, _: Float ->
			thisBone.getGlobalRotation()
		}
	}
}
