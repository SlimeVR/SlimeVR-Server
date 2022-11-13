package dev.slimevr.vr.trackers.udp;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket0Heartbeat extends UDPPacket {

	public UDPPacket0Heartbeat() {
	}

	@Override
	public int getPacketId() {
		return 0;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		// Empty packet
	}

	@Override
	public void writeData(ByteBuffer buf) throws IOException {
		// Empty packet
	}
}
