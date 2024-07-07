package dev.slimevr.tracking.processor.skeleton

import dev.slimevr.tracking.processor.Bone
import dev.slimevr.tracking.trackers.Tracker

/*
 * Implements FABRIK (Forwards And Backwards Reaching Inverse Kinematics) to allow
 * positional trackers such as vive/tundra trackers to be used in conjunction with
 * IMU trackers
 */

class IKSolver(private val root: Bone) {
	companion object {
		const val TOLERANCE_SQR = 1e-8 // == 0.01 cm
		const val MAX_ITERATIONS = 200
		const val ITERATIONS_BEFORE_STEP = 20
		const val ITERATIONS_BETWEEN_STEP = 20
		const val MAX_LOOSENS = 10
		const val TOLERANCE_STEP = 2f
	}

	var enabled = true
	private var chainList = mutableListOf<IKChain>()
	private var rootChain: IKChain? = null
	private var needsReset = false

	/**
	 * Any time the skeleton is rebuilt or trackers are assigned / unassigned the chains
	 * should be rebuilt.
	 */
	fun buildChains(trackers: List<Tracker>) {
		chainList.clear()

		val positionalConstraints = extractPositionalConstraints(trackers)
		val rotationalConstraints = extractRotationalConstraints(trackers)

		rootChain = chainBuilder(root, null, 0, positionalConstraints, rotationalConstraints)
		populateChainList(rootChain!!)
		addConstraints()

		// Check if there is any constraints (other than the head) in the model
		rootChain = if (neededChain(rootChain!!)) rootChain else null
		chainList.sortBy { -it.level }

		println("ChainList length ${chainList.size}")
		for (chain in chainList) {
			println("Start = ${chain.nodes.first().boneType.name}")
			println("End = ${chain.nodes.last().boneType.name}\n")
		}
	}

	/**
	 * Reset the offsets of positional trackers.
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
		var tailConstraint = getConstraint(currentBone, positionalConstraints)

		// Add bones until there is a reason to make a new chain
		while (currentBone.children.size == 1 && tailConstraint == null) {
			currentBone = currentBone.children.first()
			currentBone.rotationConstraint.allowModifications =
				getConstraint(currentBone, rotationalConstraints) == null
			boneList.add(currentBone)
			tailConstraint = getConstraint(currentBone, positionalConstraints)
		}

		var chain = IKChain(boneList, parent, level, baseConstraint, tailConstraint)

		if (currentBone.children.isNotEmpty()) {
			// Build child chains
			val childrenList = mutableListOf<IKChain>()
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
			chain = combineChains(chain, chain.children.first())
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
			childChain.tailConstraint,
		)

		newChain.children = childChain.children

		for (c in newChain.children) {
			c.parent = newChain
		}

		return newChain
	}

	private fun addConstraints() {
		fun constrainChain(chain: IKChain) {
			chain.nodes.forEach { it.rotationConstraint.allowModifications = false }
		}
		chainList.forEach { if (it.tailConstraint == null) constrainChain(it) }
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
			if (c.trackerPosition != null && bone.boneType.bodyPart == (c.trackerPosition?.bodyPart ?: 0)) {
				constraints.remove(c)
				return c
			}
		}
		return null
	}

	/**
	 * Loosen rotational constraints gradually
	 */
	private fun loosenConstraints() {
		for (chain in chainList) {
			if (chain.loosens < MAX_LOOSENS) chain.decreaseConstraints()
		}
	}

	fun solve() {
		if (rootChain == null || !enabled) return

		var solved: Boolean
		if (needsReset) {
			for (c in chainList) {
				c.resetTrackerOffsets()
			}
			needsReset = false
		}

		rootChain?.resetChain()

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

			if (i > ITERATIONS_BEFORE_STEP && i % ITERATIONS_BETWEEN_STEP == 0) {
				// Help the chains out of a deadlock
				for (chain in chainList) {
					chain.updateChildCentroidWeight()
				}
				loosenConstraints()
			}
		}

		root.update()
	}
}
