package dev.slimevr.vr.trackers.udp;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket14Error extends UDPPacket implements SensorSpecificPacket {

	public int sensorId;
	public int errorNumber;

	public UDPPacket14Error() {
	}

	@Override
	public int getPacketId() {
		return 14;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		sensorId = buf.get() & 0xFF;
		errorNumber = buf.get() & 0xFF;
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
