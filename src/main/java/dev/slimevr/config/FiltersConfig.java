package dev.slimevr.config;

import dev.slimevr.Main;
import dev.slimevr.vr.trackers.IMUTracker;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerFilters;


public class FiltersConfig {


	private String type = "none";
	private float amount = 0.3f;
	private int buffer = 2;

	public FiltersConfig() {
	}

	public void updateTrackersFilters(TrackerFilters filter, float amount, int buffer) {
		setType(filter.name());
		setAmount(amount);
		setBuffer(buffer);

		IMUTracker imu;
		for (Tracker t : Main.vrServer.getAllTrackers()) {
			Tracker tracker = t.get();
			if (tracker instanceof IMUTracker) {
				imu = (IMUTracker) tracker;
				imu.setFilter(filter.name(), amount, buffer);
			}
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public int getBuffer() {
		return buffer;
	}

	public void setBuffer(int buffer) {
		this.buffer = buffer;
	}
}
