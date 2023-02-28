package dev.slimevr.tracking.trackers;

import io.eiren.util.BufferedTimer;


public class HMDTracker extends ComputedTracker implements TrackerWithTPS {

	protected BufferedTimer timer = new BufferedTimer(1f);

	public HMDTracker(String name) {
		super(0, name, name, true, true, null, true);
		setBodyPosition(TrackerPosition.HEAD);
	}

	@Override
	public float getTPS() {
		return timer.getAverageFPS();
	}

	@Override
	public void dataTick() {
		super.dataTick();
		timer.update();
	}

	@Override
	public boolean isComputed() {
		return false;
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
