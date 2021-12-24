package dev.slimevr.vr.processor.skeleton;

import java.util.Map;

import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.VRServerThread;
import io.eiren.vr.processor.TransformNode;

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
