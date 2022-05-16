package dev.slimevr.vr.trackers.udp;

import java.io.IOException;
import java.nio.ByteBuffer;


public abstract class UDPPacket {

	/**
	 * Naively read null-terminated ASCII string from the byte buffer
	 *
	 * @param buf
	 * @return
	 * @throws IOException
	 */
	public static String readASCIIString(ByteBuffer buf) throws IOException {
		StringBuilder sb = new StringBuilder();
		while (true) {
			char c = (char) (buf.get() & 0xFF);
			if (c == 0)
				break;
			sb.append(c);
		}
		return sb.toString();
	}

	public static String readASCIIString(ByteBuffer buf, int length) throws IOException {
		StringBuilder sb = new StringBuilder();
		while (length-- > 0) {
			char c = (char) (buf.get() & 0xFF);
			if (c == 0)
				break;
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * Naively write null-terminated ASCII string to byte buffer
	 *
	 * @param str
	 * @param buf
	 * @throws IOException
	 */
	public static void writeASCIIString(String str, ByteBuffer buf) throws IOException {
		for (int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);
			buf.put((byte) (c & 0xFF));
		}
		buf.put((byte) 0);
	}

	public abstract int getPacketId();

	public abstract void readData(ByteBuffer buf) throws IOException;

	public abstract void writeData(ByteBuffer buf) throws IOException;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append('{');
		sb.append(getPacketId());
		if (this instanceof SensorSpecificPacket) {
			sb.append(",sensor:");
			sb.append(((SensorSpecificPacket) this).getSensorId());
		}
		sb.append('}');
		return sb.toString();
	}
}
