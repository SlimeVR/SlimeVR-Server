package dev.slimevr.tracker.behaviours

import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.util.timeSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class TrackerYawResetSmoothingBehaviour : TrackerBehaviour {

	private fun animateEase(t: Float) = t * t

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: Tracker) {
		receiver.context.state
			.map { it.yawResetSmoothing }
			.distinctUntilChanged()
			.flatMapLatest { yawResetSmoothing ->
				yawResetSmoothing ?: return@flatMapLatest emptyFlow()
				val startTime = timeSource.markNow()

				receiver.appContext.skeleton.computed
					.onEach {
						val t = (startTime.elapsedNow() / yawResetSmoothing.duration).toFloat().coerceIn(0f, 1f)
						val done = t >= 1f
						val heading = if (done) yawResetSmoothing.to else yawResetSmoothing.from.interpR(yawResetSmoothing.to, animateEase(t))
						receiver.context.dispatch(TrackerActions.TickYawResetSmoothing(heading, done))
						if (done) return@onEach
					}
			}
			.launchIn(receiver.context.scope)
	}
}
