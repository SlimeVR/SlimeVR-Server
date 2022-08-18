package dev.slimevr.vr.trackers;

import java.util.HashMap;
import java.util.Map;


public enum TrackerFilters {

	NONE(0, "none"),
	SMOOTHING(1, "smoothing"),
	PREDICTION(2, "prediction");

	private static final Map<String, TrackerFilters> byConfigkey = new HashMap<>();

	static {
		for (TrackerFilters configVal : values()) {
			byConfigkey.put(configVal.configKey.toLowerCase(), configVal);
		}
	}

	public static final TrackerFilters[] values = values();
	public final int id;
	public final String configKey;

	TrackerFilters(int id, String configKey) {
		this.id = id;
		this.configKey = configKey;
	}

	public static TrackerFilters fromId(int id) {
		for (TrackerFilters filter : values) {
			if (filter.id == id)
				return filter;
		}
		return null;
	}

	public static TrackerFilters getByConfigkey(String configKey) {
		return configKey == null ? null : byConfigkey.get(configKey.toLowerCase());
	}
}
