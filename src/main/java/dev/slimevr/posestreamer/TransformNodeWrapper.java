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

	protected TransformNodeWrapper parent;
	public final List<TransformNodeWrapper> children = new FastList<>();

	public TransformNodeWrapper(String name, TransformNode nodeToWrap) {
		this.wrappedNode = nodeToWrap;

		this.name = name;

		this.localTransform = nodeToWrap.localTransform;
		this.worldTransform = nodeToWrap.worldTransform;
	}

	public TransformNodeWrapper(TransformNode nodeToWrap) {
		this(nodeToWrap.getName(), nodeToWrap);
	}

	public boolean hasLocalRotation () {
		return wrappedNode.localRotation;
	}

	public void attachChild(TransformNodeWrapper node) {
		this.children.add(node);
		node.parent = this;
	}

	public String getName() {
		return name;
	}
}
