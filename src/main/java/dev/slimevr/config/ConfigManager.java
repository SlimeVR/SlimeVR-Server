package dev.slimevr.config;

import io.eiren.util.ann.ThreadSafe;
import io.eiren.yaml.YamlException;
import io.eiren.yaml.YamlFile;

import java.io.*;


public class ConfigManager {

	private static String CONFIG_PATH = "vrconfig.yml";

	private final YamlFile config = new YamlFile();

	private final TrackersConfig trackersConfig;

	private final FilteringConfig filteringConfig;

	private final AutoboneConfig autoboneConfig;

	private final WindowConfig windowConfig;

	private final KeybindingsConfig keybindingsConfig;

	private final BridgeConfig bridgeConfig;


	public ConfigManager() {
		this.trackersConfig = new TrackersConfig(this);
		this.filteringConfig = new FilteringConfig(this);
		this.autoboneConfig = new AutoboneConfig(this);
		this.windowConfig = new WindowConfig(this);
		this.keybindingsConfig = new KeybindingsConfig(this);
		this.bridgeConfig = new BridgeConfig(this);
	}

	public void loadConfig() {
		try {
			config.load(new FileInputStream(new File(CONFIG_PATH)));
		} catch (FileNotFoundException e) {
			// Config file didn't exist, is not an error
		} catch (YamlException e) {
			e.printStackTrace();
		}
		this.trackersConfig.loadConfig();
		this.autoboneConfig.loadConfig();
		this.filteringConfig.loadConfig();
		this.windowConfig.loadConfig();
		this.bridgeConfig.loadConfig();
	}

	@ThreadSafe
	public synchronized void saveConfig() {
		this.trackersConfig.saveConfig();
		File cfgFile = new File(CONFIG_PATH);
		try {
			config.save(new FileOutputStream(cfgFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public YamlFile getConfig() {
		return config;
	}

	public FilteringConfig getFilteringConfig() {
		return filteringConfig;
	}

	public TrackersConfig getTrackersConfig() {
		return trackersConfig;
	}

	public AutoboneConfig getAutoboneConfig() {
		return autoboneConfig;
	}

	public WindowConfig getWindowConfig() {
		return windowConfig;
	}

	public KeybindingsConfig getKeybindingsConfig() {
		return keybindingsConfig;
	}

	public BridgeConfig getBridgeConfig() {
		return bridgeConfig;
	}
}
