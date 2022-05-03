package dev.slimevr.vr.trackers;

public enum TrackerFilters {

	NONE(0),
	INTERPOLATION(1),
	EXTRAPOLATION(2);

	public static final TrackerFilters[] values = values();

	public final int id;

	TrackerFilters(int id) {
		this.id = id;
	}

	public static TrackerFilters fromId(int id) {
		for (TrackerFilters filter : values) {
			if (filter.id == id)
				return filter;
		}
		return null;
	}
}
