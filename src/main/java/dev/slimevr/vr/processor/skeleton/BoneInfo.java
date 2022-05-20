package dev.slimevr.vr.processor.skeleton;

import com.jme3.math.Transform;

import dev.slimevr.vr.processor.TransformNode;

public class BoneInfo {
	
	public final BoneType boneType;
	public final Transform globalTransform = new Transform();
	public float length;
	
	public BoneInfo(BoneType boneType) {
		this.boneType = boneType;
	}
	
	public BoneInfo set(Transform globalTransform, float length) {
		this.globalTransform.set(globalTransform);
		this.length = length;
		return this;
	}
	
	public BoneInfo set(TransformNode node) {
		return set(node.worldTransform, node.localTransform.getTranslation().length());
	}
}
