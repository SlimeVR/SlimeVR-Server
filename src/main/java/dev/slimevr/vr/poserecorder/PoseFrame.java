package dev.slimevr.vr.poserecorder;

import java.util.HashMap;
import java.util.List;

import io.eiren.vr.processor.TrackerBodyPosition;
import io.eiren.vr.trackers.Tracker;

public final class PoseFrame {

	public final HashMap<TrackerBodyPosition, TrackerFrame> trackerFrames;

	public PoseFrame(HashMap<TrackerBodyPosition, TrackerFrame> trackerFrames) {
		this.trackerFrames = trackerFrames;
	}

	public PoseFrame(List<TrackerFrame> trackerFrames) {
		HashMap<TrackerBodyPosition, TrackerFrame> trackerFramesMap = new HashMap<TrackerBodyPosition, TrackerFrame>(trackerFrames.size());

		for (TrackerFrame trackerFrame : trackerFrames) {
			trackerFramesMap.put(trackerFrame.designation, trackerFrame);
		}

		this.trackerFrames = trackerFramesMap;
	}

	public static PoseFrame fromTrackers(List<Tracker> trackers) {
		if (trackers == null || trackers.isEmpty()) {
			return null;
		}

		HashMap<TrackerBodyPosition, TrackerFrame> trackerFrames = new HashMap<TrackerBodyPosition, TrackerFrame>(trackers.size());

		for (Tracker tracker : trackers) {
			TrackerFrame trackerFrame = TrackerFrame.fromTracker(tracker);

			if (trackerFrame != null) {
				trackerFrames.put(trackerFrame.designation, trackerFrame);
			}
		}

		return new PoseFrame(trackerFrames);
	}
}
