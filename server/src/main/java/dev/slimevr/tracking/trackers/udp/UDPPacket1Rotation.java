package dev.slimevr.tracking.trackers.udp;


import io.github.axisangles.ktmath.Quaternion;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket1Rotation extends UDPPacket implements SensorSpecificPacket {

	public Quaternion rotation = Quaternion.Companion.getIDENTITY();

	public UDPPacket1Rotation() {
	}

	@Override
	public int getPacketId() {
		return 1;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		float x = buf.getFloat();
		float y = buf.getFloat();
		float z = buf.getFloat();
		float w = buf.getFloat();
		rotation = new Quaternion(w, x, y, z);
	}

	@Override
	public void writeData(ByteBuffer buf) throws IOException {
		// Never sent back in current protocol
	}

	@Override
	public int getSensorId() {
		return 0;
	}
}
