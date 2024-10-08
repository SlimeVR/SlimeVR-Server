package dev.slimevr.tracking.trackers

interface TrackerStatusListener {

	fun onTrackerStatusChanged(tracker: Tracker, oldStatus: TrackerStatus, newStatus: TrackerStatus)
}
