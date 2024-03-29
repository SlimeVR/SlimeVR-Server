package dev.slimevr.autobone

import kotlin.math.*

/**
 * This is a stat calculator based on Welford's online algorithm
 * https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Welford%27s_online_algorithm
 */
class StatsCalculator {
	private var count = 0
	var mean = 0f
		private set
	private var m2 = 0f

	fun reset() {
		count = 0
		mean = 0f
		m2 = 0f
	}

	fun addValue(newValue: Float) {
		count += 1
		val delta = newValue - mean
		mean += delta / count
		val delta2 = newValue - mean
		m2 += delta * delta2
	}

	val variance: Float
		get() = if (count < 1) {
			Float.NaN
		} else {
			m2 / count
		}
	val sampleVariance: Float
		get() = if (count < 2) {
			Float.NaN
		} else {
			m2 / (count - 1)
		}
	val standardDeviation: Float
		get() = sqrt(variance)
}
