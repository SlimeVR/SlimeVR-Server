package io.eiren.vr.trackers;

import io.eiren.util.BufferedTimer;

public class SteamVRTracker extends ComputedTracker implements TrackerWithTPS {

	public final int id;
	protected BufferedTimer timer = new BufferedTimer(1f);
	
	public SteamVRTracker(int id, String name, boolean hasRotation, boolean hasPosition) {
		super(name, hasRotation, hasPosition);
		this.id = id;
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
	public boolean userEditable() {
		return true;
	}

	@Override
	public boolean isComputed() {
		return false;
	}
}
