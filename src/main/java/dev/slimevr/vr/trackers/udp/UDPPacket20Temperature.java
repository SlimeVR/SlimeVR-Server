package dev.slimevr.vr.trackers.udp;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket20Temperature extends UDPPacket implements SensorSpecificPacket {

	public int sensorId;
	public float temperature;

	public UDPPacket20Temperature() {
	}

	@Override
	public int getPacketId() {
		return 20;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		sensorId = buf.get() & 0xFF;
		temperature = buf.getFloat();
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
