package dev.slimevr.tracking.trackers.udp;


import io.github.axisangles.ktmath.Quaternion;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket17RotationData extends UDPPacket implements SensorSpecificPacket {

	public static final int DATA_TYPE_NORMAL = 1;
	public static final int DATA_TYPE_CORRECTION = 2;
	public Quaternion rotation = Quaternion.Companion.getIDENTITY();
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
		float x = buf.getFloat();
		float y = buf.getFloat();
		float z = buf.getFloat();
		float w = buf.getFloat();
		rotation = new Quaternion(w, x, y, z);
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
