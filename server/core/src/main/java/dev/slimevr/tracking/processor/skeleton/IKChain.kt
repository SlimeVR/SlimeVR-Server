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
	// state variables
	var children = mutableListOf<IKChain>()
	var target = Vector3.NULL
	private var distToTargetSqr = Float.POSITIVE_INFINITY
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
		// start at the constraint or the centroid of the children
		if (tailConstraint == null && children.size > 1) {
			target /= getChildrenCentroidWeightSum()
		} else {
			target = tailConstraint?.position ?: Vector3.NULL
		}

		// set the end node to target
		positions[positions.size - 1] = target

		for (i in positions.size - 2 downTo 0) {
			val direction = (positions[i] - positions[i + 1]).unit()
			val constrainedDirection = nodes[i].rotationConstraint
				.applyConstraintInverse(direction, nodes[i])

			positions[i] = positions[i + 1] + (constrainedDirection * nodes[i].length)
		}

		if (parent != null) parent!!.target += positions[0] * centroidWeight
	}

	private fun forwards() {
		if (baseConstraint != null) {
			positions[0] = baseConstraint.position
		} else if (parent != null) {
			positions[0] = parent!!.positions.last()
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

	// Run the forward pass
	fun forwardsMulti() {
		forwards()

		for (c in children) {
			c.forwardsMulti()
		}
	}

	// Returns the sum of the children's centroid weights
	private fun getChildrenCentroidWeightSum(): Float {
		var sum = 0.0f
		for (child in children) {
			sum += child.centroidWeight
		}

		return sum
	}

	// Reset any vars that may be modified from the last solve
	fun resetChain() {
		centroidWeight = 1f

		for (child in children) {
			child.resetChain()
		}
	}

	// Reduce the centroid weight of the child that is closest to its target
	fun updateChildCentroidWeight() {
		if (children.size <= 1) return

		var closestToSolved = children.first()
		for (child in children) {
			if (child.distToTargetSqr < closestToSolved.distToTargetSqr) {
				closestToSolved = child
			}
		}

		// TODO remove nasty magic numbers
		if (closestToSolved.centroidWeight > 0.1f) {
			closestToSolved.centroidWeight -= 0.1f
		}
	}

	// Updates the distance to target and centroid weight variables
	// and returns the new distance to the target
	fun computeTargetDistance(): Float {
		distToTargetSqr = if (tailConstraint != null) {
			(positions.last() - (tailConstraint.position)).lenSq()
		} else {
			0.0f
		}

		return distToTargetSqr
	}

	// Sets a bones rotation from a rotation vector after constraining the rotation
	// vector with the bone's rotational constraint
	// returns the constrained rotation as a vector
	private fun setBoneRotation(bone: Bone, rotationVector: Vector3): Vector3 {
		val rotation = bone.rotationConstraint.applyConstraint(rotationVector, bone)
		bone.setRotationRaw(rotation)

		// TODO optimize (this is required to update the world translation for the next bone as it uses the world
		//  rotation of the parent)
		bone.update()

		return rotation.sandwich(Vector3.NEG_Y)
	}
}
