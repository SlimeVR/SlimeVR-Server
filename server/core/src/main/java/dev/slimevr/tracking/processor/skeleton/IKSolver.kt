package dev.slimevr.tracking.processor.skeleton

import dev.slimevr.tracking.processor.Bone
import dev.slimevr.tracking.trackers.Tracker

/*
 * Implements FABRIK (Forwards And Backwards Reaching Inverse Kinematics) and allows
 * positional trackers such as vive/tundra trackers to be used in conjunction with
 * IMU trackers
 */

class IKSolver(private val root: Bone) {
	companion object {
		private const val TOLERANCE_SQR = 1e-8 // == 0.0001 cm
		private const val MAX_ITERATIONS = 100
	}

	private var chainList = mutableListOf<IKChain>()
	private var rootChain: IKChain? = null

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
	}

	// convert the skeleton in to a system of chains
	// a break in a chain is created at any point that has either
	// multiple children or is positionally constrained, useless chains are discarded
	private fun chainBuilder(root: Bone, parent: IKChain?, level: Int, constraints: MutableList<Tracker>): IKChain {
		val boneList = mutableListOf<Bone>()
		var currentBone = root
		boneList.add(currentBone)

		// get constraints
		val baseConstraint = if (parent == null) {
			getConstraint(boneList.first(), constraints)
		} else {
			parent.tailConstraint
		}
		var tailConstraint: Tracker? = null

		// add bones until there is a reason to make a new chain
		while (currentBone.children.size == 1 && tailConstraint == null) {
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
		if (chain.children.size == 1 && chain.tailConstraint == null) {
			chain = combineChains(chain, chain.children[0])
		}

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

		val newChain = IKChain(
			boneList,
			chain.parent,
			chain.level,
			chain.baseConstraint,
			childChain.tailConstraint
		)

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
			if (c.tailConstraint != null) {
				return true
			}

			if (neededChain(c)) {
				return true
			}
		}

		return false
	}

	private fun extractConstraints(trackers: List<Tracker>): MutableList<Tracker> {
		val constraintList = mutableListOf<Tracker>()
		for (t in trackers) {
			if (t.hasPosition && !t.isInternal &&
				!t.status.reset
			) {
				constraintList.add(t)
			}
		}
		return constraintList
	}

	private fun getConstraint(bone: Bone, constraints: MutableList<Tracker>): Tracker? {
		for (c in constraints) {
			if (bone.boneType.bodyPart == (c.trackerPosition?.bodyPart ?: 0)) {
				constraints.remove(c)
				return c
			}
		}
		return null
	}

	fun solve() {
		rootChain?.resetChain()

		// run up to MAX_ITERATIONS iterations per tick
		for (i in 0..MAX_ITERATIONS) {
			for (chain in chainList) {
				chain.backwards()
			}
			rootChain?.forwardsMulti()

			// if all chains have reached their target the chain is solved
			var solved = true
			for (chain in chainList) {
				if (chain.computeTargetDistance() > TOLERANCE_SQR) {
					solved = false
					break
				}
			}

			if (solved) break

			// help the chains out of a deadlock
			for (chain in chainList) {
				chain.updateChildCentroidWeight()
			}
		}

		// update transforms
		root.update()
	}
}
