package dev.slimevr.config;

import dev.slimevr.vr.trackers.Tracker;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.collections.FastList;
import io.eiren.yaml.YamlNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class TrackersConfig {

	private final Map<String, TrackerConfig> configuration = new HashMap<>();
	private final ConfigManager configManager;

	public TrackersConfig(ConfigManager configManager) {
		this.configManager = configManager;

	}

	public void loadConfig() {
		List<YamlNode> trackersConfig = this.configManager
			.getConfig()
			.getNodeList("trackers", null);
		for (YamlNode node : trackersConfig) {
			TrackerConfig cfg = new TrackerConfig(node);
			synchronized (configuration) {
				configuration.put(cfg.trackerName, cfg);
			}
		}
	}

	public void saveConfig() {
		List<YamlNode> nodes = this.configManager.getConfig().getNodeList("trackers", null);
		List<Map<String, Object>> trackersConfig = new FastList<>(nodes.size());
		for (YamlNode node : nodes) {
			trackersConfig.add(node.root);
		}
		this.configManager.getConfig().setProperty("trackers", trackersConfig);
		synchronized (configuration) {
			Iterator<TrackerConfig> iterator = configuration.values().iterator();
			while (iterator.hasNext()) {
				TrackerConfig tc = iterator.next();
				Map<String, Object> cfg = null;
				for (Map<String, Object> c : trackersConfig) {
					if (tc.trackerName.equals(c.get("name"))) {
						cfg = c;
						break;
					}
				}
				if (cfg == null) {
					cfg = new HashMap<>();
					trackersConfig.add(cfg);
				}
				tc.saveConfig(new YamlNode(cfg));
			}
		}
	}

	@ThreadSafe
	public TrackerConfig getTracker(Tracker tracker) {
		synchronized (configuration) {
			TrackerConfig config = configuration.get(tracker.getName());
			if (config == null) {
				config = new TrackerConfig(tracker);
				configuration.put(tracker.getName(), config);
			}
			return config;
		}
	}

	public void loadTrackerConfig(Tracker tracker) {
		TrackerConfig config = getTracker(tracker);
		tracker.loadConfig(config);
	}

	public void saveTrackerConfig(Tracker tracker) {
		TrackerConfig tc = getTracker(tracker);
		tracker.saveConfig(tc);

	}
}
