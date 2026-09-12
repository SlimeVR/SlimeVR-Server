package dev.slimevr.util

import kotlin.time.Duration

/**
 * Blocks the calling thread for a requested duration, as precisely as the platform allows.
 */
fun interface PreciseWaiter {
	fun sleep(duration: Duration)
}
