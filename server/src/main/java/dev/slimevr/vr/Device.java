package dev.slimevr.vr;

import dev.slimevr.vr.trackers.Tracker;
import io.eiren.util.collections.FastList;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;


public class Device {

	public static final AtomicInteger nextLocalDeviceId = new AtomicInteger();

	private final int id;
	private String customName;
	private String firmwareVersion;
	private String manufacturer;

	private final FastList<Tracker> trackers = new FastList<>();

	public Device() {
		this.id = nextLocalDeviceId.incrementAndGet();
	}

	public int getId() {
		return id;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public String getManufacturer() {
		return this.manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getDisplayName() {
		return null;
	}

	public String getFirmwareVersion() {
		return this.firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public String getCustomName() {
		return this.customName;
	}

	public InetAddress getIpAddress() {
		return null;
	}

	public FastList<Tracker> getTrackers() {
		return trackers;
	}
}
