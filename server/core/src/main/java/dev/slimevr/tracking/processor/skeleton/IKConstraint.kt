package dev.slimevr.tracking.processor.skeleton

import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

class IKConstraint(val tracker: Tracker) {
	private var offset = Vector3.NULL
	private var rotationOffset = Quaternion.IDENTITY

	fun getPosition(): Vector3 = tracker.position + (tracker.getRotation() * rotationOffset).sandwich(offset)

	fun reset(nodePosition: Vector3) {
		// HMD on Head and Controllers in hands are assumed to be perfectly aligned with the bones they're intended to track
		// Other generic trackers need to be calibrated from mounting position to actual body part

		// TODO: Make positional mounting calibration configurable with sensible defaults
		// Generally HMD on head and Controllers in hands don't need to be calibrated
		// But sometimes controllers may be used on top of the hands, e.g. for tracking gloves

		if ((tracker.isHmd && tracker.trackerPosition == TrackerPosition.HEAD) ||
			tracker.trackerPosition == TrackerPosition.LEFT_HAND ||
			tracker.trackerPosition == TrackerPosition.RIGHT_HAND
		) {
			return
		}
		rotationOffset = tracker.getRotation().inv()
		offset = nodePosition - tracker.position
	}
}
