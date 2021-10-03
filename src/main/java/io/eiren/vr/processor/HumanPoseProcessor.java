package io.eiren.vr.processor;

import java.util.List;
import java.util.function.Consumer;

import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.VRServerThread;
import io.eiren.util.collections.FastList;
import io.eiren.vr.VRServer;
import io.eiren.vr.trackers.HMDTracker;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerStatus;

public class HumanPoseProcessor {
	
	private final VRServer server;
	private final List<ComputedHumanPoseTracker> computedTrackers = new FastList<>();
	private final List<Consumer<HumanSkeleton>> onSkeletonUpdated = new FastList<>();
	private HumanSkeleton skeleton;

	public HumanPoseProcessor(VRServer server, HMDTracker hmd, int trackersAmount) {
		this.server = server;
		computedTrackers.add(new ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition.WAIST, TrackerBodyPosition.WAIST));
		if(trackersAmount > 2) {
			computedTrackers.add(new ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition.LEFT_FOOT, TrackerBodyPosition.LEFT_FOOT));
			computedTrackers.add(new ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition.RIGHT_FOOT, TrackerBodyPosition.RIGHT_FOOT));
			if(trackersAmount == 4 || trackersAmount >= 6) {
				computedTrackers.add(new ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition.CHEST, TrackerBodyPosition.CHEST));
			}
			if(trackersAmount >= 5) {
				computedTrackers.add(new ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition.LEFT_KNEE, TrackerBodyPosition.LEFT_ANKLE));
				computedTrackers.add(new ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition.RIGHT_KNEE, TrackerBodyPosition.RIGHT_ANKLE));
			}
		}
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
	public void setSkeletonConfig(String key, float newLength) {
		if(skeleton != null)
			skeleton.setSkeletonConfig(key, newLength);
	}

	@ThreadSafe
	public void resetSkeletonConfig(String key) {
		if(skeleton != null)
			skeleton.resetSkeletonConfig(key);
	}
	
	@ThreadSafe
	public float getSkeletonConfig(String key) {
		if(skeleton != null) {
			Number f = skeleton.getSkeletonConfig().get(key);
			if(f != null)
				return f.floatValue();
		}
		return 0.0f;
	}
	
	@ThreadSafe
	public List<? extends Tracker> getComputedTrackers() {
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
		skeleton = new HumanSkeletonWithLegs(server, computedTrackers);
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
