package dev.slimevr.tracking.processor.stayaligned.trackers

import dev.slimevr.math.AngleErrors

/**
 * Aggregates the yaw errors from multiple forces.
 */
class YawErrors {
	var lockedError = AngleErrors()
	var centerError = AngleErrors()
	var neighborError = AngleErrors()
}
