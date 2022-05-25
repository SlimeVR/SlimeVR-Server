package dev.slimevr.vr.processor;

import dev.slimevr.VRServer;
import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.vr.processor.skeleton.Skeleton;
import dev.slimevr.vr.processor.skeleton.HumanSkeleton;
import dev.slimevr.vr.processor.skeleton.SkeletonConfig;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;
import dev.slimevr.vr.trackers.*;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.collections.FastList;

import java.util.List;
import java.util.function.Consumer;


public class HumanPoseProcessor {

	private final VRServer server;
	private final List<ComputedHumanPoseTracker> computedTrackers = new FastList<>();
	private final List<Consumer<Skeleton>> onSkeletonUpdated = new FastList<>();
	private Skeleton skeleton;

	public HumanPoseProcessor(VRServer server, HMDTracker hmd) {
		this.server = server;
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.WAIST,
					TrackerRole.WAIST
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.LEFT_FOOT,
					TrackerRole.LEFT_FOOT
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.RIGHT_FOOT,
					TrackerRole.RIGHT_FOOT
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.CHEST,
					TrackerRole.CHEST
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.LEFT_KNEE,
					TrackerRole.LEFT_KNEE
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.RIGHT_KNEE,
					TrackerRole.RIGHT_KNEE
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.LEFT_ELBOW,
					TrackerRole.LEFT_ELBOW
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.RIGHT_ELBOW,
					TrackerRole.RIGHT_ELBOW
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.LEFT_HAND,
					TrackerRole.LEFT_HAND
				)
			);
		computedTrackers
			.add(
				new ComputedHumanPoseTracker(
					Tracker.getNextLocalTrackerId(),
					ComputedHumanPoseTrackerPosition.RIGHT_HAND,
					TrackerRole.RIGHT_HAND
				)
			);
	}

	public Skeleton getSkeleton() {
		return skeleton;
	}

	@VRServerThread
	public void addSkeletonUpdatedCallback(Consumer<Skeleton> consumer) {
		onSkeletonUpdated.add(consumer);
		if (skeleton != null)
			consumer.accept(skeleton);
	}

	@ThreadSafe
	public void setSkeletonConfig(SkeletonConfigValue key, float newLength) {
		if (skeleton != null)
			skeleton.getSkeletonConfig().setConfig(key, newLength);
	}

	@ThreadSafe
	public void resetSkeletonConfig(SkeletonConfigValue key) {
		if (skeleton != null)
			skeleton.resetSkeletonConfig(key);
	}

	@ThreadSafe
	public void resetAllSkeletonConfigs() {
		if (skeleton != null)
			skeleton.resetAllSkeletonConfigs();
	}

	@ThreadSafe
	public SkeletonConfig getSkeletonConfig() {
		return skeleton.getSkeletonConfig();
	}

	@ThreadSafe
	public float getSkeletonConfig(SkeletonConfigValue key) {
		if (skeleton != null) {
			return skeleton.getSkeletonConfig().getConfig(key);
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
		skeleton = new HumanSkeleton(server, computedTrackers);
		for (Consumer<Skeleton> sc : onSkeletonUpdated)
			sc.accept(skeleton);
	}

	@VRServerThread
	private void disconnectAllTrackers() {
		for (ComputedHumanPoseTracker t : computedTrackers) {
			t.setStatus(TrackerStatus.DISCONNECTED);
		}
	}

	@VRServerThread
	public void update() {
		if (skeleton != null)
			skeleton.updatePose();
	}

	@VRServerThread
	public void resetTrackers() {
		if (skeleton != null)
			skeleton.resetTrackersFull();
	}

	@VRServerThread
	public void resetTrackersYaw() {
		if (skeleton != null)
			skeleton.resetTrackersYaw();
	}
}
