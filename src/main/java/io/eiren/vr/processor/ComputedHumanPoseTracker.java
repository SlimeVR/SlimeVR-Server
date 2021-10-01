package io.eiren.vr.processor;

import io.eiren.util.BufferedTimer;
import io.eiren.vr.trackers.ComputedTracker;
import io.eiren.vr.trackers.ShareableTracker;
import io.eiren.vr.trackers.TrackerRole;
import io.eiren.vr.trackers.TrackerWithTPS;

public class ComputedHumanPoseTracker extends ComputedTracker implements TrackerWithTPS, ShareableTracker {
	
	public final ComputedHumanPoseTrackerPosition skeletonPosition;
	protected final TrackerRole trackerRole;
	protected BufferedTimer timer = new BufferedTimer(1f);

	public ComputedHumanPoseTracker(int trackerId, ComputedHumanPoseTrackerPosition skeletonPosition, TrackerRole role) {
		super(trackerId, "human://" + skeletonPosition.name(), true, true);
		this.skeletonPosition = skeletonPosition;
		this.trackerRole = role;
	}
	
	@Override
	public float getTPS() {
		return timer.getAverageFPS();
	}
	
	@Override
	public void dataTick() {
		timer.update();
	}

	@Override
	public TrackerRole getTrackerRole() {
		return trackerRole;
	}
}
