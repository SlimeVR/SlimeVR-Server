package dev.slimevr.poserecorder;

public enum TrackerFrameData {

	DESIGNATION(0), ROTATION(1), POSITION(2),;

	public final int flag;

	TrackerFrameData(int id) {
		this.flag = 1 << id;
	}

	public boolean check(int dataFlags) {
		return (dataFlags & this.flag) != 0;
	}
}
