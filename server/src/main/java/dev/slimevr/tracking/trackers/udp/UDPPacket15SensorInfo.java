package dev.slimevr.tracking.trackers.udp;

import dev.slimevr.tracking.trackers.TrackerStatus;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket15SensorInfo extends UDPPacket implements SensorSpecificPacket {

	public int sensorId;
	public int sensorStatus;
	public int sensorType;

	public UDPPacket15SensorInfo() {
	}

	public static TrackerStatus getStatus(int sensorStatus) {
		return switch (sensorStatus) {
			case 0 -> TrackerStatus.DISCONNECTED;
			case 1 -> TrackerStatus.OK;
			case 2 -> TrackerStatus.ERROR;
			default -> null;
		};
	}

	@Override
	public int getPacketId() {
		return 15;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		sensorId = buf.get() & 0xFF;
		sensorStatus = buf.get() & 0xFF;
		if (buf.remaining() > 0)
			sensorType = buf.get() & 0xFF;
	}

	@Override
	public void writeData(ByteBuffer buf) throws IOException {
		// Never sent back in current protocol
	}

	@Override
	public int getSensorId() {
		return sensorId;
	}
}
