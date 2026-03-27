package dev.slimevr.tracker

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

object TrackerInfosBehaviour : TrackerBehaviour {
	override fun reduce(state: TrackerState, action: TrackerActions) = if (action is TrackerActions.Update) action.transform(state) else state

	override fun observe(receiver: TrackerContext) {
		receiver.state.onEach {
// 			AppLogger.tracker.info("Tracker state changed {State}", it)
		}.launchIn(receiver.scope)
	}
}
