package dev.slimevr.vr.trackers;

public class SensorTap {

	public final boolean doubleTap;

	public SensorTap(int tapBits) {
		doubleTap = (tapBits & 0x40) > 0;
	}

	@Override
	public String toString() {
		return "Tap{" + (doubleTap ? "double" : "") + "}";
	}

	public enum TapAxis {
		X, Y, Z
	}
}
