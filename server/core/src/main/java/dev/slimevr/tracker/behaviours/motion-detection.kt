package dev.slimevr.tracker.behaviours

import dev.slimevr.math.angle.Angle
import dev.slimevr.tracker.Motion
import dev.slimevr.tracker.RawRotation
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.tracker.TrackerState
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.seconds

/**
 * Detects whether a tracker is at rest or rotating.
 * Used for Stay Aligned and TapDetection.
 */
class TrackerMotionDetectionBehaviour : TrackerBehaviour {

	override fun observe(receiver: Tracker) {
		var lastStateChangeTime = timeSource.markNow()
		var lastRotationTime = timeSource.markNow()
		var lastRotation: RawRotation = Quaternion.IDENTITY

		// For update loop
		receiver.context.state
			.map { it.rawRotation }
			.distinctUntilChanged()
			.onEach { rotation ->
				val now = timeSource.markNow()
				val motionState = receiver.context.state.value.motion
				val isRotating = isRotating(lastRotation, rotation)

				when (motionState) {
					// Detect if tracker is at rest
					Motion.ROTATING,
					Motion.STARTED_ROTATING,
					->
						if (!isRotating) {
							if (now > lastRotationTime + ENTER_REST_TIME) {
								// Been not moving for long enough; set as at rest.
								receiver.context.dispatch(TrackerActions.SetMotion(Motion.RESTING))
								lastStateChangeTime = now

								// Update the rotation to continue detecting if the tracker is at rest
								lastRotation = rotation
								lastRotationTime = now
							}
						} else if (motionState == Motion.STARTED_ROTATING && (now > lastStateChangeTime + ENTER_MOVING_TIME)) {
							// Been moving for long enough; set as moving.
							receiver.context.dispatch(TrackerActions.SetMotion(Motion.ROTATING))
							lastStateChangeTime = now
						}

					// Detect if tracker is moving
					Motion.RESTING ->
						if (isRotating) {
							// Started moving; set as recently at rest.
							receiver.context.dispatch(TrackerActions.SetMotion(Motion.STARTED_ROTATING))
							lastStateChangeTime = now
						}
				}

				// Update rotation if the tracker is moving
				if (isRotating) {
					lastRotation = rotation
					lastRotationTime = now
				}
			}
			.launchIn(receiver.context.scope)
	}

	/**
	 * Tracker is moving if rotating or accelerating past certain thresholds.
	 */
	private fun isRotating(lastRotation: RawRotation, rotation: RawRotation) = Angle.absBetween(lastRotation, rotation) > MAX_ROTATION

	override fun reduce(state: TrackerState, action: TrackerActions): TrackerState = when (action) {
		is TrackerActions.SetMotion -> {
			state.copy(
				motion = action.motion,
				stayAlignedData = state.stayAlignedData.copy(lockedRotation = if (action.motion == Motion.RESTING) state.stayAlignedData.correctedRotation else null),
			)
		}

		else -> state
	}

	// TODO : these values may need fine-tuning for TapDetection
	//  or maybe need multiple motion detector with an abstract class
	companion object {
		val MAX_ROTATION = Angle.ofDeg(2f)
		val ENTER_REST_TIME = 1.seconds
		val ENTER_MOVING_TIME = 3.seconds
	}
}
