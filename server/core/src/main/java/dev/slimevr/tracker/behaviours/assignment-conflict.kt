package dev.slimevr.tracker.behaviours

import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.TrackerStatus

fun isActive(status: TrackerStatus) = status == TrackerStatus.OK || status == TrackerStatus.SLEEPING || status == TrackerStatus.TIMED_OUT

class TrackerAssignmentConflictBehaviour : TrackerBehaviour {
	override fun observe(receiver: Tracker) {
		receiver.context.state
			.map { isActive(it.status) }
			.distinctUntilChanged()
			.filter { active -> active }
			.onEach {
				val state = receiver.context.state.value
				val bodyPart = state.bodyPart ?: return@onEach

				val bodyPartTaken = receiver.appContext.server.context.state.value.trackers.values.any { other ->
					val otherState = other.context.state.value
					otherState.id != state.id && otherState.bodyPart == bodyPart && isActive(otherState.status)
				}
				if (bodyPartTaken) {
					receiver.context.dispatch(TrackerActions.Update { copy(bodyPart = null) })
				}
			}
			.launchIn(receiver.context.scope)
	}
}
