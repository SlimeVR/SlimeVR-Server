package dev.slimevr.vr.processor.skeleton;

import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.vr.processor.TransformNode;
import io.eiren.util.ann.ThreadSafe;


public abstract class Skeleton {

	@VRServerThread
	public abstract void updatePose();

	@ThreadSafe
	public abstract TransformNode getRootNode();

	@ThreadSafe
	public abstract TransformNode[] getAllNodes();

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
