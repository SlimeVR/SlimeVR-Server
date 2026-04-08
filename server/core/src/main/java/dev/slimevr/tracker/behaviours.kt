package dev.slimevr.tracker

import kotlin.time.TimeSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

object TrackerBasicBehaviour : TrackerBehaviour {
	override fun reduce(state: TrackerState, action: TrackerActions) = if (action is TrackerActions.Update) action.transform(state) else state
}

object TrackerTPSBehaviour : TrackerBehaviour {
	@OptIn(ExperimentalAtomicApi::class)
	override fun observe(receiver: TrackerContext) {
		val count = AtomicInt(0)

		receiver.state.distinctUntilChangedBy { it.rawRotation }.onEach {
			count.incrementAndFetch()
		}.launchIn(receiver.scope)

		receiver.scope.launch {
			var mark = TimeSource.Monotonic.markNow()
			while (isActive) {
				delay(1000)
				val elapsed = mark.elapsedNow()
				val tps = count.exchange(0) * 1000L / elapsed.inWholeMilliseconds
				receiver.dispatch(TrackerActions.Update { copy(tps = tps.toUShort()) })
				mark = TimeSource.Monotonic.markNow()
			}
		}
	}
}
