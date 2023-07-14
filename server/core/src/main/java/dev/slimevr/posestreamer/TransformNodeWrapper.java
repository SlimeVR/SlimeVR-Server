package dev.slimevr.posestreamer;

import dev.slimevr.tracking.processor.BoneType;
import dev.slimevr.tracking.processor.TransformNode;
import io.eiren.util.collections.FastList;
import io.github.axisangles.ktmath.Quaternion;
import io.github.axisangles.ktmath.Transform;

import java.util.List;


public class TransformNodeWrapper {

	public final TransformNode wrappedNode;
	public final Transform localTransform;
	public final Transform worldTransform;
	public final List<TransformNodeWrapper> children;
	protected BoneType boneType;
	protected TransformNodeWrapper parent;
	private boolean reversedHierarchy = false;

	public TransformNodeWrapper(
		TransformNode nodeToWrap,
		BoneType boneType,
		boolean reversedHierarchy,
		int initialChildCapacity
	) {
		this.wrappedNode = nodeToWrap;

		this.boneType = boneType;

		this.localTransform = nodeToWrap.getLocalTransform();
		this.worldTransform = nodeToWrap.getWorldTransform();

		this.reversedHierarchy = reversedHierarchy;

		this.children = new FastList<>(initialChildCapacity);
	}

	public TransformNodeWrapper(
		TransformNode nodeToWrap,
		BoneType boneType,
		int initialChildCapacity
	) {
		this(nodeToWrap, boneType, false, initialChildCapacity);
	}

	public TransformNodeWrapper(TransformNode nodeToWrap, BoneType boneType) {
		this(nodeToWrap, boneType, false, 5);
	}

	public TransformNodeWrapper(
		TransformNode nodeToWrap,
		boolean reversedHierarchy,
		int initialChildCapacity
	) {
		this(nodeToWrap, nodeToWrap.getBoneType(), reversedHierarchy, initialChildCapacity);
	}

	public TransformNodeWrapper(TransformNode nodeToWrap, boolean reversedHierarchy) {
		this(nodeToWrap, nodeToWrap.getBoneType(), reversedHierarchy, 5);
	}

	public TransformNodeWrapper(TransformNode nodeToWrap, int initialChildCapacity) {
		this(nodeToWrap, nodeToWrap.getBoneType(), initialChildCapacity);
	}

	public TransformNodeWrapper(TransformNode nodeToWrap) {
		this(nodeToWrap, nodeToWrap.getBoneType());
	}

	public static TransformNodeWrapper wrapFullHierarchyWithFakeRoot(TransformNode root) {
		// Allocate a "fake" root with appropriate size depending on connections
		// the
		// root has
		TransformNodeWrapper fakeRoot = new TransformNodeWrapper(
			root,
			root.getParent() != null ? 2 : 1
		);

		// Attach downwards hierarchy to the fake root
		wrapNodeHierarchyDown(root, fakeRoot);

		// Attach upwards hierarchy to the fake root
		fakeRoot.attachChild(wrapHierarchyUp(root));

		return fakeRoot;
	}

	public static TransformNodeWrapper wrapFullHierarchy(TransformNode root) {
		return wrapNodeHierarchyUp(wrapHierarchyDown(root));
	}

	public static TransformNodeWrapper wrapHierarchyDown(TransformNode root) {
		return wrapNodeHierarchyDown(
			root,
			new TransformNodeWrapper(root, root.getChildren().size())
		);
	}

	public static TransformNodeWrapper wrapNodeHierarchyDown(
		TransformNode root,
		TransformNodeWrapper target
	) {
		for (TransformNode child : root.getChildren()) {
			target.attachChild(wrapHierarchyDown(child));
		}

		return target;
	}

	public static TransformNodeWrapper wrapHierarchyUp(TransformNode root) {
		return wrapNodeHierarchyUp(
			new TransformNodeWrapper(root, true, root.getParent() != null ? 1 : 0)
		);
	}

	public static TransformNodeWrapper wrapNodeHierarchyUp(TransformNodeWrapper root) {
		return wrapNodeHierarchyUp(root.wrappedNode, root);
	}

	public static TransformNodeWrapper wrapNodeHierarchyUp(
		TransformNode root,
		TransformNodeWrapper target
	) {
		TransformNode parent = root.getParent();
		if (parent == null) {
			return target;
		}

		// Flip the offset for these reversed nodes
		TransformNodeWrapper wrapper = new TransformNodeWrapper(
			parent,
			true,
			(parent.getParent() != null ? 1 : 0) + Math.max(0, parent.getChildren().size() - 1)
		);
		target.attachChild(wrapper);

		// Re-attach other children
		if (parent.getChildren().size() > 1) {
			for (TransformNode child : parent.getChildren()) {
				// Skip the original node
				if (child == target.wrappedNode) {
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
		return wrappedNode.getLocalRotation();
	}

	public Quaternion calculateLocalRotation(Quaternion relativeTo) {
		return calculateLocalRotationInverse(relativeTo.inv());
	}

	public Quaternion calculateLocalRotationInverse(
		Quaternion inverseRelativeTo
	) {
		return inverseRelativeTo.times(worldTransform.getRotation());
	}

	public void attachChild(TransformNodeWrapper node) {
		if (node.parent != null) {
			throw new IllegalArgumentException("The child node must not already have a parent");
		}

		this.children.add(node);
		node.parent = this;
	}

	public TransformNodeWrapper getParent() {
		return parent;
	}

	public BoneType getBoneType() {
		return boneType;
	}

	public void setBoneType(BoneType boneType) {
		this.boneType = boneType;
	}
}
