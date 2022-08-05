package dev.slimevr.config;

import io.eiren.yaml.YamlNode;

import java.util.HashMap;


public class KeybindingsConfig {

	public static String CONFIG_ROOT = "keybindings";

	private YamlNode rootNode;
	private final ConfigManager configManager;

	private String resetBinding = "CTRL+ALT+SHIFT+Y";

	private String quickResetBinding = "CTRL+ALT+SHIFT+U";

	public KeybindingsConfig(ConfigManager cm) {
		this.configManager = cm;
	}

	public void loadConfig() {
		this.rootNode = configManager.getConfig().getNode(CONFIG_ROOT);
		if (this.rootNode == null)
			this.rootNode = new YamlNode(new HashMap<>());
		this.resetBinding = this.rootNode.getString("reset", this.resetBinding);
		this.quickResetBinding = this.rootNode.getString("quickReset", this.quickResetBinding);
		configManager.getConfig().setProperty(CONFIG_ROOT, this.rootNode);
	}

	public String getResetBinding() {
		return resetBinding;
	}

	public String getQuickResetBinding() {
		return quickResetBinding;
	}
}
