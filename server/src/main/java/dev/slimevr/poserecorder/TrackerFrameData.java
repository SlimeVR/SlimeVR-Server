package dev.slimevr.poserecorder;

public enum TrackerFrameData {

	DESIGNATION_STRING(0), ROTATION(1), POSITION(2), DESIGNATION_ENUM(3), ACCELERATION(4),
	RAW_ROTATION(5),;

	public final int flag;

	TrackerFrameData(int id) {
		this.flag = 1 << id;
	}

	public boolean check(int dataFlags) {
		return (dataFlags & this.flag) != 0;
	}
}
