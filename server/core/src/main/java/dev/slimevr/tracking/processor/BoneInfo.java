package dev.slimevr.tracking.processor;

import io.github.axisangles.ktmath.Quaternion;
import io.github.axisangles.ktmath.Vector3;


/**
 * Provides an easy way to access pose information of a particular skeletal bone
 * (as opposed to trackers).
 */
public class BoneInfo {
	public final BoneType boneType;
	public final TransformNode headNode;
	public final TransformNode tailNode;
	public float length;

	/**
	 * Creates a `BoneInfo`. We use `tailNode` because the length of the bone
	 * comes from the tail node's local transform (offset from head), but the
	 * rotation of a bone comes from the head node's rotation.
	 */
	public BoneInfo(BoneType boneType, TransformNode tailNode) {
		this.boneType = boneType;
		this.tailNode = tailNode;

		if (tailNode.getParent() != null)
			this.headNode = tailNode.getParent();
		else
			this.headNode = tailNode;

		updateLength();
	}

	/**
	 * Recomputes `BoneInfo.length`
	 */
	public void updateLength() {
		length = tailNode.getLocalTransform().getTranslation().len();
	}

	public Vector3 getGlobalTranslation() {
		return tailNode.getWorldTransform().getTranslation();
	}

	public Vector3 getLocalTranslation() {
		return tailNode.getLocalTransform().getTranslation();
	}

	public Quaternion getGlobalRotation() {
		return headNode.getWorldTransform().getRotation();
	}

	public Quaternion getLocalRotation() {
		return headNode.getLocalTransform().getRotation();
	}

}
