package dev.slimevr.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import com.github.jonpeterson.jackson.module.versioning.JsonVersionedModel;
import dev.slimevr.config.serializers.BridgeConfigMapDeserializer;
import dev.slimevr.config.serializers.TrackerConfigMapDeserializer;
import dev.slimevr.vr.trackers.Tracker;

import java.util.HashMap;
import java.util.Map;


@JsonVersionedModel(
	currentVersion = "2", defaultDeserializeToVersion = "1", toCurrentConverterClass = CurrentVRConfigConverter.class
)
public class VRConfig {

	private WindowConfig window = new WindowConfig();

	private FilteringConfig filtering = new FilteringConfig();

	private AutoBoneConfig autobone = new AutoBoneConfig();

	private KeybindingsConfig keybindings = new KeybindingsConfig();

	private SkeletonConfig skeleton = new SkeletonConfig();

	@JsonDeserialize(using = TrackerConfigMapDeserializer.class)
	@JsonSerialize(keyUsing = StdKeySerializers.StringKeySerializer.class)
	private Map<String, TrackerConfig> trackers = new HashMap<>();

	@JsonDeserialize(using = BridgeConfigMapDeserializer.class)
	@JsonSerialize(keyUsing = StdKeySerializers.StringKeySerializer.class)
	private Map<String, BridgeConfig> bridges = new HashMap<>();

	public WindowConfig getWindow() {
		return window;
	}

	public FilteringConfig getFiltering() {
		return filtering;
	}

	public AutoBoneConfig getAutoBone() {
		return autobone;
	}

	public KeybindingsConfig getKeybindings() {
		return keybindings;
	}

	public Map<String, TrackerConfig> getTrackers() {
		return trackers;
	}

	public Map<String, BridgeConfig> getBridges() {
		return bridges;
	}

	public SkeletonConfig getSkeleton() {
		return skeleton;
	}

	public TrackerConfig getTracker(Tracker tracker) {
		TrackerConfig config = trackers.get(tracker.getName());
		if (config == null) {
			config = new TrackerConfig(tracker);
			trackers.put(tracker.getName(), config);
		}
		return config;
	}

	public void readTrackerConfig(Tracker tracker) {
		TrackerConfig config = getTracker(tracker);
		tracker.readConfig(config);
	}

	public void writeTrackerConfig(Tracker tracker) {
		TrackerConfig tc = getTracker(tracker);
		tracker.writeConfig(tc);
	}

	public BridgeConfig getBrige(String bridgeKey) {
		BridgeConfig config = bridges.get(bridgeKey);
		if (config == null) {
			config = new BridgeConfig();
			bridges.put(bridgeKey, config);
		}
		return config;
	}
}

