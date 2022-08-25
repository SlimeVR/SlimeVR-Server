package dev.slimevr.vr.trackers;


import dev.slimevr.filtering.TrackerFilters;

public interface TrackerWithFiltering {

	void setFiltering(TrackerFilters type, float amount, int buffer);
}
