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
		return TransformNodeWrapper.wrapFullHierarchy(getNodeFromHierarchy(rootNode, "Waist"));
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
