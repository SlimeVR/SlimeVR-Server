package dev.slimevr.tracking.videocalibration.snapshots

import dev.slimevr.tracking.videocalibration.data.Camera
import java.awt.image.BufferedImage
import kotlin.time.Duration
import kotlin.time.TimeSource

class ImageSnapshot(
	val instant: TimeSource.Monotonic.ValueTimeMark,
	val timestamp: Duration,
	val image: BufferedImage,
	val camera: Camera,
)
