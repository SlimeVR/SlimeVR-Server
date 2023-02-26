package dev.slimevr.poserecorder

enum class TrackerFrameData(id: Int) {
	DESIGNATION_STRING(0), ROTATION(1), POSITION(2), DESIGNATION_ENUM(3), ACCELERATION(4), RAW_ROTATION(5);

	val flag: Int

	init {
		flag = 1 shl id
	}

	inline fun check(dataFlags: Int): Boolean {
		return dataFlags and flag != 0
	}

	inline fun add(dataFlags: Int): Int {
		return dataFlags or flag
	}

	inline fun remove(dataFlags: Int): Int {
		return dataFlags xor flag
	}
}
