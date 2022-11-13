package dev.slimevr.vr.trackers.udp;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket200ProtocolChange extends UDPPacket {

	public int targetProtocol;
	public int targetProtocolVersion;

	public UDPPacket200ProtocolChange() {
	}

	@Override
	public int getPacketId() {
		return 200;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		targetProtocol = buf.get() & 0xFF;
		targetProtocolVersion = buf.get() & 0xFF;
	}

	@Override
	public void writeData(ByteBuffer buf) throws IOException {
		buf.put((byte) targetProtocol);
		buf.put((byte) targetProtocolVersion);
	}
}
