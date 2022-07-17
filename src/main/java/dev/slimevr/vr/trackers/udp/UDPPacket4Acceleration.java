package dev.slimevr.vr.trackers.udp;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.jme3.math.Vector3f;


public class UDPPacket4Acceleration extends UDPPacket implements SensorSpecificPacket {

	public int sensorId;
	public Vector3f acceleration = new Vector3f();

	public UDPPacket4Acceleration() {
	}

	@Override
	public int getPacketId() {
		return 4;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		sensorId = buf.get() & 0xFF;
		acceleration.set(buf.getFloat(), buf.getFloat(), buf.getFloat());
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
