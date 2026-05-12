package dev.slimevr.tracker

import dev.slimevr.skeleton.SkeletonActions
import dev.slimevr.util.safeLaunch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.time.TimeSource

private const val NS_CONVERTER = 1.0e9f
private const val CLUMP_TIME_NS = 0.06f * NS_CONVERTER
private const val NEEDED_ACCEL_DELTA = 4.0f
private const val ALLOWED_BODY_ACCEL = 2.0f
private const val ALLOWED_BODY_ACCEL_SQUARED = ALLOWED_BODY_ACCEL * ALLOWED_BODY_ACCEL
private const val TAP_WINDOW_PER_TAP_S = 0.3f

object TrackerTapDetectionBehaviour : TrackerBehaviour {
	override fun observe(receiver: Tracker) {
		val accelList = ArrayDeque<Pair<Float, Long>>()
		val tapTimestamps = ArrayDeque<Long>()
		var waitForLowAccel = false

		// TODO: only enable this on the trackers that have taps assigned to them
		receiver.context.state
			.distinctUntilChangedBy { it.acceleration }
			.onEach { current ->
				val now = System.nanoTime()
				val magnitude = current.acceleration.len()

				accelList.add(magnitude to now)
				while (accelList.isNotEmpty() && now - accelList.first().second > CLUMP_TIME_NS) {
					accelList.removeFirst()
				}

				val max = accelList.maxOfOrNull { it.first } ?: 0f
				val min = accelList.minOfOrNull { it.first } ?: 0f
				val accelDelta = max - min

				if (accelDelta > NEEDED_ACCEL_DELTA && !waitForLowAccel) {
					val othersOverThreshold = receiver.appContext.server.context.state.value.trackers.values
						.count { it.context.state.value.id != current.id && it.context.state.value.acceleration.lenSq() > ALLOWED_BODY_ACCEL_SQUARED }
					if (othersOverThreshold <= 1) {
						tapTimestamps.add(now)
						waitForLowAccel = true
					}
				}

				if (max < ALLOWED_BODY_ACCEL) {
					waitForLowAccel = false
				}

				if (tapTimestamps.isNotEmpty()) {
					val totalWindowNs = (TAP_WINDOW_PER_TAP_S * tapTimestamps.size * NS_CONVERTER).toLong()
					while (tapTimestamps.isNotEmpty() && now - tapTimestamps.first() > totalWindowNs) {
						tapTimestamps.removeFirst()
					}

					if (tapTimestamps.isNotEmpty()) {
						val lastTapTime = tapTimestamps.last()
						if (now - lastTapTime > (TAP_WINDOW_PER_TAP_S * NS_CONVERTER).toLong()) {
							val count = tapTimestamps.size
							if (count >= 2) {
								println("Detected $count taps on ${receiver.context.state.value.id}")
								// TODO trigger the tap action
							}
							tapTimestamps.clear()
						}
					}
				}
			}
			.launchIn(receiver.context.scope)
	}
}

object TrackerBasicBehaviour : TrackerBehaviour {
	override fun reduce(state: TrackerState, action: TrackerActions) = when (action) {
		is TrackerActions.Update -> action.transform(state)

		is TrackerActions.SetMagStatus -> state.copy(magStatus = action.status)

		is TrackerActions.SetStatus -> state.copy(status = action.status)

		is TrackerActions.SetRotation -> state.copy(
			rawRotation = action.rotation ?: state.rawRotation,
			acceleration = action.acceleration ?: state.acceleration,
		)
	}
}

object TrackerTPSBehaviour : TrackerBehaviour {
	@OptIn(ExperimentalAtomicApi::class)
	override fun observe(receiver: Tracker) {
		val count = AtomicInt(0)

		receiver.context.state.distinctUntilChangedBy { it.rawRotation }.onEach {
			count.incrementAndFetch()
		}.launchIn(receiver.context.scope)

		receiver.context.scope.safeLaunch {
			var mark = TimeSource.Monotonic.markNow()
			while (isActive) {
				try {
					delay(1000)
					val elapsed = mark.elapsedNow()
					val tps = count.exchange(0) * 1000L / elapsed.inWholeMilliseconds
					receiver.context.dispatch(TrackerActions.Update { copy(tps = tps.toUShort()) })
					mark = TimeSource.Monotonic.markNow()
				} catch (e: Exception) {
					dev.slimevr.AppLogger.coroutines.error(e, "Error in TrackerTPSBehaviour")
				}
			}
		}
	}
}

object TrackerToSkeletonBehaviour : TrackerBehaviour {

	override fun observe(receiver: Tracker) {
		receiver.context.state
			.filter { it.bodyPart != null }
			.map { Pair(it.bodyPart, it.rawRotation) }
			.distinctUntilChanged()
			.onEach { (bodyPart, rotation) ->
				bodyPart?.let {
					receiver.appContext.skeleton.context.dispatch(
						SkeletonActions.SetBoneRotation(it, rotation)
					)
				}
		}.launchIn(receiver.context.scope)
	}

}
