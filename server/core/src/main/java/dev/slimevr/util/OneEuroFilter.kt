package dev.slimevr.util

import kotlin.math.PI
import kotlin.math.abs
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

fun smoothingFactor(tE: Double, cutoff: Double): Double {
	val r = 2.0 * PI * cutoff * tE
	return r / (r + 1.0)
}

fun exponentialSmoothing(a: Double, x: Double, xPrev: Double): Double = a * x + (1 - a) * xPrev

class OneEuroFilter(
	x0: Double,
	dx0: Double = 0.0,
	private val minCutoff: Double = 1.0,
	private val beta: Double = 0.0,
	private val dCutoff: Double = 1.0,
) {
	private var xPrev: Double = x0
	private var dxPrev: Double = dx0
	private var tPrev = TimeSource.Monotonic.markNow()

	operator fun invoke(x: Double): Double {
		val t = TimeSource.Monotonic.markNow()
		val tE = t - tPrev

		return if (tE.isPositive()) {
			val tES = tE.toDouble(DurationUnit.SECONDS)

			// The filtered derivative of the signal
			val aD = smoothingFactor(tES, dCutoff)
			val dx = (x - xPrev) / tES
			val dxHat = exponentialSmoothing(aD, dx, dxPrev)

			// The filtered signal
			val cutoff = minCutoff + beta * abs(dxHat)
			val a = smoothingFactor(tES, cutoff)
			val xHat = exponentialSmoothing(a, x, xPrev)

			// Memorize the previous values
			xPrev = xHat
			dxPrev = dxHat
			tPrev = t

			xHat
		} else {
			xPrev = x
			x
		}
	}

	fun reset(x0: Double, dx0: Double = 0.0) {
		xPrev = x0
		dxPrev = dx0
		tPrev = TimeSource.Monotonic.markNow()
	}
}
