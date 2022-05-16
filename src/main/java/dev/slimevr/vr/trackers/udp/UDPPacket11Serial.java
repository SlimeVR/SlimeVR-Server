package dev.slimevr.vr.trackers.udp;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket11Serial extends UDPPacket {

	public String serial;

	public UDPPacket11Serial() {
	}

	@Override
	public int getPacketId() {
		return 11;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		int length = buf.getInt();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; ++i) {
			char ch = (char) buf.get();
			sb.append(ch);
		}
		serial = sb.toString();
	}

	@Override
	public void writeData(ByteBuffer buf) throws IOException {
		// Never sent back in current protocol
	}
}
