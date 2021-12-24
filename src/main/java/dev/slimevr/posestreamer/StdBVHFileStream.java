package dev.slimevr.posestreamer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;

import dev.slimevr.vr.processor.TransformNode;

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
		TransformNode newRoot = getNodeFromHierarchy(rootNode, "Hip");
		if(newRoot == null) {
			return null;
		}
		
		TransformNodeWrapper wrappedRoot = TransformNodeWrapper.wrapHierarchyDown(newRoot);
		
		/*
		// If should wrap up hierarchy
		if (newRoot.getParent() != null) {
			// Create an extra node for full proper rotation
			TransformNodeWrapper spineWrapper = new TransformNodeWrapper(new TransformNode("Spine", false), true, 1);
			wrappedRoot.attachChild(spineWrapper);
		
			// Wrap up on top of the spine node
			TransformNodeWrapper.wrapNodeHierarchyUp(newRoot, spineWrapper);
		}
		*/
		
		TransformNodeWrapper.wrapNodeHierarchyUp(wrappedRoot);
		
		return wrappedRoot;
	}
	
	private TransformNode getNodeFromHierarchy(TransformNode node, String name) {
		if(node.getName().equalsIgnoreCase(name)) {
			return node;
		}
		
		for(TransformNode child : node.children) {
			TransformNode result = getNodeFromHierarchy(child, name);
			if(result != null) {
				return result;
			}
		}
		
		return null;
	}
}
