package dev.slimevr.tracking.processor.skeleton

import dev.slimevr.tracking.processor.Bone
import dev.slimevr.tracking.trackers.Tracker

/*
 * Implements CCDIK (Cyclic Coordinate Descent Inverse Kinematics) to allow
 * positional trackers such as vive/tundra trackers to be used in conjunction with
 * IMU trackers
 */

class IKSolver(private val root: Bone) {
	companion object {
		const val TOLERANCE_SQR = 1e-8 // == 0.01 cm
		const val MAX_ITERATIONS = 100
		const val ANNEALING_STEP = 20
		const val ANNEALING_ITERATIONS = 5
		const val ANNEALING_MAX = 60
		const val DAMPENING_FACTOR = 0.5f
		const val STATIC_DAMPENING = 0.1f
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
		var constraint = getConstraint(currentBone, rotationalConstraints)
		currentBone.rotationConstraint.allowModifications = constraint == null
		currentBone.rotationConstraint.hasTrackerRotation = constraint != null
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
			constraint = getConstraint(currentBone, rotationalConstraints)
			currentBone.rotationConstraint.allowModifications = constraint == null
			currentBone.rotationConstraint.hasTrackerRotation = constraint != null
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
		boneList.addAll(chain.bones)
		boneList.addAll(childChain.bones)

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
			chain.bones.forEach { it.rotationConstraint.allowModifications = false }
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

	private fun solve(iterations: Int, useConstraints: Boolean = true): Boolean {
		var solved: Boolean
		for (i in 0..iterations) {
			for (chain in chainList) {
				chain.backwardsCCDIK(useConstraints)
			}

			rootChain?.computeTargetDistance()

			// If all chains have reached their target the chain is solved
			solved = true
			for (chain in chainList) {
				if (chain.distToTargetSqr > TOLERANCE_SQR) {
					solved = false
				}
			}

			if (solved && useConstraints) return true
		}

		return false
	}

	fun solve() {
		if (rootChain == null || !enabled) return

		if (needsReset) {
			for (c in chainList) {
				c.resetTrackerOffsets()
			}
			needsReset = false
		}

		rootChain?.resetChain()
		root.update()

		for (i in 0 until MAX_ITERATIONS step ANNEALING_STEP) {
			solve(ANNEALING_ITERATIONS, (i > ANNEALING_MAX))
			val solved = solve(ANNEALING_STEP - ANNEALING_ITERATIONS)

			if (solved) break
		}

		root.update()
	}
}
