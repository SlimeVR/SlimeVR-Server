package dev.slimevr.tracking.trackers.udp

data class SensorTap(val tapBits: Int) {
	val doubleTap = tapBits and 0x40 > 0

	enum class TapAxis {
		X,
		Y,
		Z,
	}
}
