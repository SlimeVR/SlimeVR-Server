package dev.slimevr.hid.behaviours

import dev.slimevr.hid.HIDReceiver
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.util.timeSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.TrackerStatus
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val idleTimeout = 2.seconds

/**
 * Sleep + idle-watchdog tracking, shared by the legacy and v3 sleep behaviours. One instance per
 * behaviour; jobs run on the receiver's context scope and are dropped by [stop] on disconnect.
 */
class HidSleepTimers(private val receiver: HIDReceiver) {
	private val startedAt = timeSource.markNow()
	private val sleepJobs = mutableMapOf<Int, Job>()
	private val idleJobs = mutableMapOf<Int, Job>()
	private val lastSeen = mutableMapOf<Int, Duration>()

	fun cancelSleep(hidId: Int) {
		sleepJobs.remove(hidId)?.cancel()
	}

	fun scheduleSleep(hidId: Int, delayMs: Long) {
		cancelSleep(hidId)
		sleepJobs[hidId] = receiver.context.scope.launch {
			delay(delayMs)
			receiver.getTracker(hidId)?.context?.dispatch(TrackerActions.SetStatus(TrackerStatus.SLEEPING))
		}
	}

	private fun armIdleTimeout(hidId: Int) {
		lastSeen[hidId] = startedAt.elapsedNow()
		if (idleJobs[hidId]?.isActive == true) return
		idleJobs[hidId] = receiver.context.scope.launch {
			var remaining = idleTimeout
			while (remaining > Duration.ZERO) {
				delay(remaining)
				remaining = (lastSeen[hidId] ?: Duration.ZERO) + idleTimeout - startedAt.elapsedNow()
			}
			receiver.getTracker(hidId)?.context?.dispatch(TrackerActions.SetStatus(TrackerStatus.SLEEPING))
		}
	}

	fun onPacket(hidId: Int) {
		val tracker = receiver.getTracker(hidId) ?: return
		if (tracker.context.state.value.status == TrackerStatus.SLEEPING) {
			cancelSleep(hidId)
			tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
		}
		armIdleTimeout(hidId)
	}

	fun stop() {
		for (job in sleepJobs.values + idleJobs.values) job.cancel()
		sleepJobs.clear()
		idleJobs.clear()
	}
}
