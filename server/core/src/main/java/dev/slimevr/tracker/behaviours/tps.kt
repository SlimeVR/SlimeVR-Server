package dev.slimevr.tracker.behaviours

import dev.slimevr.logging.AppLogger
import dev.slimevr.tracker.Motion
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.util.timeSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

class TrackerTPSBehaviour : TrackerBehaviour {
	@OptIn(ExperimentalAtomicApi::class)
	override fun observe(receiver: Tracker) {
		val count = AtomicInt(0)

		receiver.context.state.distinctUntilChangedBy { it.rawRotation }.onEach {
			count.incrementAndFetch()
		}.launchIn(receiver.context.scope)

		receiver.context.scope.launch {
			var mark = timeSource.markNow()
			while (isActive) {
				try {
					delay(1000)
					val elapsed = mark.elapsedNow()
					val tps = count.exchange(0) * 1000L / elapsed.inWholeMilliseconds

					// Tracker is at rest if it hasn't been updated in the last second
					val updateMotionAction = if (tps == 0L && receiver.context.state.value.motion != Motion.RESTING) TrackerActions.SetMotion(Motion.RESTING) else null
					receiver.context.dispatchAll(
						listOfNotNull(
							TrackerActions.Update { copy(tps = tps.toUShort()) },
							updateMotionAction,
						),
					)

					mark = timeSource.markNow()
				} catch (e: Exception) {
					AppLogger.coroutines.error(e, "Error in TrackerTPSBehaviour")
				}
			}
		}
	}
}
