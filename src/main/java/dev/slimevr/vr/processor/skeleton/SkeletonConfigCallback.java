package dev.slimevr.vr.processor.skeleton;

import com.jme3.math.Vector3f;


public interface SkeletonConfigCallback {

	void updateOffsetsState(SkeletonConfigOffsets offset, float newValue);

	void updateTogglesState(SkeletonConfigToggles toggle, boolean newValue);

	void updateValuesState(SkeletonConfigValues value, float newValue);

	void updateNodeOffset(BoneType nodeOffset, Vector3f offset);
}
