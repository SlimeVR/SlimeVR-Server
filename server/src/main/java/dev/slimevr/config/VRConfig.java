package dev.slimevr.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;
import com.github.jonpeterson.jackson.module.versioning.JsonVersionedModel;
import dev.slimevr.config.serializers.BridgeConfigMapDeserializer;
import dev.slimevr.config.serializers.TrackerConfigMapDeserializer;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerRole;

import java.util.HashMap;
import java.util.Map;


@JsonVersionedModel(
	currentVersion = "2", defaultDeserializeToVersion = "1", toCurrentConverterClass = CurrentVRConfigConverter.class
)
public class VRConfig {

	private final WindowConfig window = new WindowConfig();

	private final FiltersConfig filters = new FiltersConfig();

	private final OSCConfig oscRouter = new OSCConfig();

	private final OSCConfig vrcOSC = new OSCConfig();

	private final AutoBoneConfig autobone = new AutoBoneConfig();

	private final KeybindingsConfig keybindings = new KeybindingsConfig();

	private final SkeletonConfig skeleton = new SkeletonConfig();

	private final LegTweaksConfig legTweaks = new LegTweaksConfig();

	private final TapDetectionConfig tapDetection = new TapDetectionConfig();

	@JsonDeserialize(using = TrackerConfigMapDeserializer.class)
	@JsonSerialize(keyUsing = StdKeySerializers.StringKeySerializer.class)
	private final Map<String, TrackerConfig> trackers = new HashMap<>();

	@JsonDeserialize(using = BridgeConfigMapDeserializer.class)
	@JsonSerialize(keyUsing = StdKeySerializers.StringKeySerializer.class)
	private final Map<String, BridgeConfig> bridges = new HashMap<>();

	private final OverlayConfig overlay = new OverlayConfig();

	public VRConfig() {
		// Initialize default settings for OSC Router
		oscRouter.setPortIn(9002);
		oscRouter.setPortOut(9000);

		// Initialize default settings for VRC OSC
		vrcOSC.setPortIn(9001);
		vrcOSC.setPortOut(9000);
		// Initialize default tracker role settings
		vrcOSC
			.setOSCTrackerRole(
				TrackerRole.WAIST,
				vrcOSC.getOSCTrackerRole(TrackerRole.WAIST, true)
			);
		vrcOSC
			.setOSCTrackerRole(
				TrackerRole.LEFT_FOOT,
				vrcOSC.getOSCTrackerRole(TrackerRole.WAIST, true)
			);
		vrcOSC
			.setOSCTrackerRole(
				TrackerRole.RIGHT_FOOT,
				vrcOSC.getOSCTrackerRole(TrackerRole.WAIST, true)
			);
	}


	public WindowConfig getWindow() {
		return window;
	}

	public FiltersConfig getFilters() {
		return filters;
	}

	public OSCConfig getOscRouter() {
		return oscRouter;
	}

	public OSCConfig getVrcOSC() {
		return vrcOSC;
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

	public LegTweaksConfig getLegTweaks() {
		return legTweaks;
	}

	public TapDetectionConfig getTapDetection() {
		return tapDetection;
	}

	public OverlayConfig getOverlay() {
		return overlay;
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

