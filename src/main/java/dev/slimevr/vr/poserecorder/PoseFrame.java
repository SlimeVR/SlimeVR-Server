package dev.slimevr.vr.poserecorder;

import java.util.List;

import io.eiren.util.collections.FastList;
import io.eiren.vr.processor.ComputedHumanPoseTracker;
import io.eiren.vr.processor.TrackerBodyPosition;
import io.eiren.vr.trackers.ComputedTracker;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerStatus;
import io.eiren.vr.trackers.TrackerUtils;

public final class PoseFrame {

	public final List<TrackerFrame> trackerFrames;

	public PoseFrame(List<TrackerFrame> trackerFrames) {
		this.trackerFrames = trackerFrames;
	}

	// Ignore computed trackers by default
	public static PoseFrame fromTrackers(List<Tracker> trackers) {
		return fromTrackers(trackers, false);
	}

	public static PoseFrame fromTrackers(List<Tracker> trackers, boolean includeComputed) {
		if (trackers == null || trackers.isEmpty()) {
			return null;
		}

		List<TrackerFrame> trackerFrames = new FastList<TrackerFrame>(trackers.size());

		for (Tracker tracker : trackers) {
			// Ignore computed trackers if they aren't requested
			if (!includeComputed && tracker.isComputed()) {
				continue;
			}

			TrackerFrame trackerFrame = TrackerFrame.fromTracker(tracker);

			if (trackerFrame != null) {
				trackerFrames.add(trackerFrame);
			}
		}

		return new PoseFrame(trackerFrames);
	}

	//#region Easy Utility Access
	public TrackerFrame findTracker(TrackerBodyPosition designation) {
		return (TrackerFrame)TrackerUtils.findTrackerForBodyPosition(trackerFrames, designation);
	}

	public TrackerFrame findTracker(TrackerBodyPosition designation, TrackerBodyPosition altDesignation) {
		return (TrackerFrame)TrackerUtils.findTrackerForBodyPosition(trackerFrames, designation, altDesignation);
	}
	//#endregion
}
