package com.oneeurofilter

import kotlin.math.PI
import kotlin.math.abs

fun smoothingFactor(tE: Double, cutoff: Double): Double {
    val r = 2 * PI * cutoff * tE
    return r / (r + 1)
}

fun exponentialSmoothing(a: Double, x: Double, xPrev: Double): Double {
    return a * x + (1 - a) * xPrev
}

class OneEuroFilter(
    x0: Double,
    dx0: Double = 0.0,
    private val minCutoff: Double = 1.0,
    private val beta: Double = 0.0,
    private val dCutoff: Double = 1.0
) {
    private var xPrev: Double = x0
    private var dxPrev: Double = dx0
    private var tPrev: Long = System.nanoTime()

    operator fun invoke(x: Double): Double {
        val t = System.nanoTime()
        val tE = (t - tPrev) / 1_000_000_000.0 // Convert nanoseconds to seconds

        return if (tE != 0.0) {
            // The filtered derivative of the signal
            val aD = smoothingFactor(tE, dCutoff)
            val dx = (x - xPrev) / tE
            val dxHat = exponentialSmoothing(aD, dx, dxPrev)

            // The filtered signal
            val cutoff = minCutoff + beta * abs(dxHat)
            val a = smoothingFactor(tE, cutoff)
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
        tPrev = System.nanoTime()
    }
}
