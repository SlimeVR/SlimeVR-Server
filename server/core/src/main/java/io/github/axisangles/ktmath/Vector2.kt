@file:Suppress("unused")

package io.github.axisangles.ktmath

import kotlinx.serialization.Serializable
import kotlin.math.*

@JvmInline
@Serializable
value class Vector2(val x: Float, val y: Float) {
	companion object {
		val NULL = Vector2(0f, 0f)
		val POS_X = Vector2(1f, 0f)
		val POS_Y = Vector2(0f, 1f)
		val NEG_X = Vector2(-1f, 0f)
		val NEG_Y = Vector2(0f, -1f)
	}

	operator fun component1() = x
	operator fun component2() = y

	operator fun unaryMinus() = Vector2(-x, -y)

	operator fun plus(that: Vector2) = Vector2(
		this.x + that.x,
		this.y + that.y,
	)

	operator fun minus(that: Vector2) = Vector2(
		this.x - that.x,
		this.y - that.y,
	)

	/**
	 * computes the dot product of this vector with that vector
	 * @param that the vector with which to be dotted
	 * @return the dot product
	 **/
	infix fun dot(that: Vector2) = this.x * that.x + this.y * that.y

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
	fun unit(): Vector2 {
		val m = len()
		return if (m == 0f) NULL else this / m
	}

	operator fun times(that: Float) = Vector2(
		this.x * that,
		this.y * that,
	)

	// computes division of this Vector2 by a float
	operator fun div(that: Float) = Vector2(
		this.x / that,
		this.y / that,
	)

	fun isNear(other: Vector2, maxError: Float = 1e-6f) = abs(x - other.x) <= maxError && abs(y - other.y) <= maxError
}

operator fun Float.times(that: Vector2): Vector2 = that * this
