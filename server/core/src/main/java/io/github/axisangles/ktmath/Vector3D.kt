@file:Suppress("unused")

package io.github.axisangles.ktmath

import kotlinx.serialization.Serializable
import kotlin.math.*

@JvmInline
@Serializable
value class Vector3D(val x: Double, val y: Double, val z: Double) {
	companion object {
		val NULL = Vector3D(0.0, 0.0, 0.0)
		val POS_X = Vector3D(1.0, 0.0, 0.0)
		val POS_Y = Vector3D(0.0, 1.0, 0.0)
		val POS_Z = Vector3D(0.0, 0.0, 1.0)
		val NEG_X = Vector3D(-1.0, 0.0, 0.0)
		val NEG_Y = Vector3D(0.0, -1.0, 0.0)
		val NEG_Z = Vector3D(0.0, 0.0, -1.0)
	}

	operator fun component1() = x
	operator fun component2() = y
	operator fun component3() = z

	operator fun unaryMinus() = Vector3D(-x, -y, -z)

	operator fun plus(that: Vector3D) = Vector3D(
		this.x + that.x,
		this.y + that.y,
		this.z + that.z,
	)

	operator fun minus(that: Vector3D) = Vector3D(
		this.x - that.x,
		this.y - that.y,
		this.z - that.z,
	)

	/**
	 * computes the dot product of this vector with that vector
	 * @param that the vector with which to be dotted
	 * @return the dot product
	 **/
	infix fun dot(that: Vector3D) = this.x * that.x + this.y * that.y + this.z * that.z

	/**
	 * computes the cross product of this vector with that vector
	 * @param that the vector with which to be crossed
	 * @return the cross product
	 **/
	infix fun cross(that: Vector3D) = Vector3D(
		this.y * that.z - this.z * that.y,
		this.z * that.x - this.x * that.z,
		this.x * that.y - this.y * that.x,
	)

	infix fun hadamard(that: Vector3D) = Vector3D(
		this.x * that.x,
		this.y * that.y,
		this.z * that.z,
	)

	/**
	 * computes the square of the length of this vector
	 * @return the length squared
	 **/
	fun lenSq() = x * x + y * y + z * z

	/**
	 * computes the length of this vector
	 * @return the length
	 **/
	fun len() = sqrt(x * x + y * y + z * z)

	/**
	 * @return the normalized vector
	 **/
	fun unit(): Vector3D {
		val m = len()
		return if (m == 0.0) NULL else this / m
	}

	operator fun times(that: Double) = Vector3D(
		this.x * that,
		this.y * that,
		this.z * that,
	)

	// computes division of this Vector3D by a Double
	operator fun div(that: Double) = Vector3D(
		this.x / that,
		this.y / that,
		this.z / that,
	)

	/**
	 * computes the angle between this vector with that vector
	 * @param that the vector to which the angle is computed
	 * @return the angle
	 **/
	fun angleTo(that: Vector3D): Double = atan2(this.cross(that).len(), this.dot(that))

	fun isNear(other: Vector3D, maxError: Double = 1e-6) = abs(x - other.x) <= maxError && abs(y - other.y) <= maxError && abs(z - other.z) <= maxError

	fun toFloat() = Vector3(x.toFloat(), y.toFloat(), z.toFloat())

	override fun toString(): String = "Vector3D(x=$x y=$y z=$z)"
}

operator fun Double.times(that: Vector3D): Vector3D = that * this
