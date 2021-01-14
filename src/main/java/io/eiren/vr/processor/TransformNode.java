package io.eiren.vr.processor;

import java.util.List;

import com.jme3.math.Transform;

import essentia.util.collections.FastList;

public class TransformNode {
	
	public final Transform localTransform = new Transform();
	public final Transform worldTransform = new Transform();
	public final List<TransformNode> children = new FastList<>();
	public boolean localRotation = false;
	private TransformNode parent;
	
	public void attachChild(TransformNode node) {
		this.children.add(node);
		node.parent = this;
	}
	
	public void update() {
		updateWorldTransforms(); // Call update on each frame because we have relatively few nodes
		for(int i = 0; i < children.size(); ++i)
			children.get(i).update();
	}
	
	protected synchronized void updateWorldTransforms() {
		if(parent == null) {
			worldTransform.set(localTransform);
		} else {
			worldTransform.set(localTransform);
			if(localRotation)
				worldTransform.combineWithParent(parent.worldTransform);
			else
				worldTransform.combineWithParentGlobalRotation(localTransform);
		}
	}
}
