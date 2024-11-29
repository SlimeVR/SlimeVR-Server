package dev.slimevr.tracking.processor

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import java.util.concurrent.CopyOnWriteArrayList
import dev.slimevr.tracking.processor.Constraint.Companion.ConstraintType
import dev.slimevr.tracking.trackers.Tracker

/**
 * Represents a bone composed of 2 joints: headNode and tailNode.
 */
class Bone(val boneType: BoneType, val rotationConstraint: Constraint) {
	private val headNode = TransformNode(true)
	private val tailNode = TransformNode(false)
	var parent: Bone? = null
		private set
	val children: MutableList<Bone> = CopyOnWriteArrayList()
	var rotationOffset = Quaternion.IDENTITY
	var attachedTracker: Tracker? = null

	init {
		headNode.attachChild(tailNode)
	}

	/**
	 * Attach another bone as a child of this.
	 */
	fun attachChild(bone: Bone) {
		// Attach bone
		require(bone.parent == null) { "The child bone must not already have a parent." }
		children.add(bone)
		bone.parent = this

		// Attach node
		tailNode.attachChild(bone.headNode)
	}

	/**
	 * Detach this bone from its parent and children.
	 */
	fun detachWithChildren() {
		// Detach bones
		for (child in children) child.parent = null
		children.clear()
		parent?.children?.remove(this)
		parent = null

		// Detach nodes
		headNode.detachWithChildren()
		tailNode.detachWithChildren()

		// Reattach this bone
		headNode.attachChild(tailNode)
	}

	/**
	 * Computes the rotations and positions of
	 * this bone and all of its children.
	 */
	fun update() {
		headNode.update()
	}

	/**
	 * Computes the rotations and positions of
	 * this bone and all of its children while
	 * enforcing rotation constraints.
	 */
	fun updateWithConstraints() {
		val initialRot = getGlobalRotation()
		val newRot = rotationConstraint.applyConstraint(initialRot, this)
		setRotationRaw(newRot)
		updateThisNode()

		// Correct tracker if applicable.
		if (rotationConstraint.constraintType != ConstraintType.HINGE &&
			rotationConstraint.constraintType != ConstraintType.LOOSE_HINGE) {
			val deltaRot = newRot * initialRot.inv()
			val angle = deltaRot.angleR()

			if (angle > 0.01f &&
				(attachedTracker?.filteringHandler?.getFilteringImpact() ?: 1f) < 0.01f &&
				(parent?.attachedTracker?.filteringHandler?.getFilteringImpact() ?: 0f) < 0.01f) {
				attachedTracker?.resetsHandler?.updateConstraintFix(deltaRot)
			}
		}

		// Recursively apply constraints and update children.
		for (child in children) {
			child.updateWithConstraints()
		}
	}

	/**
	 * Computes the rotations and positions of this bone.
	 * Only to be used while traversing bones from top to bottom.
	 */
	private fun updateThisNode() {
		headNode.updateThisNode()
		tailNode.updateThisNode()
	}

	/**
	 * Returns the world-aligned rotation of the bone
	 */
	fun getGlobalRotation(): Quaternion = headNode.worldTransform.rotation

	/**
	 * Returns the rotation of the bone relative to its parent
	 */
	fun getLocalRotation(): Quaternion = headNode.localTransform.rotation

	/**
	 * Sets the global rotation of the bone
	 */
	fun setRotation(rotation: Quaternion) {
		headNode.localTransform.rotation = rotation * rotationOffset
	}

	/**
	 * Sets the global rotation of the bone directly
	 */
	fun setRotationRaw(rotation: Quaternion) {
		headNode.localTransform.rotation = rotation
	}

	/**
	 * Returns the global position of the head of the bone
	 */
	fun getPosition(): Vector3 = headNode.worldTransform.translation

	/**
	 * Returns the global position of the tail of the bone
	 */
	fun getTailPosition(): Vector3 = tailNode.worldTransform.translation

	/**
	 * Sets the global position of the head of the bone.
	 * Note: cannot set the global position of bones with parents,
	 * consider changing the bone's length instead.
	 */
	fun setPosition(position: Vector3) {
		require(parent == null) { "Cannot set the position of a child bone." }
		headNode.localTransform.translation = position
	}

	/**
	 * The length of the bone is in meters.
	 * This is the local translation of the tail node.
	 * This is also the difference between the head position and the tail position.
	 */
	var length: Float
		get() = tailNode.localTransform.translation.len()
		set(len) = updateLength(len)

	private fun updateLength(length: Float) {
		tailNode.localTransform.translation = Vector3(0f, -length, 0f)
	}
}
