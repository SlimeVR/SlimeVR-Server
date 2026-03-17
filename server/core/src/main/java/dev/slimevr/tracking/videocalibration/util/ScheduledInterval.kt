package dev.slimevr.tracking.videocalibration.util

import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * Helper to track when to run a regularly scheduled task.
 *
 * Useful when run in a single-threaded loop like VRServer::onTick.
 */
class ScheduledInterval(val interval: Duration) {

	private var nextInvoke = TimeSource.Monotonic.markNow()

	/**
	 * Checks if the task should be invoked.
	 */
	fun shouldInvoke(): Boolean {
		val now = TimeSource.Monotonic.markNow()
		if (now >= nextInvoke) {
			while (nextInvoke < now) {
				nextInvoke += interval
			}
			return true
		} else {
			return false
		}
	}
}
