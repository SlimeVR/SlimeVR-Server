package dev.slimevr.setup

import dev.slimevr.autobone.StatsCalculator
import dev.slimevr.tracking.trackers.Tracker

class HeightConfigHandler(val tracker: Tracker) {
	val maxDeviation = 0.1f
	val stableTimeS = 3f

	// TODO: Collect mean, max, and min values, use max/min to enforce max deviation
	var accumulator = StatsCalculator()
	var accumulatedTime = 0f

	fun tick() {
		val trackerHeight = tracker.position.y

		// Accumulate height and track stability
		accumulator.addValue(trackerHeight)
		// TODO: Actual tick time
		accumulatedTime += 0.1f

		// If the height stability
		if (accumulator.standardDeviation > maxDeviation) {
			accumulator.reset()
			accumulator.addValue(trackerHeight)
			accumulatedTime = 0f
		} else if (accumulatedTime >= stableTimeS) {
		}
	}
}
