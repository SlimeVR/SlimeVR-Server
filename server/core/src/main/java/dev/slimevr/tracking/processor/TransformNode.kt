package dev.slimevr.tracking.processor

import io.eiren.util.ann.ThreadSafe
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Transform
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

class TransformNode @JvmOverloads constructor(
	var boneType: BoneType? = null,
	val localRotation: Boolean = true,
) {
	val localTransform = Transform()
	val worldTransform = Transform()
	val children: MutableList<TransformNode> = CopyOnWriteArrayList()
	var parent: TransformNode? = null
		private set
	var rotationOffset = Quaternion.IDENTITY

	fun attachChild(node: TransformNode) {
		require(node.parent == null) { "The child node must not already have a parent" }
		children.add(node)
		node.parent = this
	}

	@ThreadSafe
	fun update() {
		// Apply rotation offset
		localTransform.rotation *= rotationOffset

		// Call update on each frame because we have relatively few nodes
		updateWorldTransforms()
		for (node in children) {
			node.update()
		}
	}

	@Synchronized
	private fun updateWorldTransforms() {
		worldTransform.set(localTransform)
		parent?.let {
			if (localRotation) {
				worldTransform.combineWithParent(it.worldTransform)
			} else {
				combineWithParentGlobalRotation(it.worldTransform)
			}
		}
	}

	fun depthFirstTraversal(visitor: Consumer<TransformNode?>) {
		for (node in children) {
			node.depthFirstTraversal(visitor)
		}
		visitor.accept(this)
	}

	fun combineWithParentGlobalRotation(parent: Transform) {
		worldTransform.scale = worldTransform.scale hadamard parent.scale
		val scaledTranslation = worldTransform.translation hadamard parent.scale
		worldTransform.translation = parent.rotation.sandwich(scaledTranslation) + parent.translation
	}

	fun detachWithChildren() {
		for (child in children) {
			child.parent = null
		}
		children.clear()
		parent?.children?.remove(this)
		parent = null
	}
}
