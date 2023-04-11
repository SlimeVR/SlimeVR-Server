package dev.slimevr.tracking.processor;

import com.jme3.math.Transform;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import io.eiren.util.ann.ThreadSafe;


public class TransformNode {

	public final Transform localTransform = new Transform();
	public final Transform worldTransform = new Transform();
	public final List<TransformNode> children = new CopyOnWriteArrayList<>();
	public boolean localRotation = false;
	protected TransformNode parent;
	protected BoneType boneType;

	public TransformNode(BoneType boneType, boolean localRotation) {
		this.boneType = boneType;
		this.localRotation = localRotation;
	}

	public TransformNode(BoneType boneType) {
		this(boneType, true);
	}

	public TransformNode() {
		this(null, true);
	}

	public void attachChild(TransformNode node) {
		if (node.parent != null) {
			throw new IllegalArgumentException("The child node must not already have a parent");
		}

		this.children.add(node);
		node.parent = this;
	}

	public TransformNode getParent() {
		return parent;
	}

	@ThreadSafe
	public void update() {
		updateWorldTransforms(); // Call update on each frame because we have
									// relatively few nodes
		for (TransformNode node : children) {
			node.update();
		}
	}

	protected synchronized void updateWorldTransforms() {
		if (parent == null) {
			worldTransform.set(localTransform);
		} else {
			worldTransform.set(localTransform);
			if (localRotation)
				worldTransform.combineWithParent(parent.worldTransform);
			else
				combineWithParentGlobalRotation(parent.worldTransform);
		}
	}

	public void depthFirstTraversal(Consumer<TransformNode> visitor) {
		for (TransformNode node : children) {
			node.depthFirstTraversal(visitor);
		}
		visitor.accept(this);
	}

	public BoneType getBoneType() {
		return boneType;
	}

	public void combineWithParentGlobalRotation(Transform parent) {
		worldTransform.getScale().multLocal(parent.getScale());
		worldTransform.getTranslation().multLocal(parent.getScale());

		parent
			.getRotation()
			.multLocal(worldTransform.getTranslation())
			.addLocal(parent.getTranslation());
	}

	public void detachWithChildren() {
		for (TransformNode child : children) {
			child.parent = null;
		}
		this.children.clear();
		if (this.parent != null) {
			this.parent.children.remove(this);
		}
		this.parent = null;
	}
}
