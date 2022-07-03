package dev.slimevr.vr;

import dev.slimevr.vr.trackers.Tracker;
import io.eiren.util.collections.FastList;


public class Device implements IDevice {

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

	@Override
	public String getManufacturer() {
		return this.manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	@Override
	public String getDisplayName() {
		return null;
	}

	@Override
	public String getFirmwareVersion() {
		return this.firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	@Override
	public String getCustomName() {
		return this.customName;
	}

	@Override
	public FastList<Tracker> getTrackers() {
		return trackers;
	}
}
