package dev.slimevr.tracking.trackers;

import io.eiren.util.BufferedTimer;


public class ComputedHumanPoseTracker extends ComputedTracker
	implements TrackerWithTPS, ShareableTracker {

	protected final TrackerRole trackerRole;
	protected BufferedTimer timer = new BufferedTimer(1f);

	public ComputedHumanPoseTracker(
		int trackerId,
		TrackerRole role
	) {
		super(trackerId, "human://" + role.name(), true, true);
		this.trackerRole = role;
		// TODO: Use `TrackerPosition` instead of `TrackerRole`
		this.bodyPosition = TrackerPosition.getByTrackerRole(role).orElse(null);
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

	@Override
	public Device getDevice() {
		return null;
	}

	@Override
	public TrackerJava get() {
		return this;
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public String getCustomName() {
		return null;
	}
}
