package dev.slimevr.config;

import dev.slimevr.vr.trackers.TrackerFilters;


public class FiltersConfig {

	// Type of filtering applied (none, smoothing or prediction)
	private String type = "none";

	// Amount/Intensity of the filtering (0 to 1)
	private float amount = 0.3f;

	// Size of the quaternion buffer (how many rotations are kept for the
	// filtering calculations)
	private int buffer = 2;

	public FiltersConfig() {
	}

	public TrackerFilters getEnumType() {
		return TrackerFilters.getByConfigkey(type);
	}

	public String getStringType() {
		return type;
	}

	public void setType(TrackerFilters type) {
		this.type = type.configKey;
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
