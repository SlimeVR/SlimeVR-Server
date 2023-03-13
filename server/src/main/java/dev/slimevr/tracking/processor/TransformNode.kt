package dev.slimevr.tracking.processor

import io.eiren.util.collections.FastList
import io.github.axisangles.ktmath.Transform
import java.util.function.Consumer

class TransformNode @JvmOverloads constructor(var boneType: BoneType? = null, val localRotation: Boolean = true) {
	val localTransform = Transform()
	val worldTransform = Transform()
	val children: MutableList<TransformNode> = FastList()
	var parent: TransformNode? = null
		private set

	fun attachChild(node: TransformNode) {
		require(node.parent == null) { "The child node must not already have a parent" }
		children.add(node)
		node.parent = this
	}

	fun update() {
		// Call update on each frame because we have relatively few nodes
		updateWorldTransforms()
		for (node in children) {
			node.update()
		}
	}

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
		worldTransform.scale = worldTransform.scale cross parent.scale
		val scaledTranslation = worldTransform.translation cross parent.scale
		worldTransform.translation = (parent.rotation.toMatrix() * scaledTranslation) + parent.translation
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
