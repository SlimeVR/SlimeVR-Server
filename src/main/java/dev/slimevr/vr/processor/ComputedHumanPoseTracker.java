package dev.slimevr.vr.processor;

import dev.slimevr.vr.trackers.*;
import dev.slimevr.vr.trackers.udp.UDPDevice;
import io.eiren.util.BufferedTimer;


public class ComputedHumanPoseTracker extends ComputedTracker
	implements TrackerWithTPS, ShareableTracker {

	public final ComputedHumanPoseTrackerPosition skeletonPosition;
	protected final TrackerRole trackerRole;
	protected BufferedTimer timer = new BufferedTimer(1f);

	public ComputedHumanPoseTracker(
		int trackerId,
		ComputedHumanPoseTrackerPosition skeletonPosition,
		TrackerRole role
	) {
		super(trackerId, "human://" + skeletonPosition.name(), true, true);
		this.skeletonPosition = skeletonPosition;
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
	public UDPDevice getDevice() {
		return null;
	}
}
