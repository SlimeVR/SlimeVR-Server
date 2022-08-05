package dev.slimevr.config;

import dev.slimevr.Main;
import dev.slimevr.vr.trackers.IMUTracker;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerFilters;


public class FilteringConfig {

	private final ConfigManager configManager;

	public static String CONFIG_PREFIX = "filters.";
	public static float DEFAULT_INTENSITY = 0.3f;
	public static int DEFAULT_TICK = 1;

	private String type;
	private float amount;
	private int ticks;

	public FilteringConfig(ConfigManager configManager) {
		this.configManager = configManager;

	}

	public void loadConfig() {
		this.type = this.configManager
			.getConfig()
			.getString(CONFIG_PREFIX + "type", TrackerFilters.NONE.name());
		this.amount = this.configManager
			.getConfig()
			.getFloat(CONFIG_PREFIX + "amount", DEFAULT_INTENSITY);
		this.ticks = this.configManager
			.getConfig()
			.getInt(CONFIG_PREFIX + "tickCount", DEFAULT_TICK);
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

	public void setTicks(int ticks) {
		this.ticks = ticks;
		this.configManager.getConfig().setProperty(CONFIG_PREFIX + "tickCount", ticks);
	}

	public int getTicks() {
		return ticks;
	}

	public void setAmount(float amount) {
		this.amount = amount;
		this.configManager.getConfig().setProperty(CONFIG_PREFIX + "amount", amount);
	}

	public float getAmount() {
		return amount;
	}

	public void setType(String type) {
		this.type = type;
		this.configManager.getConfig().setProperty(CONFIG_PREFIX + "type", type);
	}

	public String getType() {
		return type;
	}
}
