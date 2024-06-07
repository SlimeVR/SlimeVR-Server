package dev.slimevr.tracking.processor.skeleton

import dev.slimevr.tracking.trackers.Tracker
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

class IKConstraint(val tracker: Tracker) {
	private var offset = Vector3.NULL
	private var rotationOffset = Quaternion.IDENTITY

	fun getPosition(): Vector3 =
		tracker.position + (tracker.getRotation() * rotationOffset).sandwich(offset)

	fun reset(nodePosition: Vector3) {
		offset = nodePosition - tracker.position
		rotationOffset = tracker.getRotation().inv()
	}
}
