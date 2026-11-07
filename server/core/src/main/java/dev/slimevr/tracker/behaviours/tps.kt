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
import kotlin.time.Duration.Companion.seconds

class TrackerTpsBehaviour : TrackerBehaviour {
	override fun observe(receiver: Tracker) {
		receiver.context.scope.launch {
			var mark = timeSource.markNow()
			while (isActive) {
				delay(1.seconds)
				val elapsed = mark.elapsedNow()
				val trackerState = receiver.context.state.value
				val tps = (trackerState.accumulatedTicks * 1000u).toLong() / elapsed.inWholeMilliseconds

				// Update TPS
				receiver.context.dispatch(
					TrackerActions.Update { copy(tps = tps.toUShort(), accumulatedTicks = 0u) },
				)
				// Tracker is at rest if it has 0 TPS
				if (tps == 0L && trackerState.motion != Motion.RESTING) {
					receiver.context.dispatch(TrackerActions.SetMotion(Motion.RESTING))
				}

				mark = timeSource.markNow()
			}
		}
	}
}
