package dev.slimevr.vr.trackers;

import java.util.concurrent.atomic.AtomicInteger;


public interface Device {

	public static final AtomicInteger nextLocalDeviceId = new AtomicInteger();

	int getId();
}
