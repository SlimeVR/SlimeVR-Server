package io.eiren.vr.processor;

import java.util.List;
import java.util.function.Consumer;

import com.jme3.math.Transform;

import io.eiren.util.collections.FastList;

public class TransformNode {
	
	public final Transform localTransform = new Transform();
	public final Transform worldTransform = new Transform();
	public final List<TransformNode> children = new FastList<>();
	public boolean localRotation = false;
	protected TransformNode parent;
	protected String name;
	
	public TransformNode(String name, boolean localRotation) {
		this.name = name;
		this.localRotation = localRotation;
	}
	
	public TransformNode(String name) {
		this(name, true);
	}
	
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
				combineWithParentGlobalRotation(parent.worldTransform);
		}
	}
	
	public void depthFirstTraversal(Consumer<TransformNode> visitor) {
		for(int i = 0; i < children.size(); ++i) {
			children.get(i).depthFirstTraversal(visitor);
		}
		visitor.accept(this);
	}
	
	public String getName() {
		return name;
	}

    public void combineWithParentGlobalRotation(Transform parent) {
    	worldTransform.getScale().multLocal(parent.getScale());
    	worldTransform.getTranslation().multLocal(parent.getScale());
        
        parent
            .getRotation()
            .multLocal(worldTransform.getTranslation())
            .addLocal(parent.getTranslation());
    }
}
