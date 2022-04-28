package dev.slimevr.vr.trackers.udp;

public interface SensorSpecificPacket {

	/**
	 * Sensor with id 255 is "global" representing a whole device
	 *
	 * @param sensorId
	 * @return
	 */
	static boolean isGlobal(int sensorId) {
		return sensorId == 255;
	}

	int getSensorId();
}
