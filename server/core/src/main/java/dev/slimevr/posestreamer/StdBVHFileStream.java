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
		TransformNode newRoot = getNodeFromHierarchy(rootNode, BoneType.HIP);
		if (newRoot == null) {
			return null;
		}

		return TransformNodeWrapper.wrapFullHierarchy(newRoot);
	}

	private TransformNode getNodeFromHierarchy(TransformNode node, BoneType boneType) {
		// if (node.getBoneType() == boneType) {
		// return node;
		// } TODO omg

		for (TransformNode child : node.getChildren()) {
			TransformNode result = getNodeFromHierarchy(child, boneType);
			if (result != null) {
				return result;
			}
		}

		return null;
	}
}
