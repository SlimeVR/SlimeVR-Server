package dev.slimevr.posestreamer;

import java.util.List;

import com.jme3.math.Transform;

import io.eiren.util.collections.FastList;
import io.eiren.vr.processor.TransformNode;

public class TransformNodeWrapper {

	public final TransformNode wrappedNode;

	protected String name;

	public final Transform localTransform;
	public final Transform worldTransform;

	public final boolean flippedOffset;

	protected TransformNodeWrapper parent;
	public final List<TransformNodeWrapper> children = new FastList<>();

	public TransformNodeWrapper(TransformNode nodeToWrap, String name, boolean flippedOffset) {
		this.wrappedNode = nodeToWrap;

		this.name = name;

		this.localTransform = nodeToWrap.localTransform;
		this.worldTransform = nodeToWrap.worldTransform;

		this.flippedOffset = flippedOffset;
	}

	public TransformNodeWrapper(TransformNode nodeToWrap, String name) {
		this(nodeToWrap, name, false);
	}

	public TransformNodeWrapper(TransformNode nodeToWrap, boolean flippedOffset) {
		this(nodeToWrap, nodeToWrap.getName(), flippedOffset);
	}

	public TransformNodeWrapper(TransformNode nodeToWrap) {
		this(nodeToWrap, nodeToWrap.getName());
	}

	public boolean hasLocalRotation () {
		return wrappedNode.localRotation;
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

	public String getName() {
		return name;
	}
}
