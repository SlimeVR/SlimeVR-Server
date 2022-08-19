package dev.slimevr.vr.trackers;


public interface TrackerWithFiltering {

	void setFiltering(TrackerFilters type, float amount, int buffer);
}
