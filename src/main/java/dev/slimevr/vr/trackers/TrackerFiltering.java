package dev.slimevr.vr.trackers;

public class TrackerFiltering {
	// TODO move initializing logic from IMUTracker to here so we don't have to
	// load settings once per tracker. We can just cache them in this class.
	// Also will make adding filtering to other types of trackers than
	// IMUTracker much easier if we need to. -Erimel

	public static String CONFIG_PREFIX = "filters.";
	public static float DEFAULT_INTENSITY = 0.3f;
	public static int DEFAULT_TICK = 1;
}
