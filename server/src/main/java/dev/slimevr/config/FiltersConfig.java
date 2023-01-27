package dev.slimevr.config;

import dev.slimevr.Main;
import dev.slimevr.filtering.TrackerFilters;
import dev.slimevr.tracking.trackers.Tracker;
import dev.slimevr.tracking.trackers.TrackerWithFiltering;


public class FiltersConfig {

	// Type of filtering applied (none, smoothing or prediction)
	private String type = "prediction";

	// Amount/Intensity of the specified filtering (0 to 1)
	private float amount = 0.2f;

	public FiltersConfig() {
	}

	public void updateTrackersFilters() {
		for (Tracker t : Main.getVrServer().getAllTrackers()) {
			Tracker tracker = t.get();
			if (tracker instanceof TrackerWithFiltering) {
				((TrackerWithFiltering) tracker)
					.setFiltering(
						enumGetType(),
						getAmount()
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
}
