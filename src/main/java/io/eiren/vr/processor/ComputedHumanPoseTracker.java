package io.eiren.vr.processor;

import io.eiren.vr.trackers.ComputedTracker;

public class ComputedHumanPoseTracker extends ComputedTracker {
	
	public final ComputedHumanPoseTrackerPosition skeletonPosition;

	public ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition skeletonPosition) {
		super("human://" + skeletonPosition.name());
		this.skeletonPosition = skeletonPosition;
	}
}
