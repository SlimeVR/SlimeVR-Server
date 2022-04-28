package dev.slimevr.vr.trackers.udp;

public class UDPPacket16Rotation2 extends UDPPacket1Rotation {

	public UDPPacket16Rotation2() {
	}

	@Override
	public int getPacketId() {
		return 16;
	}

	@Override
	public int getSensorId() {
		return 1;
	}
}
