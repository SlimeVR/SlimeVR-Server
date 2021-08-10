package io.eiren.gui.autobone;

import java.util.HashMap;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.vr.processor.HumanSkeletonWithLegs;
import io.eiren.vr.processor.TransformNode;

public final class PoseFrame {

	public final Vector3f rootPos;
	public final HashMap<String, Quaternion> rotations;

	public PoseFrame(Vector3f rootPos, HashMap<String, Quaternion> rotations) {
		this.rootPos = rootPos;
		this.rotations = rotations;
	}

	public PoseFrame(HumanSkeletonWithLegs skeleton) {
		// Copy headset position
		TransformNode rootNode = skeleton.getRootNode();
		this.rootPos = new Vector3f(rootNode.localTransform.getTranslation());

		// Copy all rotations
		this.rotations = new HashMap<String, Quaternion>();
		rootNode.depthFirstTraversal(visitor -> {
			// Insert a copied quaternion so it isn't changed by reference
			rotations.put(visitor.getName(), new Quaternion(visitor.localTransform.getRotation()));
		});
	}
}
