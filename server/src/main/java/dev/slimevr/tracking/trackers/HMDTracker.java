package dev.slimevr.tracking.trackers;

import io.eiren.util.BufferedTimer;


public class HMDTracker extends ComputedTracker implements TrackerWithTPS {

	protected BufferedTimer timer = new BufferedTimer(1f);
	private static final long UPDATE_TIMEOUT = 15000;
	private long timeAtLastUpdate;

	public HMDTracker(String name) {
		super(0, name, true, true);
		setBodyPosition(TrackerPosition.HMD);
	}

	public boolean isBeingUpdated() {
		return System.currentTimeMillis() - timeAtLastUpdate < UPDATE_TIMEOUT;
	}

	@Override
	public float getTPS() {
		return timer.getAverageFPS();
	}

	@Override
	public void dataTick() {
		timer.update();
		timeAtLastUpdate = System.currentTimeMillis();
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
