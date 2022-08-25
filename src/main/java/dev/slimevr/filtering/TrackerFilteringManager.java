package dev.slimevr.filtering;

import dev.slimevr.VRServer;
import dev.slimevr.config.FiltersConfig;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerWithFiltering;


public class TrackerFilteringManager {

	private final VRServer vrserver;
	private final FiltersConfig filtersConfig;

	public TrackerFilteringManager(VRServer vrserver) {
		this.vrserver = vrserver;
		filtersConfig = vrserver.getConfigManager().getVrConfig().getFilters();
	}

	public void updateTrackersFilters() {
		for (Tracker t : vrserver.getAllTrackers()) {
			Tracker tracker = t.get();
			if (tracker instanceof TrackerWithFiltering) {
				((TrackerWithFiltering) tracker)
					.setFiltering(
						filtersConfig.enumGetType(),
						filtersConfig.getAmount(),
						filtersConfig.getBuffer()
					);
			}
		}

	}
}
