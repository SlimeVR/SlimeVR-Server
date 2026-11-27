package dev.slimevr.hid.behaviours

import dev.slimevr.hid.HIDReceiver
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.util.timeSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.TrackerStatus
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private val idleTimeout = 2.seconds

// How early before the announced sleep deadline the last packet may land and still count as sleep.
// Firmware refreshes the deadline about every 100 ms, the rest covers lost packets.
private val sleepDeadlineMargin = 3.seconds

/**
 * Sleep + idle-watchdog tracking, shared by the legacy and v3 sleep behaviours. One instance per
 * behaviour; jobs run on the receiver's context scope and are dropped by [stop] on disconnect.
 *
 * A tracker that goes quiet is SLEEPING only when its last packet came close to the sleep deadline
 * it announced. Any other silence is TIMED_OUT.
 */
class HidSleepTimers(private val receiver: HIDReceiver) {
	private val startedAt = timeSource.markNow()
	private val idleJobs = mutableMapOf<Int, Job>()
	private val lastSeen = mutableMapOf<Int, Duration>()
	private val sleepDeadlines = mutableMapOf<Int, Duration>()

	fun clearSleepDeadline(hidId: Int) {
		sleepDeadlines.remove(hidId)
	}

	fun setSleepDeadline(hidId: Int, delayMs: Long) {
		sleepDeadlines[hidId] = startedAt.elapsedNow() + delayMs.milliseconds
	}

	private fun silenceStatus(hidId: Int): TrackerStatus {
		val deadline = sleepDeadlines[hidId] ?: return TrackerStatus.TIMED_OUT
		val seen = lastSeen[hidId] ?: return TrackerStatus.TIMED_OUT
		return if (seen >= deadline - sleepDeadlineMargin) TrackerStatus.SLEEPING else TrackerStatus.TIMED_OUT
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
			receiver.getTracker(hidId)?.context?.dispatch(TrackerActions.SetStatus(silenceStatus(hidId)))
		}
	}

	fun onPacket(hidId: Int) {
		val tracker = receiver.getTracker(hidId) ?: return
		val status = tracker.context.state.value.status
		if (status == TrackerStatus.SLEEPING || status == TrackerStatus.TIMED_OUT) {
			clearSleepDeadline(hidId)
			tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
		}
		armIdleTimeout(hidId)
	}

	fun stop() {
		for (job in idleJobs.values) job.cancel()
		idleJobs.clear()
	}
}
