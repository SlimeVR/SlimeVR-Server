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
		rootChain = chainBuilder(root, null, 0, constraints)
		populateChainList(rootChain!!)

		// check if there is any constraints (other than the head) in the model
		rootChain = if (neededChain(rootChain!!)) rootChain else null
		chainList.sortBy { -it.level }

		print("DEBUG chainList.size = ")
		println(chainList.size)
	}

	// convert the skeleton in to a system of chains
	// a break in a chain is created at any point that has either
	// multiple children or is positionally constrained, useless chains are discarded
	private fun chainBuilder(root: Bone, parent: IKChain?, level: Int, constraints: MutableList<IKConstraint>): IKChain {
		val boneList = mutableListOf<Bone>()
		var currentBone = root
		boneList.add(currentBone)

		// get constraints
		val baseConstraint = if (parent == null) getConstraint(boneList.first(), constraints)
							 else parent.tailConstraint
		var tailConstraint: IKConstraint? = null

		// add bones until there is a reason to make a new chain
		while(currentBone.children.size == 1 && tailConstraint == null) {
			currentBone = currentBone.children[0]
			boneList.add(currentBone)
			tailConstraint = getConstraint(boneList.last(), constraints)
		}

		var chain = IKChain(boneList, parent, level, baseConstraint, tailConstraint)

		if (currentBone.children.isNotEmpty()) {
			val childrenList = mutableListOf<IKChain>()

			// build child chains
			for (child in currentBone.children) {
				val childChain = chainBuilder(child, chain, level + 1, constraints)
				if (neededChain(childChain)) {
					childrenList.add(childChain)
				}
			}
			chain.children = childrenList
		}

		// if the chain has only one child and no tail constraint combine the chains
		if (chain.children.size == 1 && chain.tailConstraint == null)
			chain = combineChains(chain, chain.children[0])

		return chain
	}

	private fun populateChainList(chain: IKChain) {
		chainList.add(chain)
		for (c in chain.children) {
			populateChainList(c)
		}
	}

	private fun combineChains(chain: IKChain, childChain: IKChain): IKChain {
		val boneList = mutableListOf<Bone>()
		boneList.addAll(chain.nodes)
		boneList.addAll(childChain.nodes)

		val newChain = IKChain(boneList, chain.parent, chain.level,
			chain.baseConstraint, childChain.tailConstraint)

		newChain.children = childChain.children

		for (c in newChain.children) {
			c.parent = newChain
		}

		return newChain
	}

	// a chain is needed if there is a positional constraint in its children
	private fun neededChain(chain: IKChain): Boolean {
		if (chain.tailConstraint != null) {
			return true
		}

		for (c in chain.children) {
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
			if (t.hasPosition && !t.isInternal &&
				!t.status.reset)
				constraintList.add(IKConstraint(t))
		}
		return constraintList
	}

	private fun getConstraint(bone: Bone, constraints: MutableList<IKConstraint>): IKConstraint? {
		for (c in constraints) {
			if (bone.boneType.bodyPart == (c.tracker.trackerPosition?.bodyPart ?: 0)) {
				constraints.remove(c)
				return c
			}
		}
		return null
	}

	fun solve() {
		// for now run 100 iterations per tick
		for (i in 0..100) {
			for (chain in chainList) {
				chain.backwards()
			}
			rootChain?.forwardsMulti()
		}
		// update transforms
		root.update()
	}
}

class IKChain(val nodes: MutableList<Bone>, var parent: IKChain?, val level: Int,
				val baseConstraint: IKConstraint? , val tailConstraint: IKConstraint?) {
	var children = mutableListOf<IKChain>()
	var positions = getPositionList()
	var target = Vector3.NULL

	private val squThreshold = 0.00001f

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
			target = tailConstraint?.tracker?.position ?: Vector3.NULL

		if ((positions.last() - target).lenSq() > squThreshold) {
			// set the end node to target
			positions[positions.size - 1] = target

			for (i in positions.size - 2 downTo 0) {
				val direction = (positions[i] - positions[i + 1]).unit()
				positions[i] = positions[i + 1] + (direction * nodes[i].length)
			}
		}

		if (parent != null)
			parent!!.target += positions[0]
	}

	private fun forwards() {
		if (baseConstraint != null)
			positions[0] = baseConstraint.tracker.position
		else if (parent != null)
			positions[0] = parent!!.positions.last()

		for (i in 1 until positions.size - 1) {
			val direction = (positions[i] - positions[i - 1]).unit()

			// TODO apply constraints here
			setBoneRotation(nodes[i - 1], direction)

			positions[i] = positions[i - 1] + (direction * nodes[i - 1].length)
		}

		// set the last node position to point at the centroid of the children
		// or the positional constraint
		if (children.isNotEmpty() && tailConstraint == null) {
			val direction = (target - positions[positions.size - 2]).unit()
			positions[positions.size - 1] = positions[positions.size - 2] + (direction * nodes.last().length)
			setBoneRotation(nodes.last(), direction)
		}
		else if (tailConstraint != null) {
			val direction = (tailConstraint.tracker.position - positions[positions.size - 2]).unit()
			positions[positions.size - 1] = positions[positions.size - 2] + (direction * nodes.last().length)
			setBoneRotation(nodes.last(), direction)
		}

		target = Vector3.NULL
	}

	fun forwardsMulti() {
		forwards()

		for (c in children) {
			c.forwardsMulti()
		}
	}

	private fun setBoneRotation(bone: Bone, rotationVector: Vector3) {
		bone.setRotationRaw(Quaternion.fromTo(Vector3.NEG_Y, rotationVector).unit())
	}
}

class IKConstraint(val tracker: Tracker) {


}
