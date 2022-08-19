package dev.slimevr.vr.trackers;

import dev.slimevr.VRServer;
import dev.slimevr.config.FiltersConfig;


public class TrackerFiltering {
	VRServer vrserver;
	FiltersConfig filtersConfig;

	public TrackerFiltering(VRServer vrserver) {
		this.vrserver = vrserver;
		filtersConfig = vrserver.getConfigManager().getVrConfig().getFilters();

		updateTrackersFilters(
			filtersConfig.getEnumType(),
			filtersConfig.getAmount(),
			filtersConfig.getBuffer()
		);
	}

	public void updateTrackersFilters(TrackerFilters filter, float amount, int buffer) {
		for (Tracker t : vrserver.getAllTrackers()) {
			Tracker tracker = t.get();
			if (tracker instanceof TrackerWithFiltering) {
				((TrackerWithFiltering) tracker).setFiltering(filter, amount, buffer);
			}
		}

	}
}
