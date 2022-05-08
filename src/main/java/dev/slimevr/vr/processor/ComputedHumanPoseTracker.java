package dev.slimevr.vr.processor;

import dev.slimevr.vr.trackers.ComputedTracker;
import dev.slimevr.vr.trackers.ShareableTracker;
import dev.slimevr.vr.trackers.TrackerRole;
import dev.slimevr.vr.trackers.TrackerWithTPS;
import dev.slimevr.vr.trackers.udp.Device;
import dev.slimevr.vr.trackers.TrackerPosition;
import io.eiren.util.BufferedTimer;

public class ComputedHumanPoseTracker extends ComputedTracker implements TrackerWithTPS, ShareableTracker {

	public final ComputedHumanPoseTrackerPosition skeletonPosition;
	protected final TrackerRole trackerRole;
	protected BufferedTimer timer = new BufferedTimer(1f);

	public ComputedHumanPoseTracker(int trackerId, ComputedHumanPoseTrackerPosition skeletonPosition,
			TrackerRole role) {
		super(trackerId, "human://" + skeletonPosition.name(), true, true);
		this.skeletonPosition = skeletonPosition;
		this.trackerRole = role;
		this.bodyPosition = TrackerPosition.getByRole(role);
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
}
