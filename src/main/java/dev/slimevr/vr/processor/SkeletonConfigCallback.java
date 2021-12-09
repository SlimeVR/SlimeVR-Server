package dev.slimevr.vr.processor;

import com.jme3.math.Vector3f;

public interface SkeletonConfigCallback {
	public void updateConfigState(SkeletonConfigValue config, float newValue);
	public void updateToggleState(SkeletonConfigToggle configToggle, boolean newValue);
	public void updateNodeOffset(SkeletonNodeOffset nodeOffset, Vector3f offset);
}
