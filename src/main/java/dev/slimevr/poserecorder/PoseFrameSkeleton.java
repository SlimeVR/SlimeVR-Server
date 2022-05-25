package dev.slimevr.poserecorder;

import dev.slimevr.VRServer;
import dev.slimevr.vr.processor.ComputedHumanPoseTracker;
import dev.slimevr.vr.processor.skeleton.HumanSkeleton;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;
import dev.slimevr.vr.trackers.Tracker;

import java.util.List;
import java.util.Map;


public class PoseFrameSkeleton extends HumanSkeleton {

	private int frameCursor = 0;

	protected PoseFrameSkeleton(List<? extends ComputedHumanPoseTracker> computedTrackers) {
		super(computedTrackers);
	}

	public PoseFrameSkeleton(
		VRServer server,
		List<? extends ComputedHumanPoseTracker> computedTrackers
	) {
		super(server, computedTrackers);
	}

	public PoseFrameSkeleton(
		List<? extends Tracker> trackers,
		List<? extends ComputedHumanPoseTracker> computedTrackers
	) {
		super(trackers, computedTrackers);
	}

	public PoseFrameSkeleton(
		List<? extends Tracker> trackers,
		List<? extends ComputedHumanPoseTracker> computedTrackers,
		Map<SkeletonConfigValue, Float> configs,
		Map<SkeletonConfigValue, Float> altConfigs
	) {
		super(trackers, computedTrackers, configs, altConfigs);
	}

	public PoseFrameSkeleton(
		List<? extends Tracker> trackers,
		List<? extends ComputedHumanPoseTracker> computedTrackers,
		Map<SkeletonConfigValue, Float> configs
	) {
		super(trackers, computedTrackers, configs);
	}

	private int limitCursor() {
		if (frameCursor < 0) {
			frameCursor = 0;
		}

		return frameCursor;
	}

	public int setCursor(int index) {
		frameCursor = index;
		return limitCursor();
	}

	public int incrementCursor(int increment) {
		frameCursor += increment;
		return limitCursor();
	}

	public int incrementCursor() {
		return incrementCursor(1);
	}

	public int getCursor() {
		return frameCursor;
	}

	// Get tracker for specific frame
	@Override
	protected Tracker trackerPreUpdate(Tracker tracker) {
		if (tracker instanceof PoseFrameTracker) {
			// Return frame if available, otherwise return the original tracker
			TrackerFrame frame = ((PoseFrameTracker) tracker).safeGetFrame(frameCursor);
			return frame == null ? tracker : frame;
		}
		return tracker;
	}
}
