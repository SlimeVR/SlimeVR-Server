package dev.slimevr.vr.trackers.udp;

import com.jme3.math.Quaternion;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket1Rotation extends UDPPacket implements SensorSpecificPacket {

	public final Quaternion rotation = new Quaternion();

	public UDPPacket1Rotation() {
	}

	@Override
	public int getPacketId() {
		return 1;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		rotation.set(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());
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
