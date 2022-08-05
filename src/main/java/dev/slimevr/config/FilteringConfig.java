package dev.slimevr.config;

import dev.slimevr.Main;
import dev.slimevr.vr.trackers.IMUTracker;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerFilters;
import io.eiren.yaml.YamlNode;

import java.util.HashMap;


public class FilteringConfig {

	private final ConfigManager configManager;

	public static String CONFIG_ROOT = "filters";
	public static float DEFAULT_INTENSITY = 0.3f;
	public static int DEFAULT_TICK = 1;

	private String type;
	private float amount;
	private int ticks;

	private YamlNode rootNode;


	public FilteringConfig(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void loadConfig() {
		this.rootNode = this.configManager.getConfig().getNode(CONFIG_ROOT);
		if (rootNode == null)
			this.rootNode = new YamlNode(new HashMap<>());
		configManager.getConfig().setProperty(CONFIG_ROOT, this.rootNode);

		this.type = this.rootNode
			.getString("type", TrackerFilters.NONE.name());
		this.amount = this.rootNode
			.getFloat("amount", DEFAULT_INTENSITY);
		this.ticks = this.rootNode
			.getInt("tickCount", DEFAULT_TICK);
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
		this.rootNode.setProperty("tickCount", ticks);
	}

	public int getTicks() {
		return ticks;
	}

	public void setAmount(float amount) {
		this.amount = amount;
		this.rootNode.setProperty("amount", amount);
	}

	public float getAmount() {
		return amount;
	}

	public void setType(String type) {
		this.type = type;
		this.rootNode.setProperty("type", type);
	}

	public String getType() {
		return type;
	}
}
