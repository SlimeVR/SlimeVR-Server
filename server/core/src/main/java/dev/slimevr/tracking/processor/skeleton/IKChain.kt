package dev.slimevr.tracking.processor.skeleton

import dev.slimevr.tracking.processor.Bone
import dev.slimevr.tracking.trackers.Tracker
import io.github.axisangles.ktmath.Vector3

/*
 * This class implements a chain of Bones for use by the FABRIK solver
 */

class IKChain(
	val nodes: MutableList<Bone>,
	var parent: IKChain?,
	val level: Int,
	val baseConstraint: Tracker?,
	val tailConstraint: Tracker?,
) {
	companion object {
		const val CENTROID_PULL_ADJUSTMENT = 0.1f
	}

	// State variables
	private val computedBasePosition = baseConstraint?.let { IKConstraint(it) }
	private val computedTailPosition = tailConstraint?.let { IKConstraint(it) }
	var children = mutableListOf<IKChain>()
	private var targetSum = Vector3.NULL
	var target = Vector3.NULL
	var distToTargetSqr = Float.POSITIVE_INFINITY
	var loosens = 0
	private var centroidWeight = 1f
	private var positions = getPositionList()

	private fun getPositionList(): MutableList<Vector3> {
		val posList = mutableListOf<Vector3>()
		for (n in nodes) {
			posList.add(n.getPosition())
		}
		posList.add(nodes.last().getTailPosition())

		return posList
	}

	fun backwards() {
		// Start at the constraint or the centroid of the children
		target = if (computedTailPosition == null && children.size > 1) {
			targetSum / getChildrenCentroidWeightSum()
		} else {
			(computedTailPosition?.getPosition()) ?: Vector3.NULL
		}

		positions[positions.size - 1] = target

		for (i in positions.size - 2 downTo 0) {
			var direction = (positions[i] - positions[i + 1]).unit()
			direction = nodes[i].rotationConstraint
				.applyConstraintInverse(direction, nodes[i])

			positions[i] = positions[i + 1] + (direction * nodes[i].length)
		}

		if (parent != null && parent!!.computedTailPosition == null) {
			parent!!.targetSum += positions[0] * centroidWeight
		}
	}

	private fun forwards() {
		positions[0] = if (parent != null) {
			parent!!.nodes.last().getTailPosition()
		} else {
			(computedBasePosition?.getPosition()) ?: positions[0]
		}

		for (i in 1 until positions.size - 1) {
			var direction = (positions[i] - positions[i - 1]).unit()
			direction = setBoneRotation(nodes[i - 1], direction)
			positions[i] = positions[i - 1] + (direction * nodes[i - 1].length)
		}

		var direction = (target - positions[positions.size - 2]).unit()
		direction = setBoneRotation(nodes.last(), direction)
		positions[positions.size - 1] = positions[positions.size - 2] + (direction * nodes.last().length)

		// reset sub-base target
		targetSum = Vector3.NULL
	}

	/**
	 * Run the forward pass
	 */
	fun forwardsMulti() {
		forwards()

		for (c in children) {
			c.forwardsMulti()
		}
	}

	private fun getChildrenCentroidWeightSum(): Float {
		var sum = 0.0f
		for (child in children) {
			sum += child.centroidWeight
		}

		return sum
	}

	/**
	 * Resets the chain to its default state
	 */
	fun resetChain() {
		distToTargetSqr = Float.POSITIVE_INFINITY
		centroidWeight = 1f
		loosens = 0

		for (bone in nodes) {
			bone.rotationConstraint.tolerance = 0.0f
			bone.rotationConstraint.originalRotation = bone.getGlobalRotation()
		}

		for (child in children) {
			child.resetChain()
		}
	}

	fun resetTrackerOffsets() {
		computedTailPosition?.reset(nodes.last().getTailPosition())
		computedBasePosition?.reset(nodes.first().getPosition())
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
		loosens++
		for (bone in nodes) {
			bone.rotationConstraint.tolerance += IKSolver.TOLERANCE_STEP
		}
	}

	/**
	 * Updates the distance to target and other fields
	 * Call on the root chain only returns the sum of the
	 * distances
	 */
	fun computeTargetDistance() {
		distToTargetSqr = if (computedTailPosition != null) {
			(positions.last() - computedTailPosition.getPosition()).lenSq()
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
	 * returns the constrained rotation as a vector
	 */
	private fun setBoneRotation(bone: Bone, rotationVector: Vector3): Vector3 {
		val rotation = bone.rotationConstraint.applyConstraint(rotationVector, bone)
		bone.setRotationRaw(rotation)

		bone.updateThisNode()

		return rotation.sandwich(Vector3.NEG_Y)
	}
}
