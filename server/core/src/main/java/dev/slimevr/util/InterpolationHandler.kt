package dev.slimevr.util

import io.eiren.math.FloatMath.animateEase
import io.github.axisangles.ktmath.Quaternion

class InterpolationHandler {
	/**
	 * The starting rotation of this movement.
	 */
	var start = Quaternion.IDENTITY
		private set

	/**
	 * The ending rotation of this movement.
	 */
	var end = Quaternion.IDENTITY
		private set

	/**
	 * The time left for the current movement.
	 */
	var remainingTime = 0f
		private set

	/**
	 * The total interval of interpolation for this movement.
	 */
	var totalTime = 0f
		private set

	/**
	 * The current interpolated value for this tick.
	 */
	var curRotation = Quaternion.IDENTITY
		private set

	/**
	 * Starts an interpolation from [start] to [end] over [interval] seconds.
	 * @param start The starting rotation to interpolate from.
	 * @param end The ending rotation to interpolate to.
	 * @param interval The amount of time that the interpolation will take.
	 * @param shortestDistance Whether the interpolation will take the shortest path or
	 * take the quaternion space path between [start] and [end].
	 */
	@Synchronized
	fun interpolate(
		start: Quaternion = curRotation,
		end: Quaternion,
		interval: Float,
		shortestDistance: Boolean = true,
	) {
		this.totalTime = interval
		remainingTime = interval

		this.start = if (shortestDistance) {
			start.twinNearest(end)
		} else {
			start
		}
		this.end = end

		// The current state is at the start
		// TODO: Maybe handle a mid-interval interpolation swap?
		curRotation = this.start
	}

	/**
	 * The main ticking function, computes [curRotation] for each tick and reduces
	 * [remainingTime] by [delta].
	 * @param delta The time in seconds between the last time [tick] was run and the
	 * current tick.
	 */
	@Synchronized
	fun tick(delta: Float) {
		if (remainingTime > 0f) {
			remainingTime -= delta

			// If we still need to interpolate after the delta change
			if (remainingTime > 0f) {
				// Remaining time decreases to 0, so the interpolation is reversed
				curRotation = end.interpR(
					start,
					animateEase(remainingTime / totalTime),
				)
			} else {
				remainingTime = 0f
				curRotation = end
			}
		}
	}

	@Synchronized
	fun reset() {
		remainingTime = 0f
		totalTime = 0f

		start = Quaternion.IDENTITY
		end = Quaternion.IDENTITY

		curRotation = Quaternion.IDENTITY
	}
}
