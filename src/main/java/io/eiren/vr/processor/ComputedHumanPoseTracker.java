package io.eiren.vr.processor;

import io.eiren.util.BufferedTimer;
import io.eiren.vr.trackers.ComputedTracker;
import io.eiren.vr.trackers.TrackerWithTPS;

public class ComputedHumanPoseTracker extends ComputedTracker implements TrackerWithTPS {
	
	public final ComputedHumanPoseTrackerPosition skeletonPosition;
	protected BufferedTimer timer = new BufferedTimer(1f);

	public ComputedHumanPoseTracker(ComputedHumanPoseTrackerPosition skeletonPosition) {
		super("human://" + skeletonPosition.name());
		this.skeletonPosition = skeletonPosition;
	}
	
	@Override
	public float getTPS() {
		return timer.getAverageFPS();
	}
	
	@Override
	public void dataTick() {
		timer.update();
	}
}
