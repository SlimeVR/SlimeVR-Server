package dev.slimevr.vr.trackers;

public class HMDTracker extends VRTracker {

	public HMDTracker(String name) {
		super(0, name, true, true, null);
		setBodyPosition(TrackerPosition.HMD);
	}

	@Override
	public boolean isComputed() {
		return false;
	}

	@Override
	public boolean userEditable() {
		return false;
	}
}
