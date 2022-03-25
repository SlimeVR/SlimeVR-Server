package dev.slimevr.vr.trackers.udp;

import java.io.IOException;
import java.nio.ByteBuffer;

import dev.slimevr.vr.trackers.TrackerStatus;

public class UDPPacket15SensorInfo extends UDPPacket implements SensorSpecificPacket {
	
	public int sensorId;
	public int sensorStatus;
	public int sensorType;
	
	public UDPPacket15SensorInfo() {
	}
	
	@Override
	public int getPacketId() {
		return 15;
	}
	
	@Override
	public void readData(ByteBuffer buf) throws IOException {
		sensorId = buf.get() & 0xFF;
		sensorStatus = buf.get() & 0xFF;
		if(buf.remaining() > 0)
			sensorType = buf.get() & 0xFF;
	}
	
	@Override
	public void writeData(ByteBuffer buf) throws IOException {
		// Never sent back in current protocol
	}

	@Override
	public int getSensorId() {
		return sensorId;
	}
	
	public static TrackerStatus getStatus(int sensorStatus) {
		switch(sensorStatus) {
		case 0:
			return TrackerStatus.DISCONNECTED;
		case 1:
			return TrackerStatus.OK;
		case 2:
			return TrackerStatus.ERROR;
		}
		return null;
	}
	
}
