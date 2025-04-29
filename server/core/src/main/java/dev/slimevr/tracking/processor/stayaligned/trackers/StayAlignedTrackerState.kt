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
	val restDetector = StayAlignedDefaults.makeRestDetector()

	// Rotation of the tracker when it was locked
	var lockedRotation: Quaternion? = null

	// Yaw correction to apply to tracker rotation
	var yawCorrection = Angle.ZERO

	// Alignment error that yaw correction attempts to minimize
	var yawErrors = YawErrors()

	fun update() {
		restDetector.update(tracker.getRawRotation())
		if (restDetector.state == RestDetector.State.AT_REST) {
			if (lockedRotation == null) {
				lockedRotation = tracker.getAdjustedRotationForceStayAligned()
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
