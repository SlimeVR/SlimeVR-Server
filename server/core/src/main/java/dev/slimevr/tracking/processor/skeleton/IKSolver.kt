package dev.slimevr.tracking.processor.skeleton

import dev.slimevr.tracking.processor.Bone
import dev.slimevr.tracking.trackers.Tracker
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

/*
 * Implements FABRIK (Forwards And Backwards Reaching Inverse Kinematics)
 */

class IKSolver(val root: Bone) {
	var chainList = mutableListOf<IKChain>()
	var rootChain: IKChain? = null


	fun buildChains(trackers: List<Tracker>) {
		chainList = mutableListOf()

		// extract the positional constraints
		val constraints = extractConstraints(trackers)

		// build the system of chains
		rootChain = chainBuilder(root, null, 0)
		chainList.sortBy { -it.level }

		println(chainList.size)
	}

	// convert the skeleton in to a system of chains
	// a break in a chain is created at any point that has either
	// multiple children or is positionally constrained, useless chains are discarded
	private fun chainBuilder(root: Bone, parent: IKChain?, level: Int): IKChain {
		val boneList = mutableListOf<Bone>()

		var currentBone = root
		boneList.add(currentBone)
		while(currentBone.children.size == 1) {
			currentBone = currentBone.children[0]
			boneList.add(currentBone)
		}

		val chain = IKChain(boneList, parent, level, null, null)

		if (currentBone.children.isNotEmpty()) {
			val childrenList = mutableListOf<IKChain>()

			// build child chains
			for (child in currentBone.children) {
				val childChain = chainBuilder(child, chain, level + 1)
				if (neededChain(childChain)) {
					childrenList.add(childChain)
				}
			}
			chain.children = childrenList
		}

		if (chain.children.isNotEmpty() || chain.tailConstraint != null)
			chainList.add(chain)

		return chain
	}

	// a chain is needed if there is a positional constraint in its children
	private fun neededChain(ikChain: IKChain): Boolean {
		if (ikChain.children.isEmpty() && ikChain.tailConstraint == null)
			return false

		for (c in ikChain.children) {
			if (c.tailConstraint != null)
				return true

			if (neededChain(c))
				return true
		}

		return false
	}

	private fun extractConstraints(trackers: List<Tracker>): MutableList<IKConstraint> {
		val constraintList = mutableListOf<IKConstraint>()

		// each tracker that has position is a positional constraint
		for (t in trackers) {
			if (t.hasPosition)
				constraintList.add(IKConstraint(t))
		}

		return constraintList
	}

	fun solve() {
		for (chain in chainList) {
			chain.updatePositions()
		}

		// for now run 10 iterations per tick
		for (i in 0..100) {

			//println("iter $i")
			for (chain in chainList) {
				chain.backwards()
			}
			rootChain?.forwardsMulti()

		}

		// update transforms
		root.update()

	}
}

class IKChain(val nodes: MutableList<Bone>, val parent: IKChain?, val level: Int,
				val baseConstraint: IKConstraint? , val tailConstraint: IKConstraint?) {
	val baseNode = nodes.first()
	val tailNode = nodes.last()
	var children = mutableListOf<IKChain>()
	private var positions = getPositionList()

	var target = Vector3.NULL
	val squThreshold = 0.01f

	private fun getPositionList(): MutableList<Vector3> {
		val posList = mutableListOf<Vector3>()
		for (n in nodes) {
			posList.add(n.getPosition())
		}
		posList.add(nodes.last().getTailPosition())

		return posList
	}

	fun updatePositions() {
		for (i in nodes.indices) {
			positions[i] = nodes[i].getPosition()
		}
		positions[positions.size - 1] = tailNode.getTailPosition()
	}

	fun backwards() {
		val origin = baseNode.getPosition()

		if (children.size > 1 && tailConstraint == null) {
			target /= children.size.toFloat()
		}
		else {
			// TODO set constraints here
			target = Vector3.NULL
		}

		if ((positions.last() - target).lenSq() > squThreshold) {
			//println((positions.last() - target).lenSq())

			// set the end node to target
			positions[positions.size - 1] = target

			for (i in positions.size - 2 downTo 0) {
				val direction = (positions[i] - positions[i + 1]).unit()
				positions[i] = positions[i + 1] + (direction * nodes[i].length)
			}
		}

		if (parent != null && parent.tailConstraint == null) {
			parent.target += positions[0]
		}

		// restore the base position
		positions[0] = origin

	}

	private fun forwards() {
		for (i in 1 until positions.size - 1) {
			val direction = (positions[i] - positions[i - 1]).unit()

			// TODO apply constraints here
			setBoneRotation(nodes[i - 1], direction, i - 1)

			positions[i] = positions[i - 1] + (direction * nodes[i - 1].length)
		}

		// if there are children and the tail of this chain is not constrained
		// we must average the children direction and use this to set the
		// starting point for the children
		if (children.isNotEmpty() && tailConstraint == null) {
			// reset target for next pass
			target = Vector3.NULL

			// average the children positions
			var direction = Vector3.NULL
			for (c in children) {
				direction += (c.positions[0] - positions[positions.size - 2]).unit()
			}

			// set the last position using direction
			direction /= children.size.toFloat()
			positions[positions.size - 1] = positions[positions.size - 2] + (direction * tailNode.length)

			// set the children start positions for their forward passes // TODO investigate
			for (c in children) {
				c.positions[0] = positions[positions.size - 1]
			}

			// update the Bone
			setBoneRotation(tailNode, direction, nodes.size - 1)
		}
		else if (children.isEmpty()) {
			val direction = (target - positions[positions.size - 2]).unit()
			positions[positions.size - 1] = positions[positions.size - 2] + (direction * tailNode.length)

			setBoneRotation(tailNode, direction, nodes.size - 1)
		}

	}

	fun forwardsMulti() {
		forwards()

		for (c in (children)) {
			c.forwardsMulti()
		}

	}

	private fun setBoneRotation(bone: Bone, rotationVector: Vector3, nodeIdx: Int) {
//		val parentRot = if (nodeIdx == 0) {
//			parent?.nodes?.last()?.getGlobalRotation() ?: baseNode.getGlobalRotation()
//		}
//		else  (
//			nodes[nodeIdx - 1].getGlobalRotation()
//		)
//
//		val globalRotation = Quaternion.fromRotationVector(rotationVector)
//		val localRotation = parentRot.inv() * globalRotation
//
//		bone.setRotation(localRotation)

		bone.setRotation(Quaternion.fromRotationVector(rotationVector))

	}
}

class IKConstraint(val tracker: Tracker) {


}
