package dev.slimevr.tracking.processor.stayaligned.trackers

import dev.slimevr.math.Angle
import dev.slimevr.tracking.processor.stayaligned.StayAlignedDefaults
import dev.slimevr.tracking.trackers.Tracker
import io.github.axisangles.ktmath.Quaternion

class StayAlignedTrackerState(
	val tracker: Tracker,
) {
	// Whether to hide the yaw correction
	var hideCorrection = false

	// Detects whether the tracker is at rest
	val restDetector = StayAlignedDefaults.restDetector()

	// Rotation of the tracker when it was locked
	var lockedRotation: Quaternion? = null

	// Yaw correction to apply to tracker rotation
	var yawCorrection = Angle.ZERO

	// Alignment error that yaw correction attempts to minimize
	var yawErrors = YawErrors()

	fun update() {
		val atRest = restDetector.update(tracker.getRawRotation())
		if (atRest) {
			if (lockedRotation == null) {
				lockedRotation = tracker.getRotation()
			}
		} else {
			lockedRotation = null
		}
	}

	fun reset() {
		restDetector.reset()
		lockedRotation = null
		yawCorrection = Angle.ZERO
		yawErrors = YawErrors()
	}
}
