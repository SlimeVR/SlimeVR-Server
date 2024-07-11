package dev.slimevr.tracking.processor.skeleton

import dev.slimevr.tracking.processor.Bone
import dev.slimevr.tracking.trackers.Tracker
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

/*
 * This class implements a chain of Bones
 */

class IKChain(
	val bones: MutableList<Bone>,
	var parent: IKChain?,
	val level: Int,
	val baseConstraint: Tracker?,
	val tailConstraint: Tracker?,
) {
	companion object {
		const val CENTROID_PULL_ADJUSTMENT = 0.01f
	}

	// State variables
	private val computedBasePosition = baseConstraint?.let { IKConstraint(it) }
	private val computedTailPosition = tailConstraint?.let { IKConstraint(it) }
	var children = mutableListOf<IKChain>()
	var target = Vector3.NULL
	var distToTargetSqr = Float.POSITIVE_INFINITY
	var loosens = 0
	var locked = false
	private var centroidWeight = 1f

	fun backwards() {
		// Start at the constraint or the centroid of the children
		target = computedTailPosition?.getPosition() ?: getWeightedChildTarget()

		for (i in bones.size - 1 downTo 0) {
			val currentBone = bones[i]

			// Get the local position of the end effector and the target relative to the current node
			val endEffectorLocal = (bones.last().getTailPosition() - currentBone.getPosition()).unit()
			val targetLocal = (target - currentBone.getPosition()).unit()

			// Compute the axis of rotation and angle for this bone
			val cross = endEffectorLocal.cross(targetLocal).unit()
			if (cross.lenSq() == 0.0f) continue
			val baseAngle = acos(endEffectorLocal.dot(targetLocal).coerceIn(-1.0f, 1.0f))
			val angle = baseAngle * sign(cross.dot(cross))

			val sinHalfAngle = sin(angle / 2)
			val adjustment = Quaternion(cos(angle / 2), cross.x * sinHalfAngle, cross.y * sinHalfAngle, cross.z * sinHalfAngle)
			val correctedRot = (adjustment * currentBone.getGlobalRotation()).unit()

			setBoneRotation(currentBone, correctedRot)
		}
	}

	private fun getWeightedChildTarget(): Vector3 {
		var weightSum = 0.0f
		var sum = Vector3.NULL
		for (child in children) {
			weightSum += child.centroidWeight
			sum += child.target
		}

		return sum / weightSum
	}

	/**
	 * Resets the chain to its default state
	 */
	fun resetChain() {
		distToTargetSqr = Float.POSITIVE_INFINITY
		centroidWeight = 1f

		for (bone in bones) {
			if (loosens > 0) bone.rotationConstraint.tolerance -= IKSolver.TOLERANCE_STEP
			bone.rotationConstraint.originalRotation = bone.getGlobalRotation()
		}
		loosens -= if (loosens > 0) 1 else 0

		for (child in children) {
			child.resetChain()
		}
	}

	fun resetTrackerOffsets() {
		computedTailPosition?.reset(bones.last().getTailPosition())
		computedBasePosition?.reset(bones.first().getPosition())
	}

	/**
	 * Prevent deadlocks where the centroid becomes stuck
	 * due to two or more chains pulling equally on the centroid
	 */
	fun updateChildCentroidWeight() {
		if (children.size <= 1) return

		var closestToSolved = children.first()
		for (child in children) {
			if (child.distToTargetSqr < closestToSolved.distToTargetSqr) {
				closestToSolved = child
			}
		}

		if (closestToSolved.centroidWeight > CENTROID_PULL_ADJUSTMENT) {
			closestToSolved.centroidWeight -= CENTROID_PULL_ADJUSTMENT
		}
	}

	/**
	 * Allow constrained bones to deviate more
	 */
	fun decreaseConstraints() {
		if (locked) return
		loosens++
		for (bone in bones) {
			bone.rotationConstraint.tolerance += IKSolver.TOLERANCE_STEP
		}
	}

	/**
	 * Updates the distance to target and other fields
	 * Call on the root chain
	 */
	fun computeTargetDistance() {
		distToTargetSqr = if (computedTailPosition != null) {
			(bones.last().getTailPosition() - computedTailPosition.getPosition()).lenSq()
		} else {
			0.0f
		}

		for (chain in children) {
			chain.computeTargetDistance()
		}
	}

	/**
	 * Sets a bones rotation from a rotation vector after constraining the rotation
	 * vector with the bone's rotational constraint
	 * returns the constrained rotation
	 */
	private fun setBoneRotation(bone: Bone, rotation: Quaternion): Quaternion {
		val newRotation = bone.rotationConstraint.applyConstraint(rotation, bone)
		bone.setRotationRaw(newRotation)
		bone.update()

		return newRotation
	}
}
