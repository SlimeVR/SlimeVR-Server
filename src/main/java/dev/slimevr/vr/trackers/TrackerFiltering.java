package dev.slimevr.vr.trackers;

import dev.slimevr.VRServer;
import dev.slimevr.config.FiltersConfig;


public class TrackerFiltering {

	private final VRServer vrserver;
	private final FiltersConfig filtersConfig;

	public TrackerFiltering(VRServer vrserver) {
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
