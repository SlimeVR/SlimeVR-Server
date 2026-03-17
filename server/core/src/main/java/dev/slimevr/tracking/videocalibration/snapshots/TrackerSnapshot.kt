package dev.slimevr.tracking.videocalibration.snapshots

import io.github.axisangles.ktmath.QuaternionD
import io.github.axisangles.ktmath.Vector3D

class TrackerSnapshot(
	val rawTrackerToWorld: QuaternionD,
	val adjustedTrackerToWorld: QuaternionD,
	val trackerOriginInWorld: Vector3D?,
)
