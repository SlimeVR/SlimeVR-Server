package dev.slimevr.vr.trackers;

import io.eiren.util.BufferedTimer;


public class VRTracker extends ComputedTracker {

	protected BufferedTimer timer = new BufferedTimer(1f);

	public VRTracker(int id, String serial, String name, boolean hasRotation, boolean hasPosition) {
		super(id, serial, name, hasRotation, hasPosition);
	}

	public VRTracker(int id, String name, boolean hasRotation, boolean hasPosition) {
		super(id, name, name, hasRotation, hasPosition);
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
