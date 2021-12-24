package dev.slimevr.posestreamer;

import java.util.List;

import com.jme3.math.Quaternion;
import com.jme3.math.Transform;

import dev.slimevr.vr.processor.TransformNode;
import io.eiren.util.collections.FastList;

public class TransformNodeWrapper {
	
	public final TransformNode wrappedNode;
	
	protected String name;
	
	public final Transform localTransform;
	public final Transform worldTransform;
	
	private boolean reversedHierarchy = false;
	
	protected TransformNodeWrapper parent;
	public final List<TransformNodeWrapper> children;
	
	public TransformNodeWrapper(TransformNode nodeToWrap, String name, boolean reversedHierarchy, int initialChildCapacity) {
		this.wrappedNode = nodeToWrap;
		
		this.name = name;
		
		this.localTransform = nodeToWrap.localTransform;
		this.worldTransform = nodeToWrap.worldTransform;
		
		this.reversedHierarchy = reversedHierarchy;
		
		this.children = new FastList<>(initialChildCapacity);
	}
	
	public TransformNodeWrapper(TransformNode nodeToWrap, String name, int initialChildCapacity) {
		this(nodeToWrap, name, false, initialChildCapacity);
	}
	
	public TransformNodeWrapper(TransformNode nodeToWrap, String name) {
		this(nodeToWrap, name, false, 5);
	}
	
	public TransformNodeWrapper(TransformNode nodeToWrap, boolean reversedHierarchy, int initialChildCapacity) {
		this(nodeToWrap, nodeToWrap.getName(), reversedHierarchy, initialChildCapacity);
	}
	
	public TransformNodeWrapper(TransformNode nodeToWrap, boolean reversedHierarchy) {
		this(nodeToWrap, nodeToWrap.getName(), reversedHierarchy, 5);
	}
	
	public TransformNodeWrapper(TransformNode nodeToWrap, int initialChildCapacity) {
		this(nodeToWrap, nodeToWrap.getName(), initialChildCapacity);
	}
	
	public TransformNodeWrapper(TransformNode nodeToWrap) {
		this(nodeToWrap, nodeToWrap.getName());
	}
	
	public static TransformNodeWrapper wrapFullHierarchy(TransformNode root) {
		return wrapNodeHierarchyUp(wrapHierarchyDown(root));
	}
	
	public static TransformNodeWrapper wrapHierarchyDown(TransformNode root) {
		return wrapNodeHierarchyDown(new TransformNodeWrapper(root, root.children.size()));
	}
	
	public static TransformNodeWrapper wrapNodeHierarchyDown(TransformNodeWrapper root) {
		for(TransformNode child : root.wrappedNode.children) {
			root.attachChild(wrapHierarchyDown(child));
		}
		
		return root;
	}
	
	public static TransformNodeWrapper wrapHierarchyUp(TransformNode root) {
		return wrapNodeHierarchyUp(new TransformNodeWrapper(root, root.getParent() != null ? 1 : 0));
	}
	
	public static TransformNodeWrapper wrapNodeHierarchyUp(TransformNodeWrapper root) {
		return wrapNodeHierarchyUp(root.wrappedNode, root);
	}
	
	public static TransformNodeWrapper wrapNodeHierarchyUp(TransformNode root, TransformNodeWrapper target) {
		TransformNode parent = root.getParent();
		if(parent == null) {
			return target;
		}
		
		// Flip the offset for these reversed nodes
		TransformNodeWrapper wrapper = new TransformNodeWrapper(parent, true, (parent.getParent() != null ? 1 : 0) + Math.max(0, parent.children.size() - 1));
		target.attachChild(wrapper);
		
		// Re-attach other children
		if(parent.children.size() > 1) {
			for(TransformNode child : parent.children) {
				// Skip the original node
				if(child == target.wrappedNode) {
					continue;
				}
				
				wrapper.attachChild(wrapHierarchyDown(child));
			}
		}
		
		// Continue up the hierarchy
		wrapNodeHierarchyUp(wrapper);
		// Return original node
		return target;
	}
	
	public boolean hasReversedHierarchy() {
		return reversedHierarchy;
	}
	
	public void setReversedHierarchy(boolean reversedHierarchy) {
		this.reversedHierarchy = reversedHierarchy;
	}
	
	public boolean hasLocalRotation() {
		return wrappedNode.localRotation;
	}
	
	public Quaternion calculateLocalRotation(Quaternion relativeTo, Quaternion result) {
		return calculateLocalRotationInverse(relativeTo.inverse(), result);
	}
	
	public Quaternion calculateLocalRotationInverse(Quaternion inverseRelativeTo, Quaternion result) {
		if(result == null) {
			result = new Quaternion();
		}
		
		return worldTransform.getRotation().mult(inverseRelativeTo, result);
	}
	
	public void attachChild(TransformNodeWrapper node) {
		if(node.parent != null) {
			throw new IllegalArgumentException("The child node must not already have a parent");
		}
		
		this.children.add(node);
		node.parent = this;
	}
	
	public TransformNodeWrapper getParent() {
		return parent;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
