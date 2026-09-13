package dev.slimevr.tracker.behaviours

import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.util.isActive
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.TrackerStatus

class TrackerAssignmentConflictBehaviour : TrackerBehaviour {
	override fun observe(receiver: Tracker) {
		receiver.context.state
			.map { it.status.isActive() }
			.distinctUntilChanged()
			.filter { active -> active }
			.onEach {
				val state = receiver.context.state.value
				val boneId = state.boneId ?: return@onEach

				val boneTaken = receiver.appContext.server.context.state.value.trackers.values.any { other ->
					val otherState = other.context.state.value
					otherState.id != state.id && otherState.boneId == boneId && otherState.status.isActive()
				}
				if (boneTaken) {
					receiver.context.dispatch(TrackerActions.Update { copy(boneId = null) })
				}
			}
			.launchIn(receiver.context.scope)
	}
}
