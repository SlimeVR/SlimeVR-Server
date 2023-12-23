package dev.slimevr.tracking.processor.skeleton

import dev.slimevr.tracking.processor.Bone
import dev.slimevr.tracking.trackers.Tracker
import io.github.axisangles.ktmath.Vector3

/*
 * This class implements a chain of Bones for use by the FABRIK solver
 */

class IKChain(val nodes: MutableList<Bone>, var parent: IKChain?, val level: Int,
			  val baseConstraint: Tracker?, val tailConstraint: Tracker?) {
	// state variables
	var children = mutableListOf<IKChain>()
	var target = Vector3.NULL
	private var positions = getPositionList()
	var firstBonePerturbation = 0

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
		if (tailConstraint == null && children.size > 1)
			target /= children.size.toFloat()
		else
			target = tailConstraint?.position ?: Vector3.NULL

		// set the end node to target
		positions[positions.size - 1] = target

		for (i in positions.size - 2 downTo 0) {
			val direction = (positions[i] - positions[i + 1]).unit()
			positions[i] = positions[i + 1] + (direction * nodes[i].length)
		}

		if (parent != null)
			parent!!.target += positions[0]
	}

	private fun forwards() {
		if (baseConstraint != null)
			positions[0] = baseConstraint.position
		else if (parent != null)
			positions[0] = parent!!.positions.last()

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

	fun forwardsMulti() {
		forwards()

		for (c in children) {
			c.forwardsMulti()
		}
	}

	fun getTargetDistance(): Float {
		return (positions.last() - (tailConstraint?.position ?: Vector3.NULL)).lenSq()
	}

	// Sets a bones rotation from a rotation vector after constraining the rotation
	// vector with the bone's rotational constraint
	// returns the constrained rotation as a vector
	private fun setBoneRotation(bone: Bone, rotationVector: Vector3): Vector3 {
		// TODO if a bone has a tracker associated with it force that rotation


		val rotation = bone.rotationConstraint.applyConstraint(rotationVector, bone.parent)
		bone.setRotationRaw(rotation)

		return rotation.sandwich(Vector3.NEG_Y)
	}
}
