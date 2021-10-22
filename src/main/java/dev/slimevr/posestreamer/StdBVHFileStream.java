package dev.slimevr.posestreamer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;

import io.eiren.vr.processor.TransformNode;

public class StdBVHFileStream extends BVHFileStream {

	public StdBVHFileStream(OutputStream outputStream) {
		super(outputStream);
	}

	public StdBVHFileStream(File file) throws FileNotFoundException {
		super(file);
	}

	public StdBVHFileStream(String file) throws FileNotFoundException {
		super(file);
	}
	
	@Override
	protected TransformNodeWrapper wrapSkeletonNodes(TransformNode rootNode) {
		TransformNode waistNode = getNodeFromHierarchy(rootNode, "Waist");

		TransformNodeWrapper waistWrapper = wrapNodeHierarchy(waistNode);

		addReverseWrappedNodeHierarchy(waistWrapper);

		return waistWrapper;
	}

	protected void addReverseWrappedNodeHierarchy(TransformNodeWrapper node) {
		TransformNode parent = node.wrappedNode.getParent();
		if (parent == null) {
			return;
		}

		// Flip the offset for these reversed nodes
		TransformNodeWrapper wrapper = new TransformNodeWrapper(parent, true);
		node.attachChild(wrapper);

		// Re-attach other children
		if (parent.children.size() > 1) {
			for (TransformNode child : parent.children) {
				// Skip the original node
				if (child == node.wrappedNode) {
					continue;
				}

				wrapper.attachChild(wrapNodeHierarchy(child));
			}
		}

		// Continue up the hierarchy
		addReverseWrappedNodeHierarchy(wrapper);
	}

	@Override
	protected TransformNodeWrapper wrapNodeHierarchy(TransformNode node) {
		TransformNodeWrapper wrapper = new TransformNodeWrapper(node);

		for (TransformNode child : node.children) {
			wrapper.attachChild(wrapNodeHierarchy(child));
		}

		return wrapper;
	}

	private TransformNode getNodeFromHierarchy(TransformNode node, String name) {
		if (node.getName().equalsIgnoreCase(name)) {
			return node;
		}

		for (TransformNode child : node.children) {
			TransformNode result = getNodeFromHierarchy(child, name);
			if (result != null) {
				return result;
			}
		}

		return null;
	}
}
