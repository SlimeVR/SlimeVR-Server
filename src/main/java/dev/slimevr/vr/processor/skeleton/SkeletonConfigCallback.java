package dev.slimevr.vr.processor.skeleton;

import com.jme3.math.Vector3f;


public interface SkeletonConfigCallback {

	void updateConfigState(SkeletonConfigValue config, float newValue);

	void updateToggleState(SkeletonConfigToggle configToggle, boolean newValue);

	void updateNodeOffset(BoneType nodeOffset, Vector3f offset);
}
