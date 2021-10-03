package io.eiren.vr.processor;

import java.util.Map;

import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.VRServerThread;

public abstract class HumanSkeleton {

	@VRServerThread
	public abstract void updatePose();
	
	@ThreadSafe
	public abstract TransformNode getRootNode();

	@ThreadSafe
	public abstract Map<String, Float> getSkeletonConfig();

	@ThreadSafe
	public abstract void setSkeletonConfig(String key, float newLength);
	
	@ThreadSafe
	public abstract void resetSkeletonConfig(String joint);

	@VRServerThread
	public abstract void resetTrackersFull();
	
	@VRServerThread
	public abstract void resetTrackersYaw();
}
