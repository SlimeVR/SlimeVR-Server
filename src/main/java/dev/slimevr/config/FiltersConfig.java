package dev.slimevr.config;

import dev.slimevr.Main;
import dev.slimevr.filtering.TrackerFilters;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerWithFiltering;


public class FiltersConfig {

	// Type of filtering applied (none, smoothing or prediction)
	private String type = "none";

	// Amount/Intensity of the filtering (0 to 1)
	private float amount = 0.3f;

	// Size of the quaternion buffer (how many rotations are kept for the
	// filtering calculations)
	private int buffer = 3;

	public FiltersConfig() {
	}

	public void updateTrackersFilters() {
		for (Tracker t : Main.vrServer.getAllTrackers()) {
			Tracker tracker = t.get();
			if (tracker instanceof TrackerWithFiltering) {
				((TrackerWithFiltering) tracker)
					.setFiltering(
						enumGetType(),
						getAmount(),
						getBuffer()
					);
			}
		}
	}

	public TrackerFilters enumGetType() {
		return TrackerFilters.getByConfigkey(type);
	}

	public void enumSetType(TrackerFilters type) {
		this.type = type.configKey;
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
