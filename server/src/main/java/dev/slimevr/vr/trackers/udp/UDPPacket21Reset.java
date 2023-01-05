package dev.slimevr.vr.trackers.udp;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket21Reset extends UDPPacket {

	public static final int RESET = 0;
	public static final int RESET_YAW = 1;
	public static final int RESET_MOUNTING = 2;

	public int type;

	public UDPPacket21Reset() {
	}

	@Override
	public int getPacketId() {
		return 21;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		type = buf.get() & 0xFF;
	}

	@Override
	public void writeData(ByteBuffer buf) throws IOException {
		// Never sent back in current protocol
	}
}
