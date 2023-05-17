package dev.slimevr.tracking.trackers.udp;

import io.github.axisangles.ktmath.Vector3;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;


public class UDPPacket4Acceleration extends UDPPacket implements SensorSpecificPacket {

	public int sensorId;
	public Vector3 acceleration = Vector3.Companion.getNULL();

	public UDPPacket4Acceleration() {
	}

	@Override
	public int getPacketId() {
		return 4;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		acceleration = new Vector3(buf.getFloat(), buf.getFloat(), buf.getFloat());

		try {
			sensorId = buf.get() & 0xFF;
		} catch (BufferUnderflowException e) {
			// for owo track app
			sensorId = 0;
		}
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
