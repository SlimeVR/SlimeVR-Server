package dev.slimevr.tracker.behaviours

import dev.slimevr.logging.AppLogger
import dev.slimevr.tracker.Motion
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.util.timeSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TrackerTpsBehaviour : TrackerBehaviour {
	override fun observe(receiver: Tracker) {
		receiver.context.scope.launch {
			var mark = timeSource.markNow()
			while (isActive) {
				delay(1000)
				val elapsed = mark.elapsedNow()
				val trackerState = receiver.context.state.value
				val tps = (trackerState.accumulatedTicks * 1000u).toLong() / elapsed.inWholeMilliseconds

				// Tracker is at rest if it hasn't been updated in the last second
				val updateMotionAction = if (tps == 0L && trackerState.motion != Motion.RESTING) TrackerActions.SetMotion(Motion.RESTING) else null
				receiver.context.dispatchAll(
					listOfNotNull(
						TrackerActions.Update { copy(tps = tps.toUShort(), accumulatedTicks = 0u) },
						updateMotionAction,
					),
				)

				mark = timeSource.markNow()
			}
		}
	}
}
