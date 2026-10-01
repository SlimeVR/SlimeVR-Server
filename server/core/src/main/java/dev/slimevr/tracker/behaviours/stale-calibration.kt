package dev.slimevr.tracker.behaviours

import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.MountingMethod
import solarxr_protocol.datatypes.TrackerStatus

/**
 * Marks the calibration stale when the tracker starts being used on a body part: when it connects
 * while assigned, gets assigned while connected, or moves to another body part.
 * A full reset is needed every time. The mounting only when the body part changed or the tracker was
 * never mounted, since a reconnecting tracker is still mounted the same way.
 */
class TrackerStaleCalibrationBehaviour : TrackerBehaviour {
	private data class Placement(val connected: Boolean, val bodyPart: BodyPart?)

	override fun observe(receiver: Tracker) {
		var previous: Placement? = null
		receiver.context.state
			.map { Placement(it.status == TrackerStatus.OK || it.status == TrackerStatus.SLEEPING, it.bodyPart) }
			.distinctUntilChanged()
			.onEach { placement ->
				val last = previous
				previous = placement
				val startedUsingBodyPart = placement.connected && placement.bodyPart != null &&
					(last == null || !last.connected || last.bodyPart != placement.bodyPart)
				if (startedUsingBodyPart) {
					val movedBodyPart = last != null && last.bodyPart != placement.bodyPart
					val neverMounted = receiver.context.state.value.lastMountingMethod == MountingMethod.MANUAL
					receiver.context.dispatch(TrackerActions.MarkCalibrationStale(mounting = movedBodyPart || neverMounted))
				}
			}.launchIn(receiver.context.scope)
	}
}
