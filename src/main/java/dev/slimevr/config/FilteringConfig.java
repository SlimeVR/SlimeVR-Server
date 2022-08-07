package dev.slimevr.config;

import dev.slimevr.Main;
import dev.slimevr.vr.trackers.IMUTracker;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerFilters;


public class FilteringConfig {


	private String type = "NONE";
	private float amount = 0.3f;
	private int ticks = 1;

	public FilteringConfig() {
	}

	public void updateTrackersFilters(TrackerFilters filter, float amount, int ticks) {
		setType(filter.name());
		setAmount(amount);
		setTicks(ticks);

		IMUTracker imu;
		for (Tracker t : Main.vrServer.getAllTrackers()) {
			Tracker tracker = t.get();
			if (tracker instanceof IMUTracker) {
				imu = (IMUTracker) tracker;
				imu.setFilter(filter.name(), amount, ticks);
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

	public int getTicks() {
		return ticks;
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

}
