package dev.slimevr.vr.trackers.udp;

import dev.slimevr.NetworkProtocol;
import dev.slimevr.vr.Device;
import dev.slimevr.vr.trackers.IMUTracker;
import dev.slimevr.vr.trackers.Tracker;
import io.eiren.util.collections.FastList;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;


public class UDPDevice extends Device {

	public final int id;
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
	private final FastList<Tracker> trackers = new FastList<>();
	private final ConcurrentHashMap<Integer, Integer> remote_id_map = new ConcurrentHashMap<>();

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

	@Override
	public String getManufacturer() {
		return "SlimeVR";
	}

	@Override
	public String getDisplayName() {
		return name;
	}

	@Override
	public String getFirmwareVersion() {
		return "v" + firmwareBuild;
	}

	@Override
	public String getCustomName() {
		return name;
	}

	@Override
	public InetAddress getIpAddress() {
		return this.ipAddress;
	}

	@Override
	public FastList<Tracker> getTrackers() {
		return this.trackers;
	}

	public IMUTracker setupTrackerByRemoteId(int id) {
		if (remote_id_map.get(id) == null) {
			// New tracker id sent, create a mapping to the future
			// position of the tracker in the trackers list
			remote_id_map.put(id, this.trackers.size());
			return null; // There's no tracker set up yet
		}
		return getTrackerByRemoteId(id);
	}

	public IMUTracker getTrackerByRemoteId(int id) {
		Integer remote_id = remote_id_map.get(id);
		if (remote_id != null && remote_id < this.getTrackers().size())
			return (IMUTracker) this.getTrackers().get(remote_id);
		return null;
	}
}
