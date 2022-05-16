package dev.slimevr.vr.trackers.udp;

import dev.slimevr.NetworkProtocol;
import dev.slimevr.vr.trackers.Device;
import dev.slimevr.vr.trackers.IMUTracker;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;


public class UDPDevice implements Device {

	public final int id;
	public Map<Integer, IMUTracker> sensors = new HashMap<>();
	public SocketAddress address;
	public InetAddress ipAddress;
	public long lastPacket = System.currentTimeMillis();
	public int lastPingPacketId = -1;
	public long lastPingPacketTime = 0;
	public String name;
	public String descriptiveName;
	public StringBuilder serialBuffer = new StringBuilder();
	public long lastSerialUpdate = 0;
	public long lastPacketNumber = -1;
	public NetworkProtocol protocol = null;
	public int firmwareBuild = 0;
	public boolean timedOut = false;

	public UDPDevice(SocketAddress address, InetAddress ipAddress) {
		this.address = address;
		this.ipAddress = ipAddress;
		this.id = UDPDevice.nextLocalDeviceId.incrementAndGet();
	}

	public boolean isNextPacket(long packetId) {
		if (packetId != 0 && packetId <= lastPacketNumber)
			return false;
		lastPacketNumber = packetId;
		return true;
	}

	@Override
	public String toString() {
		return "udp:/" + ipAddress;
	}

	@Override
	public int getId() {
		return id;
	}
}
