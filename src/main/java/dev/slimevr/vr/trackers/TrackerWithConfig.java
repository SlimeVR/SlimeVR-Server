package dev.slimevr.vr.trackers;

public interface TrackerWithConfig {

	void loadConfig(TrackerConfig config);

	void saveConfig(TrackerConfig config);

}
