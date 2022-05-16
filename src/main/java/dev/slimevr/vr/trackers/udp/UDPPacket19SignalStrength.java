package dev.slimevr.vr.trackers.udp;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket19SignalStrength extends UDPPacket implements SensorSpecificPacket {

	public int sensorId;
	public int signalStrength;

	public UDPPacket19SignalStrength() {
	}

	@Override
	public int getPacketId() {
		return 19;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		sensorId = buf.get() & 0xFF;
		signalStrength = buf.get();
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
