/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.math

import io.github.axisangles.ktmath.Vector3
import io.github.axisangles.ktmath.Vector3.Companion.NULL
import java.util.*
import kotlin.math.*

/**
 * `FastMath` provides 'fast' math approximations and float
 * equivalents of Math functions. These are all used as static values and
 * functions.
 *
 * @author Various
 * @version $Id: FastMath.java,v 1.45 2007/08/26 08:44:20 irrisor Exp $
 */
object FastMath {
	/** A "close to zero" double epsilon value for use  */
	const val DBL_EPSILON: Double = 2.220446049250313E-16

	/** A "close to zero" float epsilon value for use  */
	const val FLT_EPSILON: Float = 1.1920928955078125E-7f

	/** A "close to zero" float epsilon value for use  */
	const val ZERO_TOLERANCE: Float = 0.0001f
	const val ONE_THIRD: Float = 1f / 3f

	/** The value PI as a float. (180 degrees)  */
	const val PI: Float = Math.PI.toFloat()

	/** The value 2PI as a float. (360 degrees)  */
	const val TWO_PI: Float = 2.0f * PI

	/** The value PI/2 as a float. (90 degrees)  */
	const val HALF_PI: Float = 0.5f * PI

	/** The value PI/4 as a float. (45 degrees)  */
	const val QUARTER_PI: Float = 0.25f * PI

	/** The value 1/PI as a float.  */
	const val INV_PI: Float = 1.0f / PI

	/** The value 1/(2PI) as a float.  */
	const val INV_TWO_PI: Float = 1.0f / TWO_PI

	/** A value to multiply a degree value by, to convert it to radians.  */
	const val DEG_TO_RAD: Float = PI / 180.0f

	/** A value to multiply a radian value by, to convert it to degrees.  */
	const val RAD_TO_DEG: Float = 180.0f / PI

	/** A premade random object for random numbers.  */
	val rand: Random = Random(System.currentTimeMillis())

	/**
	 * Returns true if the number is within the specified `tolerance`
	 * value.
	 *
	 * @param value The number to check.
	 * @param tolerance The tolerance to zero (must be positive).
	 * @return True if the number is within the specified `tolerance`
	 * value.
	 */
	fun isApproxZero(value: Float, tolerance: Float): Boolean = value < tolerance && value > -tolerance

	/**
	 * Returns true if the number is within the [.ZERO_TOLERANCE] value.
	 *
	 * @param value The number to check.
	 * @return True if the number is within the [.ZERO_TOLERANCE] value.
	 */
	fun isApproxZero(value: Float): Boolean = isApproxZero(value, ZERO_TOLERANCE)

	/**
	 * Returns true if the two numbers are equal within the specified
	 * `tolerance` value.
	 *
	 * @param valueOne The first number to check.
	 * @param valueTwo The second number to check.
	 * @param tolerance The tolerance to zero (must be positive).
	 * @return True if the numbers are approximately equal within the specified
	 * `tolerance` value.
	 */
	fun isApproxEqual(valueOne: Float, valueTwo: Float, tolerance: Float): Boolean = isApproxZero(valueTwo - valueOne, tolerance)

	/**
	 * Returns true if the two numbers are equal within the
	 * [.ZERO_TOLERANCE] value.
	 *
	 * @param valueOne The first number to check.
	 * @param valueTwo The second number to check.
	 * @return True if the numbers are approximately equal within the
	 * [.ZERO_TOLERANCE] value.
	 */
	fun isApproxEqual(valueOne: Float, valueTwo: Float): Boolean = isApproxEqual(valueOne, valueTwo, ZERO_TOLERANCE)

	/**
	 * Returns true if the number is a power of 2 (2,4,8,16...)
	 *
	 * A good implementation found on the Java boards. note: a number is a power
	 * of two if and only if it is the smallest number with that number of
	 * significant bits. Therefore, if you subtract 1, you know that the new
	 * number will have fewer bits, so ANDing the original number with anything
	 * less than it will give 0.
	 *
	 * @param number The number to test.
	 * @return True if it is a power of two.
	 */
	fun isPowerOfTwo(number: Int): Boolean = (number > 0) && (number and (number - 1)) == 0

	fun nearestPowerOfTwo(number: Int): Int = 2.0.pow(ceil(ln(number.toDouble()) / ln(2.0))).toInt()

	/**
	 * Linear interpolation from startValue to endValue by the given percent.
	 * Basically: ((1 - percent) * startValue) + (percent * endValue)
	 *
	 * @param scale scale value to use. if 1, use endValue, if 0, use
	 * startValue.
	 * @param startValue Beginning value. 0% of f
	 * @param endValue ending value. 100% of f
	 * @return The interpolated value between startValue and endValue.
	 */
	fun interpolateLinear(scale: Float, startValue: Float, endValue: Float): Float {
		if (startValue == endValue) {
			return startValue
		}
		if (scale <= 0f) {
			return startValue
		}
		if (scale >= 1f) {
			return endValue
		}
		return ((1f - scale) * startValue) + (scale * endValue)
	}

	/**
	 * Linear interpolation from startValue to endValue by the given percent.
	 * Basically: ((1 - percent) * startValue) + (percent * endValue)
	 *
	 * @param scale scale value to use. if 1, use endValue, if 0, use
	 * startValue.
	 * @param startValue Beginning value. 0% of f
	 * @param endValue ending value. 100% of f
	 * @return The interpolated value between startValue and endValue.
	 */
	fun interpolateLinear(
		scale: Float,
		startValue: Vector3,
		endValue: Vector3,
	): Vector3 {
		val x = interpolateLinear(scale, startValue.x, endValue.x)
		val y = interpolateLinear(scale, startValue.y, endValue.y)
		val z = interpolateLinear(scale, startValue.z, endValue.z)
		return Vector3(x, y, z)
	}

	/**
	 * Linear extrapolation from startValue to endValue by the given scale. if
	 * scale is between 0 and 1 this method returns the same result as
	 * interpolateLinear if the scale is over 1 the value is linearly
	 * extrapolated. Note that the end value is the value for a scale of 1.
	 *
	 * @param scale the scale for extrapolation
	 * @param startValue the starting value (scale = 0)
	 * @param endValue the end value (scale = 1)
	 * @return an extrapolation for the given parameters
	 */
	fun extrapolateLinear(scale: Float, startValue: Float, endValue: Float): Float {
//        if (scale <= 0f) {
//            return startValue;
//        }
		return ((1f - scale) * startValue) + (scale * endValue)
	}

	/**
	 * Linear extrapolation from startValue to endValue by the given scale. if
	 * scale is between 0 and 1 this method returns the same result as
	 * interpolateLinear if the scale is over 1 the value is linearly
	 * extrapolated. Note that the end value is the value for a scale of 1.
	 *
	 * @param scale the scale for extrapolation
	 * @param startValue the starting value (scale = 0)
	 * @param endValue the end value (scale = 1)
	 * @return an extrapolation for the given parameters
	 */
	fun extrapolateLinear(
		scale: Float,
		startValue: Vector3,
		endValue: Vector3,
	): Vector3 {
//        if (scale <= 1f) {
//            return interpolateLinear(scale, startValue, endValue, store);
//        }
		val x = extrapolateLinear(scale, startValue.x, endValue.x)
		val y = extrapolateLinear(scale, startValue.y, endValue.y)
		val z = extrapolateLinear(scale, startValue.z, endValue.z)
		return Vector3(x, y, z)
	}

	/**
	 * Interpolate a spline between at least 4 control points following the
	 * Catmull-Rom equation. here is the interpolation matrix m = [ 0.0 1.0 0.0
	 * 0.0 ] [-T 0.0 T 0.0 ] [ 2T T-3 3-2T -T ] [-T 2-T T-2 T ] where T is the
	 * curve tension the result is a value between p1 and p2, t=0 for p1, t=1
	 * for p2
	 *
	 * @param u value from 0 to 1
	 * @param T The tension of the curve
	 * @param p0 control point 0
	 * @param p1 control point 1
	 * @param p2 control point 2
	 * @param p3 control point 3
	 * @return catmull-Rom interpolation
	 */
	fun interpolateCatmullRom(
		u: Float,
		T: Float,
		p0: Float,
		p1: Float,
		p2: Float,
		p3: Float,
	): Float {
		val c1 = p1
		val c2 = -1.0f * T * p0 + T * p2
		val c3 = 2 * T * p0 + (T - 3) * p1 + (3 - 2 * T) * p2 + -T * p3
		val c4 = -T * p0 + (2 - T) * p1 + (T - 2) * p2 + T * p3

		return (((c4 * u + c3) * u + c2) * u + c1)
	}

	/**
	 * Interpolate a spline between at least 4 control points following the
	 * Catmull-Rom equation. here is the interpolation matrix m = [ 0.0 1.0 0.0
	 * 0.0 ] [-T 0.0 T 0.0 ] [ 2T T-3 3-2T -T ] [-T 2-T T-2 T ] where T is the
	 * tension of the curve the result is a value between p1 and p2, t=0 for p1,
	 * t=1 for p2
	 *
	 * @param u value from 0 to 1
	 * @param T The tension of the curve
	 * @param p0 control point 0
	 * @param p1 control point 1
	 * @param p2 control point 2
	 * @param p3 control point 3
	 * @return catmull-Rom interpolation
	 */
	fun interpolateCatmullRom(
		u: Float,
		T: Float,
		p0: Vector3,
		p1: Vector3,
		p2: Vector3,
		p3: Vector3,
	): Vector3 {
		val x = interpolateCatmullRom(u, T, p0.x, p1.x, p2.x, p3.x)
		val y = interpolateCatmullRom(u, T, p0.y, p1.y, p2.y, p3.y)
		val z = interpolateCatmullRom(u, T, p0.z, p1.z, p2.z, p3.z)
		return Vector3(x, y, z)
	}

	/**
	 * Interpolate a spline between at least 4 control points following the
	 * Bezier equation. here is the interpolation matrix m = [ -1.0 3.0 -3.0 1.0
	 * ] [ 3.0 -6.0 3.0 0.0 ] [ -3.0 3.0 0.0 0.0 ] [ 1.0 0.0 0.0 0.0 ] where T
	 * is the curve tension the result is a value between p1 and p3, t=0 for p1,
	 * t=1 for p3
	 *
	 * @param u value from 0 to 1
	 * @param p0 control point 0
	 * @param p1 control point 1
	 * @param p2 control point 2
	 * @param p3 control point 3
	 * @return Bezier interpolation
	 */
	fun interpolateBezier(u: Float, p0: Float, p1: Float, p2: Float, p3: Float): Float {
		val oneMinusU = 1.0f - u
		val oneMinusU2 = oneMinusU * oneMinusU
		val u2 = u * u
		return p0 * oneMinusU2 * oneMinusU + 3.0f * p1 * u * oneMinusU2 + 3.0f * p2 * u2 * oneMinusU + p3 * u2 * u
	}

	/**
	 * Interpolate a spline between at least 4 control points following the
	 * Bezier equation. here is the interpolation matrix m = [ -1.0 3.0 -3.0 1.0
	 * ] [ 3.0 -6.0 3.0 0.0 ] [ -3.0 3.0 0.0 0.0 ] [ 1.0 0.0 0.0 0.0 ] where T
	 * is the tension of the curve the result is a value between p1 and p3, t=0
	 * for p1, t=1 for p3
	 *
	 * @param u value from 0 to 1
	 * @param p0 control point 0
	 * @param p1 control point 1
	 * @param p2 control point 2
	 * @param p3 control point 3
	 * @return Bezier interpolation
	 */
	fun interpolateBezier(
		u: Float,
		p0: Vector3,
		p1: Vector3,
		p2: Vector3,
		p3: Vector3,
	): Vector3 {
		val x = interpolateBezier(u, p0.x, p1.x, p2.x, p3.x)
		val y = interpolateBezier(u, p0.y, p1.y, p2.y, p3.y)
		val z = interpolateBezier(u, p0.z, p1.z, p2.z, p3.z)
		return Vector3(x, y, z)
	}

	/**
	 * Compute the length on a catmull rom spline between control point 1 and 2
	 *
	 * @param p0 control point 0
	 * @param p1 control point 1
	 * @param p2 control point 2
	 * @param p3 control point 3
	 * @param startRange the starting range on the segment (use 0)
	 * @param endRange the end range on the segment (use 1)
	 * @param curveTension the curve tension
	 * @return the length of the segment
	 */
	fun getCatmullRomP1toP2Length(
		p0: Vector3,
		p1: Vector3,
		p2: Vector3,
		p3: Vector3,
		startRange: Float,
		endRange: Float,
		curveTension: Float,
	): Float {
		val epsilon = 0.001f
		val middleValue = (startRange + endRange) * 0.5f
		var start = NULL
		var end = NULL
		if (startRange != 0f) {
			start = interpolateCatmullRom(startRange, curveTension, p0, p1, p2, p3)
		}
		if (endRange != 1f) {
			end = interpolateCatmullRom(endRange, curveTension, p0, p1, p2, p3)
		}
		val middle = interpolateCatmullRom(middleValue, curveTension, p0, p1, p2, p3)
		var l = end.minus(start).len()
		var l1 = middle.minus(start).len()
		var l2 = end.minus(middle).len()
		val len = l1 + l2
		if (l + epsilon < len) {
			l1 = getCatmullRomP1toP2Length(p0, p1, p2, p3, startRange, middleValue, curveTension)
			l2 = getCatmullRomP1toP2Length(p0, p1, p2, p3, middleValue, endRange, curveTension)
		}
		l = l1 + l2
		return l
	}

	/**
	 * Compute the length on a bezier spline between control point 1 and 2
	 *
	 * @param p0 control point 0
	 * @param p1 control point 1
	 * @param p2 control point 2
	 * @param p3 control point 3
	 * @return the length of the segment
	 */
	fun getBezierP1toP2Length(p0: Vector3, p1: Vector3, p2: Vector3, p3: Vector3): Float {
		var p0 = p0
		val delta = 0.02f
		var t = 0.0f
		var result = 0.0f
		while (t <= 1.0f) {
			val v2 = interpolateBezier(t, p0, p1, p2, p3)
			result += p0.minus(v2).len()
			p0 = v2
			t += delta
		}
		return result
	}

	fun fastInvSqrt(x: Float): Float {
		var x = x
		val xhalf = 0.5f * x
		var i = java.lang.Float.floatToIntBits(x) // get bits for floating value
		i = 0x5f375a86 - (i shr 1) // gives initial guess y0
		x = java.lang.Float.intBitsToFloat(i) // convert bits back to float
		x *= (1.5f - xhalf * x * x) // Newton step, repeating increases
		// accuracy
		return x
	}

	/**
	 * A method that computes normal for a triangle defined by three vertices.
	 *
	 * @param v1 first vertex
	 * @param v2 second vertex
	 * @param v3 third vertex
	 * @return a normal for the face
	 */
	fun computeNormal(v1: Vector3, v2: Vector3?, v3: Vector3): Vector3 {
		val a1 = v1.minus(v2!!)
		val a2 = v3.minus(v2)
		return a2.cross(a1).unit()
	}

	/**
	 * Returns the determinant of a 4x4 matrix.
	 */
	fun determinant(
		m00: Double,
		m01: Double,
		m02: Double,
		m03: Double,
		m10: Double,
		m11: Double,
		m12: Double,
		m13: Double,
		m20: Double,
		m21: Double,
		m22: Double,
		m23: Double,
		m30: Double,
		m31: Double,
		m32: Double,
		m33: Double,
	): Float {
		val det01 = m20 * m31 - m21 * m30
		val det02 = m20 * m32 - m22 * m30
		val det03 = m20 * m33 - m23 * m30
		val det12 = m21 * m32 - m22 * m31
		val det13 = m21 * m33 - m23 * m31
		val det23 = m22 * m33 - m23 * m32
		return (
			m00 * (m11 * det23 - m12 * det13 + m13 * det12) -
				m01
				* (m10 * det23 - m12 * det03 + m13 * det02) +
				m02
				* (m10 * det13 - m11 * det03 + m13 * det01) -
				m03
				* (m10 * det12 - m11 * det02 + m12 * det01)
			).toFloat()
	}

	/**
	 * Converts a point from Spherical coordinates to Cartesian (using positive
	 * Y as up) and stores the results in the store var.
	 */
	fun sphericalToCartesian(sphereCoords: Vector3): Vector3 {
		val y = sphereCoords.x * sin(sphereCoords.z)
		val a = sphereCoords.x * cos(sphereCoords.z)
		val x = a * cos(sphereCoords.y)
		val z = a * sin(sphereCoords.y)

		return Vector3(x, y, z)
	}

	/**
	 * Converts a point from Cartesian coordinates (using positive Y as up) to
	 * Spherical and stores the results in the store var. (Radius, Azimuth,
	 * Polar)
	 */
	fun cartesianToSpherical(cartCoords: Vector3): Vector3 {
		var x = cartCoords.x
		if (x == 0f) {
			x = FLT_EPSILON
		}
		val vx = sqrt(
			(x * x) +
				(cartCoords.y * cartCoords.y) +
				(cartCoords.z * cartCoords.z),
		)
		var vy = atan(cartCoords.z / x)
		if (x < 0) {
			vy += PI
		}
		val vz = asin(cartCoords.y / vx)
		return Vector3(vx, vy, vz)
	}

	/**
	 * Converts a point from Spherical coordinates to Cartesian (using positive
	 * Z as up) and stores the results in the store var.
	 */
	fun sphericalToCartesianZ(sphereCoords: Vector3): Vector3 {
		val z = sphereCoords.x * sin(sphereCoords.z)
		val a = sphereCoords.x * cos(sphereCoords.z)
		val x = a * cos(sphereCoords.y)
		val y = a * sin(sphereCoords.y)

		return Vector3(x, y, z)
	}

	/**
	 * Converts a point from Cartesian coordinates (using positive Z as up) to
	 * Spherical and stores the results in the store var. (Radius, Azimuth,
	 * Polar)
	 */
	fun cartesianZToSpherical(cartCoords: Vector3): Vector3 {
		var x = cartCoords.x
		if (x == 0f) {
			x = FLT_EPSILON
		}
		val vx = sqrt(
			(x * x) +
				(cartCoords.y * cartCoords.y) +
				(cartCoords.z * cartCoords.z),
		)
		var vz = atan(cartCoords.z / x)
		if (x < 0) {
			vz += PI
		}
		val vy = asin(cartCoords.y / vx)

		return Vector3(vx, vy, vz)
	}

	/**
	 * Takes an value and expresses it in terms of min to max.
	 *
	 * @param `val` - the angle to normalize (in radians)
	 * @return the normalized angle (also in radians)
	 */
	@JvmStatic
	fun normalize(value: Float, min: Float, max: Float): Float {
		var v = value
		if (v.isInfinite() || v.isNaN()) {
			return 0f
		}
		val range = max - min
		while (v > max) {
			v -= range
		}
		while (v < min) {
			v += range
		}
		return v
	}

	/**
	 * @param x the value whose sign is to be adjusted.
	 * @param y the value whose sign is to be used.
	 * @return x with its sign changed to match the sign of y.
	 */
	fun copysign(x: Float, y: Float): Float = if (y >= 0 && x <= -0) {
		-x
	} else if (y < 0 && x >= 0) {
		-x
	} else {
		x
	}

	/**
	 * Take a float input and clamp it between min and max.
	 *
	 * @param input
	 * @param min
	 * @param max
	 * @return clamped input
	 */
	@JvmStatic
	fun clamp(input: Float, min: Float, max: Float): Float = if ((input < min)) {
		min
	} else if ((input > max)) {
		max
	} else {
		input
	}

	/**
	 * Clamps the given float to be between 0 and 1.
	 *
	 * @param input
	 * @return input clamped between 0 and 1.
	 */
	fun saturate(input: Float): Float = clamp(input, 0f, 1f)

	/**
	 * Converts a single precision (32 bit) floating point value into half
	 * precision (16 bit).
	 *
	 *
	 *
	 * Source:
	 * [
 * http://www.fox-toolkit.org/ftp/fasthalffloatconversion.pdf](http://www.fox-toolkit.org/ftp/fasthalffloatconversion.pdf)<br></br>
	 * **broken link**
	 *
	 * @param half The half floating point value as a short.
	 * @return floating point value of the half.
	 */
	fun convertHalfToFloat(half: Short): Float = when (half.toInt()) {
		0x0000 -> 0f

		0x8000 -> -0f

		0x7c00 -> Float.POSITIVE_INFINITY

		0xfc00 -> Float.NEGATIVE_INFINITY

		else ->
			java.lang.Float
				.intBitsToFloat(
					((half.toInt() and 0x8000) shl 16)
						or (((half.toInt() and 0x7c00) + 0x1C000) shl 13)
						or ((half.toInt() and 0x03FF) shl 13),
				)
	}

	fun convertFloatToHalf(flt: Float): Short {
		if (flt.isNaN()) {
			throw UnsupportedOperationException("NaN to half conversion not supported!")
		} else if (flt == Float.POSITIVE_INFINITY) {
			return 0x7c00.toShort()
		} else if (flt == Float.NEGATIVE_INFINITY) {
			return 0xfc00.toShort()
		} else if (flt == 0f) {
			return 0x0000.toShort()
		} else if (flt == -0f) {
			return 0x8000.toShort()
		} else if (flt > 65504f) {
			// max value supported by half float
			return 0x7bff
		} else if (flt < -65504f) {
			return (0x7bff or 0x8000).toShort()
		} else if (flt > 0f && flt < 5.96046E-8f) {
			return 0x0001
		} else if (flt < 0f && flt > -5.96046E-8f) {
			return 0x8001.toShort()
		}

		val f = java.lang.Float.floatToIntBits(flt)
		return (
			((f shr 16) and 0x8000)
				or ((((f and 0x7f800000) - 0x38000000) shr 13) and 0x7c00)
				or ((f shr 13) and 0x03ff)
			).toShort()
	}
}
