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
		const val CENTROID_RECOVERY = 0.01f
	}

	// state variables
	var children = mutableListOf<IKChain>()
	var target = Vector3.NULL
	var distToTargetSqr = Float.POSITIVE_INFINITY
	var trySolve = true
	var solved = true
	private var centroidWeight = 1f
	private var positions = getPositionList()
	private var tailConstrainPosOffset = Vector3.NULL
	private var baseConstraintPosOffset = Vector3.NULL

	private fun getPositionList(): MutableList<Vector3> {
		val posList = mutableListOf<Vector3>()
		for (n in nodes) {
			posList.add(n.getPosition())
		}
		posList.add(nodes.last().getTailPosition())

		return posList
	}

	fun backwards() {
		if (!trySolve) return

		// start at the constraint or the centroid of the children
		if (tailConstraint == null && children.size > 1) {
			target /= getChildrenCentroidWeightSum()
		} else {
			target = (tailConstraint?.position?.plus(tailConstrainPosOffset)) ?: Vector3.NULL
		}

		// set the end node to target
		positions[positions.size - 1] = target

		for (i in positions.size - 2 downTo 0) {
			var direction = (positions[i] - positions[i + 1]).unit()
			direction = nodes[i].rotationConstraint
				.applyConstraintInverse(direction, nodes[i])

			positions[i] = positions[i + 1] + (direction * nodes[i].length)
		}

		if (parent != null && parent!!.tailConstraint == null) {
			parent!!.target += positions[0] * centroidWeight
		}
	}

	private fun forwards() {
		if (!trySolve) return

		if (parent != null) {
			positions[0] = parent!!.positions.last()
		} else if (baseConstraint != null) {
			positions[0] = baseConstraint.position + baseConstraintPosOffset
		}

		for (i in 1 until positions.size - 1) {
			var direction = (positions[i] - positions[i - 1]).unit()
			direction = setBoneRotation(nodes[i - 1], direction)
			positions[i] = positions[i - 1] + (direction * nodes[i - 1].length)
		}

		// point the last bone at the target
		var direction = (target - positions[positions.size - 2]).unit()
		direction = setBoneRotation(nodes.last(), direction)
		positions[positions.size - 1] = positions[positions.size - 2] + (direction * nodes.last().length)

		// reset sub-base target
		target = Vector3.NULL
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

	fun resetChain() {
		distToTargetSqr = Float.POSITIVE_INFINITY
		trySolve = true

		for (child in children) {
			child.resetChain()
		}
	}

	fun resetTrackerOffsets() {
		if (tailConstraint != null) {
			tailConstrainPosOffset = nodes.last().getTailPosition() - tailConstraint.position
		}
		if (baseConstraint != null) {
			baseConstraintPosOffset = nodes.first().getPosition() - baseConstraint.position
		}
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
			child.centroidWeight = if (centroidWeight < 1f) {
				child.centroidWeight + CENTROID_RECOVERY
			} else {
				child.centroidWeight
			}
		}

		if (closestToSolved.centroidWeight > CENTROID_PULL_ADJUSTMENT) {
			closestToSolved.centroidWeight -= CENTROID_PULL_ADJUSTMENT
		}
	}

	/**
	 * Updates the distance to target and other fields
	 * Call on the root chain only returns the sum of the
	 * distances
	 */
	fun computeTargetDistance() {
		distToTargetSqr = if (tailConstraint != null) {
			(positions.last() - (tailConstraint.position + tailConstrainPosOffset)).lenSq()
		} else {
			0.0f
		}

		for (chain in children)
			chain.computeTargetDistance()
	}

	/**
	 * Sets a bones rotation from a rotation vector after constraining the rotation
	 * vector with the bone's rotational constraint
	 * returns the constrained rotation as a vector
	 */
	private fun setBoneRotation(bone: Bone, rotationVector: Vector3): Vector3 {
		val rotation = bone.rotationConstraint.applyConstraint(rotationVector, bone)
		bone.setRotationRaw(rotation)

		// TODO optimize (this is required to update the world translation for the next bone as it uses the world
		//  rotation of the parent)
		bone.update()

		return rotation.sandwich(Vector3.NEG_Y)
	}
}
