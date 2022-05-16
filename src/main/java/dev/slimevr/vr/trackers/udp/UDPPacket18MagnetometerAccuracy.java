package dev.slimevr.vr.trackers.udp;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket18MagnetometerAccuracy extends UDPPacket implements SensorSpecificPacket {

	public int sensorId;
	public float accuracyInfo;

	public UDPPacket18MagnetometerAccuracy() {
	}

	@Override
	public int getPacketId() {
		return 18;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		sensorId = buf.get() & 0xFF;
		accuracyInfo = buf.getFloat();
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
