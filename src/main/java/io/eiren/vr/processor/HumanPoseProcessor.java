package io.eiren.vr.processor;

import java.util.List;
import java.util.function.Consumer;

import dev.slimevr.vr.processor.skeleton.HumanSkeleton;
import dev.slimevr.vr.processor.skeleton.SimpleSkeleton;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.VRServerThread;
import io.eiren.util.collections.FastList;
import io.eiren.vr.VRServer;
import io.eiren.vr.trackers.HMDTracker;
import io.eiren.vr.trackers.ShareableTracker;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerRole;
import io.eiren.vr.trackers.TrackerStatus;

public class HumanPoseProcessor {
	
	private final VRServer server;
	private final List<ComputedHumanPoseTracker> computedTrackers = new FastList<>();
	private final List<Consumer<HumanSkeleton>> onSkeletonUpdated = new FastList<>();
	private HumanSkeleton skeleton;

	public HumanPoseProcessor(VRServer server, HMDTracker hmd) {
		this.server = server;
		computedTrackers.add(new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.WAIST, TrackerRole.WAIST));
		computedTrackers.add(new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.LEFT_FOOT, TrackerRole.LEFT_FOOT));
		computedTrackers.add(new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.RIGHT_FOOT, TrackerRole.RIGHT_FOOT));
		computedTrackers.add(new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.CHEST, TrackerRole.CHEST));
		computedTrackers.add(new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.LEFT_KNEE, TrackerRole.LEFT_KNEE));
		computedTrackers.add(new ComputedHumanPoseTracker(Tracker.getNextLocalTrackerId(), ComputedHumanPoseTrackerPosition.RIGHT_KNEE, TrackerRole.RIGHT_KNEE));
	}
	
	public HumanSkeleton getSkeleton() {
		return skeleton;
	}

	@VRServerThread
	public void addSkeletonUpdatedCallback(Consumer<HumanSkeleton> consumer) {
		onSkeletonUpdated.add(consumer);
		if(skeleton != null)
			consumer.accept(skeleton);
	}
	
	@ThreadSafe
	public void setSkeletonConfig(SkeletonConfigValue key, float newLength) {
		if(skeleton != null)
			skeleton.getSkeletonConfig().setConfig(key, newLength);
	}

	@ThreadSafe
	public void resetSkeletonConfig(SkeletonConfigValue key) {
		if(skeleton != null)
			skeleton.resetSkeletonConfig(key);
	}

	@ThreadSafe
	public void resetAllSkeletonConfigs() {
		if(skeleton != null)
			skeleton.resetAllSkeletonConfigs();
	}
	
	@ThreadSafe
	public float getSkeletonConfig(SkeletonConfigValue key) {
		if(skeleton != null) {
			Number f = skeleton.getSkeletonConfig().getConfig(key);
			if(f != null)
				return f.floatValue();
		}
		return 0.0f;
	}
	
	@ThreadSafe
	public List<? extends ShareableTracker> getComputedTrackers() {
		return computedTrackers;
	}

	@VRServerThread
	public void trackerAdded(Tracker tracker) {
		updateSekeltonModel();
	}

	@VRServerThread
	public void trackerUpdated(Tracker tracker) {
		updateSekeltonModel();
	}

	@VRServerThread
	private void updateSekeltonModel() {
		disconnectAllTrackers();
		skeleton = new SimpleSkeleton(server, computedTrackers);
		for(int i = 0; i < onSkeletonUpdated.size(); ++i)
			onSkeletonUpdated.get(i).accept(skeleton);
	}

	@VRServerThread
	private void disconnectAllTrackers() {
		for(int i = 0; i < computedTrackers.size(); ++i) {
			computedTrackers.get(i).setStatus(TrackerStatus.DISCONNECTED);
		}
	}

	@VRServerThread
	public void update() {
		if(skeleton != null)
			skeleton.updatePose();
	}

	@VRServerThread
	public void resetTrackers() {
		if(skeleton != null)
			skeleton.resetTrackersFull();
	}

	@VRServerThread
	public void resetTrackersYaw() {
		if(skeleton != null)
			skeleton.resetTrackersYaw();
	}
}
