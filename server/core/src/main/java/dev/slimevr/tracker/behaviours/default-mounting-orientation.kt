package dev.slimevr.tracker.behaviours

import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.MountingMethod

class TrackerDefaultMountingOrientationBehaviour : TrackerBehaviour {
	/**
	 * Returns the default mounting orientation for the body part
	 */
	private fun defaultMountingForBodyPart(bodyPart: BodyPart?): Quaternion = when (bodyPart) {
		BodyPart.LEFT_LOWER_ARM, BodyPart.LEFT_HAND,
		BodyPart.LEFT_INDEX_PROXIMAL, BodyPart.LEFT_INDEX_INTERMEDIATE,
		BodyPart.LEFT_INDEX_DISTAL, BodyPart.LEFT_MIDDLE_PROXIMAL,
		BodyPart.LEFT_MIDDLE_INTERMEDIATE, BodyPart.LEFT_MIDDLE_DISTAL,
		BodyPart.LEFT_RING_PROXIMAL, BodyPart.LEFT_RING_INTERMEDIATE,
		BodyPart.LEFT_RING_DISTAL, BodyPart.LEFT_LITTLE_PROXIMAL,
		BodyPart.LEFT_LITTLE_INTERMEDIATE, BodyPart.LEFT_LITTLE_DISTAL,
		BodyPart.LEFT_SHOULDER,
		-> Quaternion.SLIMEVR.LEFT

		BodyPart.RIGHT_LOWER_ARM, BodyPart.RIGHT_HAND,
		BodyPart.RIGHT_INDEX_PROXIMAL, BodyPart.RIGHT_INDEX_INTERMEDIATE,
		BodyPart.RIGHT_INDEX_DISTAL, BodyPart.RIGHT_MIDDLE_PROXIMAL,
		BodyPart.RIGHT_MIDDLE_INTERMEDIATE, BodyPart.RIGHT_MIDDLE_DISTAL,
		BodyPart.RIGHT_RING_PROXIMAL, BodyPart.RIGHT_RING_INTERMEDIATE,
		BodyPart.RIGHT_RING_DISTAL, BodyPart.RIGHT_LITTLE_PROXIMAL,
		BodyPart.RIGHT_LITTLE_INTERMEDIATE, BodyPart.RIGHT_LITTLE_DISTAL,
		BodyPart.RIGHT_SHOULDER,
		-> Quaternion.SLIMEVR.RIGHT

		BodyPart.LEFT_UPPER_ARM, BodyPart.LEFT_LOWER_LEG -> Quaternion.SLIMEVR.FRONT_LEFT

		BodyPart.RIGHT_UPPER_ARM, BodyPart.RIGHT_LOWER_LEG -> Quaternion.SLIMEVR.FRONT_RIGHT

		else -> Quaternion.SLIMEVR.FRONT
	}

	override fun observe(receiver: Tracker) {
		receiver.context.state
			.map { it.bodyPart }
			.distinctUntilChanged()
			.drop(1)
			.onEach {
				val state = receiver.context.state.value
				if (state.position != null && state.lastMountingMethod == MountingMethod.MANUAL) {
					receiver.context.dispatch(TrackerActions.SetMountingOrientation(defaultMountingForBodyPart(it)))
				}
			}.launchIn(receiver.context.scope)
	}
}
