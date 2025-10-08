package dev.slimevr.math

import kotlin.math.*

/**
 * Averages angles by summing vectors.
 *
 * See https://www.themathdoctors.org/averaging-angles/
 */
class AngleAverage {

	private var sumX = 0.0f
	private var sumY = 0.0f

	/**
	 * Adds another angle to the average.
	 */
	fun add(angle: Angle, weight: Float = 1.0f) {
		sumX += cos(angle.toRad()) * weight
		sumY += sin(angle.toRad()) * weight
	}

	/**
	 * Gets the average angle.
	 */
	fun toAngle(): Angle = if (isEmpty()) {
		Angle.ZERO
	} else {
		Angle.ofRad(atan2(sumY, sumX))
	}

	/**
	 * Whether there are any angles to average.
	 */
	fun isEmpty() = sumX == 0.0f && sumY == 0.0f
}
