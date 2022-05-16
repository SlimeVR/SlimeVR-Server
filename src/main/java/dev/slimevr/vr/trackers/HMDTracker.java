package dev.slimevr.vr.trackers;

import io.eiren.util.BufferedTimer;


public class HMDTracker extends ComputedTracker implements TrackerWithTPS {

	protected BufferedTimer timer = new BufferedTimer(1f);

	public HMDTracker(String name) {
		super(0, name, true, true);
		setBodyPosition(TrackerPosition.HMD);
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
	public boolean isComputed() {
		return false;
	}
}
