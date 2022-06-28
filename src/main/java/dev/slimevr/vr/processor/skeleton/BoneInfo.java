package dev.slimevr.vr.processor.skeleton;

import com.jme3.math.Quaternion;

import dev.slimevr.vr.processor.TransformNode;


/**
 * Provides an easy way to access pose information of a particular skeletal bone
 * (as opposed to trackers).
 */
public class BoneInfo {

	// TODO(thebutlah): I don't think `BoneType` should include trackers, so
	// this might make more sense to be `BodyPart` or something.
	public final BoneType boneType;
	public final TransformNode tailNode;
	public float length;

	/**
	 * Creates a `BoneInfo`.
	 *
	 * We use `tailNode` because the length of the bone comes from the tail
	 * node's local transform (offset from head), but the rotation of a bone
	 * comes from the head node's rotation.
	 */
	public BoneInfo(BoneType boneType, TransformNode tailNode) {
		this.boneType = boneType;
		this.tailNode = tailNode;
		updateLength();
	}

	/**
	 * Recomputes `BoneInfo.length`
	 */
	public void updateLength() {
		this.length = this.tailNode.localTransform.getTranslation().length();
	}

	// TODO : There shouldn't be edge cases like multiplying
	// feet by rotation. This is the best solution right now,
	// or we'd need to store this info on the client, which is
	// worse. Need to rework the skeleton using new @SkeletonData
	// system
	public Quaternion getLocalRotation() {
		var rot = this.tailNode.getParent().localTransform.getRotation();
		if (this.boneType == BoneType.LEFT_FOOT || this.boneType == BoneType.RIGHT_FOOT) {
			rot = rot.mult(Quaternion.X_90_DEG);
		}
		return rot;
	}

	public Quaternion getGlobalRotation() {
		var rot = this.tailNode.getParent().worldTransform.getRotation();
		if (this.boneType == BoneType.LEFT_FOOT || this.boneType == BoneType.RIGHT_FOOT) {
			rot = rot.mult(Quaternion.X_90_DEG);
		}
		if (this.boneType == BoneType.LEFT_LOWER_ARM || this.boneType == BoneType.RIGHT_LOWER_ARM) {
			rot = rot.mult(Quaternion.X_180_DEG);
		}
		return rot;
	}

}
