@file:Suppress("unused")

package io.github.axisangles.ktmath

import kotlinx.serialization.Serializable
import kotlin.math.*

@JvmInline
@Serializable
value class Vector2D(val x: Double, val y: Double) {
	companion object {
		val NULL = Vector2D(0.0, 0.0)
		val POS_X = Vector2D(1.0, 0.0)
		val POS_Y = Vector2D(0.0, 1.0)
		val NEG_X = Vector2D(-1.0, 0.0)
		val NEG_Y = Vector2D(0.0, -1.0)
	}

	operator fun component1() = x
	operator fun component2() = y

	operator fun unaryMinus() = Vector2D(-x, -y)

	operator fun plus(that: Vector2D) = Vector2D(
		this.x + that.x,
		this.y + that.y,
	)

	operator fun minus(that: Vector2D) = Vector2D(
		this.x - that.x,
		this.y - that.y,
	)

	/**
	 * computes the dot product of this vector with that vector
	 * @param that the vector with which to be dotted
	 * @return the dot product
	 **/
	infix fun dot(that: Vector2D) = this.x * that.x + this.y * that.y

	/**
	 * computes the square of the length of this vector
	 * @return the length squared
	 **/
	fun lenSq() = x * x + y * y

	/**
	 * computes the length of this vector
	 * @return the length
	 **/
	fun len() = sqrt(x * x + y * y)

	/**
	 * @return the normalized vector
	 **/
	fun unit(): Vector2D {
		val m = len()
		return if (m == 0.0) NULL else this / m
	}

	operator fun times(that: Double) = Vector2D(
		this.x * that,
		this.y * that,
	)

	// computes division of this Vector2D by a Double
	operator fun div(that: Double) = Vector2D(
		this.x / that,
		this.y / that,
	)

	fun angleTo(that: Vector2D): Double = atan2(that.y, that.x) - atan2(y, x)

	fun isNear(other: Vector2D, maxError: Double = 1e-6) = abs(x - other.x) <= maxError && abs(y - other.y) <= maxError
}

operator fun Double.times(that: Vector2D): Vector2D = that * this
