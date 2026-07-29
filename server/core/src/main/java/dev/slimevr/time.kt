package dev.slimevr

import kotlin.time.TimeSource

/**
 * We use Monotonic timesource, which is the most precise (`System.nanoTime()`)
 */
val timeSource = TimeSource.Monotonic
