package dev.slimevr.vr.trackers.udp;

public interface SensorSpecificPacket {

	public int getSensorId();

	/**
	 * Sensor with id 255 is "global" representing a whole device
	 *
	 * @param sensorId
	 * @return
	 */
	public static boolean isGlobal(int sensorId) {
		return sensorId == 255;
	}
}
