package dev.slimevr.vr.trackers.udp;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket12BatteryLevel extends UDPPacket {

	public float voltage;
	public float level;

	public UDPPacket12BatteryLevel() {
	}

	@Override
	public int getPacketId() {
		return 12;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		voltage = buf.getFloat();
		if (buf.remaining() > 3) {
			level = buf.getFloat();
		} else {
			level = voltage;
			voltage = 0.0f;
		}
	}

	@Override
	public void writeData(ByteBuffer buf) throws IOException {
		// Never sent back in current protocol
	}
}
