package dev.slimevr.vr.processor.skeleton;

import dev.slimevr.vr.processor.TransformNode;


public class BoneInfo {

	public final BoneType boneType;
	public final TransformNode node;
	public float length;

	public BoneInfo(BoneType boneType, TransformNode node) {
		this.boneType = boneType;
		this.node = node;
		updateLength();
	}

	public void updateLength() {
		this.length = node.localTransform.getTranslation().length();
	}
}
