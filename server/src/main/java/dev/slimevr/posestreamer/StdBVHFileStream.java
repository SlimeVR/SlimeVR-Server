package dev.slimevr.posestreamer;

import dev.slimevr.tracking.processor.BoneType;
import dev.slimevr.tracking.processor.TransformNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;


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
		TransformNode newRoot = getNodeFromHierarchy(rootNode, BoneType.HIP.name());
		if (newRoot == null) {
			return null;
		}

		return TransformNodeWrapper.wrapFullHierarchy(newRoot);
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
