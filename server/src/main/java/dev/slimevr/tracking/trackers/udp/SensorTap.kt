package dev.slimevr.tracking.trackers.udp

data class SensorTap(val tapBits: Int) {
	val doubleTap = tapBits and 0x40 > 0

	enum class TapAxis {
		X, Y, Z
	}
}

interface SensorSpecificPacket {
	val sensorId: Int
	companion object {
		/**
		 * Sensor with id 255 is "global" representing a whole device
		 *
		 * @param sensorId
		 * @return
		 */
		fun isGlobal(sensorId: Int): Boolean {
			return sensorId == 255
		}
	}
}
