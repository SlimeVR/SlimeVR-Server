package dev.slimevr.tracking.trackers.udp;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket21UserAction extends UDPPacket {

	public static final int RESET_FULL = 2;
	public static final int RESET_YAW = 3;
	public static final int RESET_MOUNTING = 4;

	public int type;

	public UDPPacket21UserAction() {
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
