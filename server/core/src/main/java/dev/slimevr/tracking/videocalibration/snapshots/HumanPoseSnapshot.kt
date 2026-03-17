package dev.slimevr.tracking.videocalibration.snapshots

import dev.slimevr.tracking.videocalibration.data.Camera
import dev.slimevr.tracking.videocalibration.data.CocoWholeBodyKeypoint
import io.github.axisangles.ktmath.Vector2D
import kotlin.time.Duration
import kotlin.time.TimeSource

class HumanPoseSnapshot(
	val instant: TimeSource.Monotonic.ValueTimeMark,
	val timestamp: Duration,
	val joints: Map<CocoWholeBodyKeypoint, Vector2D>,
	val camera: Camera,
)
