package dev.slimevr.util

import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * We use Monotonic timesource, which is the most precise (`System.nanoTime()`)
 */
val timeSource = TimeSource.Monotonic

const val millisecondsInSecond = 1_000L
const val microsecondsInSecond = 1_000_000L
const val nanosecondsInSecond = 1_000_000_000L

/**
 * Returns the duration in un-rounded seconds with Float precision.
 *
 * Microseconds are used under the hood due to Float precision being limited.
 */
val Duration.inFloatingSeconds: Float
	get() = this.inWholeMicroseconds / microsecondsInSecond.toFloat()
