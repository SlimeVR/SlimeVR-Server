package dev.slimevr.vr.trackers.udp;

import dev.slimevr.vr.trackers.SensorTap;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket13Tap extends UDPPacket implements SensorSpecificPacket {

	public int sensorId;
	public SensorTap tap;

	public UDPPacket13Tap() {
	}

	@Override
	public int getPacketId() {
		return 13;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		sensorId = buf.get() & 0xFF;
		tap = new SensorTap(buf.get() & 0xFF);
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
