package dev.slimevr.util

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Vector3
import kotlin.math.sqrt
import kotlin.math.tan

data class ButterworthCoefficients(
	/** Corresponds to cutoff frequency (Hz) as `1f / tau`. **/
	val tau: Float,
	/** Sampling period in seconds. **/
	val Ts: Float,
) {
	val b0: Float
	val b1: Float
	val b2: Float

	val a1: Float
	val a2: Float

	init {
		require(tau > 0f) { "tau must be > 0" }
		require(Ts > 0f) { "Ts must be > 0" }

		val fc = (sqrt(2f) / FastMath.TWO_PI) / tau
		val C = tan(FastMath.PI * fc * Ts)
		val D = (C * C) + (sqrt(2f) * C) + 1f
		val b0 = (C * C) / D

		this.b0 = b0
		this.b1 = 2f * b0
		this.b2 = b0

		// a0 = 1f
		this.a1 = (2f * ((C * C) - 1f)) / D
		this.a2 = ((1f - (sqrt(2f) * C)) + (C * C)) / D
	}
}

class Butterworth(
	coefficients: ButterworthCoefficients,
) {
	var coefficients = coefficients
		private set

	private var initialized = false

	private var y: Float = 0f

	private var state0: Float = 0f
	private var state1: Float = 0f

	fun swapCoefficients(coefficients: ButterworthCoefficients) {
		// Don't do anything if we're not initialized
		if (!initialized) {
			this.coefficients = coefficients
			return
		}

		val old = this.coefficients
		val new = coefficients
		state0 += (old.b0 - new.b0) * y
		state1 += (old.b1 - new.b1 - old.a1 + new.a1) * y

		this.coefficients = coefficients
	}

	fun filter(
		x: Float,
	): Float {
		// Average through duration tau to initialize
		if (!initialized) {
			state0 += 1f // Sample count
			state1 += x // Sum

			val out = state1 / state0

			// If reached tau, initialize
			if (state0 * coefficients.Ts >= coefficients.tau) {
				state0 = out * (1f - coefficients.b0)
				state1 = out * (coefficients.b2 - coefficients.a2)
				initialized = true
			}

			return out
		}

		y = coefficients.b0 * x + state0
		state0 = coefficients.b1 * x - coefficients.a1 * y + state1
		state1 = coefficients.b2 * x - coefficients.a2 * y
		return y
	}
}

class Vector3Butterworth(
	coefficients: ButterworthCoefficients,
) {
	private val x = Butterworth(coefficients)
	private val y = Butterworth(coefficients)
	private val z = Butterworth(coefficients)

	val coefficients
		get() = x.coefficients

	fun swapCoefficients(
		coefficients: ButterworthCoefficients,
	) {
		x.swapCoefficients(coefficients)
		y.swapCoefficients(coefficients)
		z.swapCoefficients(coefficients)
	}

	fun filter(
		x: Vector3,
	): Vector3 = Vector3(
		this.x.filter(x.x),
		this.y.filter(x.y),
		this.z.filter(x.z),
	)
}
