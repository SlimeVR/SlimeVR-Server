package dev.slimevr.config;

import dev.slimevr.vr.trackers.TrackerRole;
import io.eiren.yaml.YamlNode;

import java.util.HashMap;


public class BridgeConfig {

	public static String CONFIG_ROOT = "bridge";

	private YamlNode rootNode;


	private final ConfigManager configManager;

	public BridgeConfig(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void loadConfig() {
		this.rootNode = this.configManager.getConfig().getNode(CONFIG_ROOT);
		if (rootNode == null)
			this.rootNode = new YamlNode(new HashMap<>());
		configManager.getConfig().setProperty(CONFIG_ROOT, this.rootNode);
	}

	public boolean getBridgeTrackerRole(String bridge, TrackerRole role, boolean def) {
		return this.rootNode.getBoolean(bridge + ".trackers." + role.name().toLowerCase(), def);
	}

	public void setBridgeTrackerRole(String bridge, TrackerRole role, boolean def) {
		this.rootNode.setProperty(bridge + ".trackers." + role.name().toLowerCase(), def);
	}
}
