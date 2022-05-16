package dev.slimevr.vr.trackers.udp;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UDPPacket3Handshake extends UDPPacket {

	public int boardType;
	public int imuType;
	public int mcuType;
	public int firmwareBuild;
	public String firmware;
	public String macString;

	public UDPPacket3Handshake() {
	}

	@Override
	public int getPacketId() {
		return 3;
	}

	@Override
	public void readData(ByteBuffer buf) throws IOException {
		if (buf.remaining() > 0) {
			byte[] mac = new byte[6];
			if (buf.remaining() > 3)
				boardType = buf.getInt();
			if (buf.remaining() > 3)
				imuType = buf.getInt();
			if (buf.remaining() > 3)
				mcuType = buf.getInt(); // MCU TYPE
			if (buf.remaining() > 11) {
				buf.getInt(); // IMU info
				buf.getInt();
				buf.getInt();
			}
			if (buf.remaining() > 3)
				firmwareBuild = buf.getInt();
			int length = 0;
			if (buf.remaining() > 0)
				length = buf.get(); // firmware version length is 1 longer than
									// that because it's nul-terminated
			firmware = readASCIIString(buf, length);
			if (buf.remaining() >= mac.length) {
				buf.get(mac);
				macString = String
					.format(
						"%02X:%02X:%02X:%02X:%02X:%02X",
						mac[0],
						mac[1],
						mac[2],
						mac[3],
						mac[4],
						mac[5]
					);
				if (macString.equals("00:00:00:00:00:00"))
					macString = null;
			}
		}
	}

	@Override
	public void writeData(ByteBuffer buf) throws IOException {
		// Never sent back in current protocol
		// Handshake for RAW SlimeVR and legacy owoTrack has different packet id
		// byte
		// order from normal packets
		// So it's handled by raw protocol call
	}
}
