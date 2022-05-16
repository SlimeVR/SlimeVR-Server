package dev.slimevr.vr.trackers.udp;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket10PingPong extends UDPPacket {

	public int pingId;

	public UDPPacket10PingPong() {
	}

	public UDPPacket10PingPong(int pingId) {
		this.pingId = pingId;
	}

	@Override
	public int getPacketId() {
		return 10;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		pingId = buf.getInt();
	}

	@Override
	public void writeData(ByteBuffer buf) throws IOException {
		buf.putInt(pingId);
	}
}
