package io.eiren.vr.trackers;

import io.eiren.util.BufferedTimer;
import io.eiren.vr.processor.TrackerBodyPosition;

public class HMDTracker extends ComputedTracker implements TrackerWithTPS {

	protected BufferedTimer timer = new BufferedTimer(1f);
	
	public HMDTracker(String name) {
		super(name, true, true);
		setBodyPosition(TrackerBodyPosition.HMD);
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
