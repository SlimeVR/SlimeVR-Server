package dev.slimevr.vr;

import dev.slimevr.vr.trackers.Tracker;
import io.eiren.util.collections.FastList;

import java.util.concurrent.atomic.AtomicInteger;


public interface IDevice {
	public static final AtomicInteger nextLocalDeviceId = new AtomicInteger();

	int getId();

	String getManufacturer();

	String getDisplayName();

	String getFirmwareVersion();

	String getCustomName();

	FastList<? extends Tracker> getTrackers();
}
