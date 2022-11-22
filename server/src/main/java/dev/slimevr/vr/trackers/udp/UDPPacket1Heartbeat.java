package dev.slimevr.vr.trackers.udp;

public class UDPPacket1Heartbeat extends UDPPacket0Heartbeat {

	@Override
	public int getPacketId() {
		return 1;
	}
}
