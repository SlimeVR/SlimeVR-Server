package dev.slimevr.tracking.processor;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


/**
 * Provides an easy way to access pose information of a particular skeletal bone
 * (as opposed to trackers).
 */
public class BoneInfo {
	public final BoneType boneType;
	public final TransformNode headNode;
	public final TransformNode tailNode;
	public float length;
	private static final Quaternion FOOT_OFFSET = Quaternion.X_90_DEG;

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
		length = tailNode.localTransform.getTranslation().length();
	}

	public Vector3f getGlobalTranslation() {
		return tailNode.worldTransform.getTranslation();
	}

	public Vector3f getLocalTranslation() {
		return tailNode.localTransform.getTranslation();
	}

	public Quaternion getGlobalRotation() {
		return getAdjustedRotation(headNode.worldTransform.getRotation());
	}

	public Quaternion getLocalRotation() {
		return getAdjustedRotation(headNode.localTransform.getRotation());
	}

	// TODO : There shouldn't be edge cases like multiplying
	// feet by rotation. This is the best solution right now,
	// or we'd need to store this info on the client, which is
	// worse. Need to rework the way the sussy offsets work
	private Quaternion getAdjustedRotation(Quaternion rot) {
		// Offset feet 90 degrees to satisfy the SteamVR bone overlay
		if (boneType == BoneType.LEFT_FOOT || boneType == BoneType.RIGHT_FOOT) {
			rot.multLocal(FOOT_OFFSET);
		}
		return rot;
	}
}
