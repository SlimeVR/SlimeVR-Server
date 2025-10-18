package dev.slimevr.math

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.*

/**
 * An angle between [-PI, PI).
 */
@JvmInline
value class Angle(private val rad: Float) {

	fun toRad() = rad

	fun toDeg() = rad * FastMath.RAD_TO_DEG

	operator fun unaryPlus() = this

	operator fun unaryMinus() = Angle(normalize(-rad))

	operator fun plus(other: Angle) = Angle(normalize(rad + other.rad))

	operator fun minus(other: Angle) = Angle(normalize(rad - other.rad))

	operator fun times(scale: Float) = Angle(normalize(rad * scale))

	operator fun div(scale: Float) = Angle(normalize(rad / scale))

	operator fun compareTo(other: Angle) = rad.compareTo(other.rad)

	override fun toString() = "${toDeg()} deg"

	companion object {
		val ZERO = Angle(0.0f)

		fun ofRad(rad: Float) = Angle(normalize(rad))

		fun ofDeg(deg: Float) = Angle(normalize(deg * FastMath.DEG_TO_RAD))

		// Angle between two vectors
		fun absBetween(a: Vector3, b: Vector3) = Angle(normalize(a.angleTo(b)))

		// Angle between two rotations in rotation space
		fun absBetween(a: Quaternion, b: Quaternion) = Angle(normalize(a.angleToR(b)))

		/**
		 * Normalizes an angle to [-PI, PI)
		 */
		private fun normalize(rad: Float): Float {
			// Normalize to [0, 2*PI)
			val r =
				if (rad < 0.0f || rad >= FastMath.TWO_PI) {
					rad - floor(rad * FastMath.INV_TWO_PI) * FastMath.TWO_PI
				} else {
					rad
				}

			// Normalize to [-PI, PI)
			return if (r > FastMath.PI) {
				r - FastMath.TWO_PI
			} else {
				r
			}
		}
	}
}
