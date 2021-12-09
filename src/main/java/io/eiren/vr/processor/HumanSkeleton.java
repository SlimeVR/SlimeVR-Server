package io.eiren.vr.processor;

import java.util.Map;

import dev.slimevr.vr.processor.SkeletonConfig;
import dev.slimevr.vr.processor.SkeletonConfigValue;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.VRServerThread;

public abstract class HumanSkeleton {

	@VRServerThread
	public abstract void updatePose();
	
	@ThreadSafe
	public abstract TransformNode getRootNode();
	
	@ThreadSafe
	public abstract SkeletonConfig getSkeletonConfig();

	@ThreadSafe
	public abstract void resetSkeletonConfig(SkeletonConfigValue config);

	@ThreadSafe
	public void resetSkeletonConfig(String config) {
		resetSkeletonConfig(SkeletonConfigValue.getByStringValue(config));
	}

	@ThreadSafe
	public void resetAllSkeletonConfigs() {
		for (SkeletonConfigValue config : SkeletonConfigValue.values) {
			resetSkeletonConfig(config);
		}
	}

	@VRServerThread
	public abstract void resetTrackersFull();
	
	@VRServerThread
	public abstract void resetTrackersYaw();
}
