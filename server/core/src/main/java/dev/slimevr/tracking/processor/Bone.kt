package dev.slimevr.tracking.processor

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Represents a bone composed of 2 joints/nodes (TransformNode)
 */
class Bone(val boneType: BoneType) {
	val headNode = TransformNode(false)
	val tailNode = TransformNode(false)
	var parent: Bone? = null
		private set
	val children: MutableList<Bone> = CopyOnWriteArrayList()

	init {
		headNode.attachChild(tailNode)
	}

	/**
	 * Attach another bone as a child of this
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
	 * Detach this bone from its parent and children
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
		headNode.attachChild(tailNode)
	}

	/**
	 * Computes the rotations and positions of
	 * this bone and all of its children.
	 */
	fun update() {
		headNode.update()
	}

	fun getGlobalRotation(): Quaternion {
		return headNode.worldTransform.rotation
	}

	fun getLocalRotation(): Quaternion {
		return headNode.localTransform.rotation
	}

	fun getGlobalPosition(): Vector3 { // TODO remove one
		return headNode.worldTransform.translation
	}

	fun getLocalPosition(): Vector3 { // TODO remove one
		return headNode.localTransform.translation
	}

	fun setGlobalRotation(rotation: Quaternion) {
		headNode.worldTransform.rotation = rotation
	}

	fun setLocalRotation(rotation: Quaternion) {
		headNode.localTransform.rotation = rotation
	}

	fun setGlobalPosition(position: Vector3) { // TODO remove one
		headNode.worldTransform.translation = position
	}

	fun setLocalPosition(position: Vector3) { // TODO remove one
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
