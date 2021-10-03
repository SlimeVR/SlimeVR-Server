package io.eiren.vr.trackers;

public class BnoTap {
	
	public final boolean doubleTap;
	
	public BnoTap(int tapBits) {
		doubleTap = (tapBits & 0x40) > 0;
	}
	
	@Override
	public String toString() {
		return "Tap{" + (doubleTap ? "double" : "") + "}";
	}
	
	public enum TapAxis {
		X,
		Y,
		Z;
	}
}
