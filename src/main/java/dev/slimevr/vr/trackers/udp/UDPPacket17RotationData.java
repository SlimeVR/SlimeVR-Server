package dev.slimevr.vr.trackers.udp;

import com.jme3.math.Quaternion;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket17RotationData extends UDPPacket implements SensorSpecificPacket {

	public static final int DATA_TYPE_NORMAL = 1;
	public static final int DATA_TYPE_CORRECTION = 2;
	public final Quaternion rotation = new Quaternion();
	public int sensorId;
	public int dataType;
	public int calibrationInfo;

	public UDPPacket17RotationData() {
	}

	@Override
	public int getPacketId() {
		return 17;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		sensorId = buf.get() & 0xFF;
		dataType = buf.get() & 0xFF;
		rotation.set(buf.getFloat(), buf.getFloat(), buf.getFloat(), buf.getFloat());
		calibrationInfo = buf.get() & 0xFF;
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
