package dev.slimevr.tracking.trackers;


import dev.slimevr.filtering.TrackerFilters;


public interface TrackerWithFiltering {

	void setFiltering(TrackerFilters type, float amount);
}
