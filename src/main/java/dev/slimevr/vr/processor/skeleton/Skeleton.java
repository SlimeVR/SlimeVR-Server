package dev.slimevr.vr.processor.skeleton;

import java.util.ArrayList;
import java.util.List;

import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.vr.processor.TransformNode;
import io.eiren.util.ann.ThreadSafe;


public abstract class Skeleton {

	public final List<BoneInfo> currentBoneInfo = new ArrayList<>();

	@VRServerThread
	public abstract void updatePose();

	@ThreadSafe
	public abstract TransformNode getRootNode();

	@ThreadSafe
	public abstract TransformNode[] getAllNodes();

	@ThreadSafe
	public abstract SkeletonConfig getSkeletonConfig();

	@ThreadSafe
	public abstract void resetSkeletonConfig(SkeletonConfigOffsets config);

	@ThreadSafe
	public void resetAllSkeletonConfigs() {
		for (SkeletonConfigOffsets config : SkeletonConfigOffsets.values) {
			resetSkeletonConfig(config);
		}
	}

	@VRServerThread
	public abstract void resetTrackersFull();

	@VRServerThread
	public abstract void resetTrackersFullStepTwo();

	@VRServerThread
	public abstract void resetTrackersYaw();

	@VRServerThread
	public abstract boolean[] getLegTweaksState();

	@VRServerThread
	public abstract void setLegTweaksEnabled(boolean value);

	@VRServerThread
	public abstract void setFloorclipEnabled(boolean value);

	@VRServerThread
	public abstract void setSkatingCorrectionEnabled(boolean value);
}
