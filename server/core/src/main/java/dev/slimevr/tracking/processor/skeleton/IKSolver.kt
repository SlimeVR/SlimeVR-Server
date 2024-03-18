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
		const val TOLERANCE_SQR = 1e-8 // == 0.01 cm
		const val MAX_ITERATIONS = 100
		const val ITERATIONS_BEFORE_STEP = MAX_ITERATIONS / 8
		const val TOLERANCE_STEP = 2f
	}

	private var chainList = mutableListOf<IKChain>()
	private var rootChain: IKChain? = null
	private var needsReset = false

	/**
	 * Any time the skeleton is rebuilt or trackers are assigned / unassigned the chains
	 * should be rebuilt.
	 */
	fun buildChains(trackers: List<Tracker>) {
		chainList.clear()

		// Extract the positional constraints
		val positionalConstraints = extractPositionalConstraints(trackers)
		val rotationalConstraints = extractRotationalConstraints(trackers)

		// Build the system of chains
		rootChain = chainBuilder(root, null, 0, positionalConstraints, rotationalConstraints)
		populateChainList(rootChain!!)

		// Check if there is any constraints (other than the head) in the model
		rootChain = if (neededChain(rootChain!!)) rootChain else null
		chainList.sortBy { -it.level }
	}

	/**
	 * Reset the offsets of positional trackers. Should only be called right after a full reset
	 * is performed.
	 */
	fun resetOffsets() {
		needsReset = true
	}

	/**
	 * Convert the skeleton in to a system of chains.
	 * A break in a chain is created at any point that has either
	 * multiple children or is positionally constrained, useless chains are discarded
	 * (useless chains are chains with no positional constraint at their tail).
	 */
	private fun chainBuilder(
		root: Bone,
		parent: IKChain?,
		level: Int,
		positionalConstraints: MutableList<Tracker>,
		rotationalConstraints: MutableList<Tracker>,
	): IKChain {
		val boneList = mutableListOf<Bone>()
		var currentBone = root
		currentBone.rotationConstraint.allowModifications =
			getConstraint(currentBone, rotationalConstraints) == null
		boneList.add(currentBone)

		// Get constraints
		val baseConstraint = if (parent == null) {
			getConstraint(boneList.first(), positionalConstraints)
		} else {
			parent.tailConstraint
		}
		var tailConstraint: Tracker? = null

		// Add bones until there is a reason to make a new chain
		while (currentBone.children.size == 1 && tailConstraint == null) {
			currentBone = currentBone.children[0]
			currentBone.rotationConstraint.allowModifications =
				getConstraint(currentBone, rotationalConstraints) == null
			boneList.add(currentBone)
			tailConstraint = getConstraint(boneList.last(), positionalConstraints)
		}

		var chain = IKChain(boneList, parent, level, baseConstraint, tailConstraint)

		if (currentBone.children.isNotEmpty()) {
			val childrenList = mutableListOf<IKChain>()

			// Build child chains
			for (child in currentBone.children) {
				val childChain = chainBuilder(child, chain, level + 1, positionalConstraints, rotationalConstraints)
				if (neededChain(childChain)) {
					childrenList.add(childChain)
				}
			}
			chain.children = childrenList
		}

		// If the chain has only one child and no tail constraint combine the chains
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

	private fun extractPositionalConstraints(trackers: List<Tracker>): MutableList<Tracker> {
		val constraintList = mutableListOf<Tracker>()
		for (t in trackers) {
			if (t.hasPosition &&
				!t.isInternal &&
				!t.status.reset
			) {
				constraintList.add(t)
			}
		}
		return constraintList
	}

	private fun extractRotationalConstraints(trackers: List<Tracker>): MutableList<Tracker> {
		val constrainList = mutableListOf<Tracker>()
		for (t in trackers) {
			if (t.hasRotation &&
				!t.status.reset &&
				!t.isInternal
			) {
				constrainList.add(t)
			}
		}

		return constrainList
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
		if (rootChain == null) return

		var solved: Boolean
		if (needsReset) {
			for (c in chainList) {
				c.resetTrackerOffsets()
			}
			needsReset = false
		}

		rootChain?.resetChain()

		// run up to MAX_ITERATIONS per tick
		for (i in 0 until MAX_ITERATIONS) {
			for (chain in chainList) {
				chain.backwards()
			}
			rootChain?.forwardsMulti()

			rootChain?.computeTargetDistance()

			// If all chains have reached their target the chain is solved
			solved = true
			for (chain in chainList) {
				if (chain.distToTargetSqr > TOLERANCE_SQR) {
					solved = false
				}
			}

			if (solved) break

			// Help the chains out of a deadlock
			for (chain in chainList) {
				chain.updateChildCentroidWeight()
			}

			// Loosen rotational constraints
			// TODO only do this if a positional tracker down the chain is actually
			// tracking accurately
			if (i % ITERATIONS_BEFORE_STEP == 0 && i != 0) {
				for (chain in chainList) {
					chain.decreaseConstraints()
				}
			}
		}

		root.update()
	}
}
