package dev.slimevr.tracking.videocalibration.snapshots

import dev.slimevr.tracking.trackers.TrackerPosition
import kotlin.time.Duration
import kotlin.time.TimeSource

class TrackersSnapshot(
	val instant: TimeSource.Monotonic.ValueTimeMark,
	val trackers: Map<TrackerPosition, TrackerSnapshot>,
)
