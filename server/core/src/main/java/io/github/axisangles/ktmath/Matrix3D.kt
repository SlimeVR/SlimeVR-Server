@file:Suppress("ktlint", "unused")

package io.github.axisangles.ktmath

import kotlinx.serialization.Serializable
import kotlin.math.*

@JvmInline
@Serializable
value class Matrix3D
@Suppress("ktlint") constructor(
	val xx: Double, val yx: Double, val zx: Double,
	val xy: Double, val yy: Double, val zy: Double,
	val xz: Double, val yz: Double, val zz: Double
) {
	companion object {
		val NULL = Matrix3D(
			0.0, 0.0, 0.0,
			0.0, 0.0, 0.0,
			0.0, 0.0, 0.0
		)
		val IDENTITY = Matrix3D(
			1.0, 0.0, 0.0,
			0.0, 1.0, 0.0,
			0.0, 0.0, 1.0
		)
	}

	/**
	 * creates a new matrix from x y and z column vectors
	 */
	constructor(x: Vector3D, y: Vector3D, z: Vector3D) : this(
		x.x, y.x, z.x,
		x.y, y.y, z.y,
		x.z, y.z, z.z
	)

	// column getters
	val x get() = Vector3D(xx, xy, xz)
	val y get() = Vector3D(yx, yy, yz)
	val z get() = Vector3D(zx, zy, zz)

	// row getters
	val xRow get() = Vector3D(xx, yx, zx)
	val yRow get() = Vector3D(xy, yy, zy)
	val zRow get() = Vector3D(xz, yz, zz)

	operator fun component1(): Double = xx
	operator fun component2(): Double = yx
	operator fun component3(): Double = zx
	operator fun component4(): Double = xy
	operator fun component5(): Double = yy
	operator fun component6(): Double = zy
	operator fun component7(): Double = xz
	operator fun component8(): Double = yz
	operator fun component9(): Double = zz

	operator fun unaryMinus(): Matrix3D = Matrix3D(
		-xx, -yx, -zx,
		-xy, -yy, -zy,
		-xz, -yz, -zz
	)

	operator fun plus(that: Matrix3D): Matrix3D = Matrix3D(
		this.xx + that.xx, this.yx + that.yx, this.zx + that.zx,
		this.xy + that.xy, this.yy + that.yy, this.zy + that.zy,
		this.xz + that.xz, this.yz + that.yz, this.zz + that.zz
	)

	operator fun minus(that: Matrix3D): Matrix3D = Matrix3D(
		this.xx - that.xx, this.yx - that.yx, this.zx - that.zx,
		this.xy - that.xy, this.yy - that.yy, this.zy - that.zy,
		this.xz - that.xz, this.yz - that.yz, this.zz - that.zz
	)

	operator fun times(that: Double): Matrix3D = Matrix3D(
		this.xx * that, this.yx * that, this.zx * that,
		this.xy * that, this.yy * that, this.zy * that,
		this.xz * that, this.yz * that, this.zz * that
	)

	operator fun times(that: Vector3D): Vector3D = Vector3D(
		this.xx * that.x + this.yx * that.y + this.zx * that.z,
		this.xy * that.x + this.yy * that.y + this.zy * that.z,
		this.xz * that.x + this.yz * that.y + this.zz * that.z
	)

	operator fun times(that: Matrix3D): Matrix3D = Matrix3D(
		this.xx * that.xx + this.yx * that.xy + this.zx * that.xz,
		this.xx * that.yx + this.yx * that.yy + this.zx * that.yz,
		this.xx * that.zx + this.yx * that.zy + this.zx * that.zz,
		this.xy * that.xx + this.yy * that.xy + this.zy * that.xz,
		this.xy * that.yx + this.yy * that.yy + this.zy * that.yz,
		this.xy * that.zx + this.yy * that.zy + this.zy * that.zz,
		this.xz * that.xx + this.yz * that.xy + this.zz * that.xz,
		this.xz * that.yx + this.yz * that.yy + this.zz * that.yz,
		this.xz * that.zx + this.yz * that.zy + this.zz * that.zz
	)

	/**
	 * computes the square of the frobenius norm of this matrix
	 * @return the frobenius norm squared
	 */
	fun normSq(): Double =
		xx * xx + yx * yx + zx * zx +
			xy * xy + yy * yy + zy * zy +
			xz * xz + yz * yz + zz * zz

	/**
	 * computes the frobenius norm of this matrix
	 * @return the frobenius norm
	 */
	fun norm(): Double = sqrt(normSq())

	/**
	 * computes the determinant of this matrix
	 * @return the determinant
	 */
	fun det(): Double =
		(xz * yx - xx * yz) * zy +
			(xx * yy - xy * yx) * zz +
			(xy * yz - xz * yy) * zx

	/**
	 * computes the trace of this matrix
	 * @return the trace
	 */
	fun trace(): Double = xx + yy + zz

	/**
	 * computes the transpose of this matrix
	 * @return the transpose matrix
	 */
	fun transpose(): Matrix3D = Matrix3D(
		xx, xy, xz,
		yx, yy, yz,
		zx, zy, zz
	)

	/**
	 * computes the inverse of this matrix
	 * @return the inverse matrix
	 */
	fun inv(): Matrix3D {
		val det = det()
		return Matrix3D(
			(yy * zz - yz * zy) / det, (yz * zx - yx * zz) / det, (yx * zy - yy * zx) / det,
			(xz * zy - xy * zz) / det, (xx * zz - xz * zx) / det, (xy * zx - xx * zy) / det,
			(xy * yz - xz * yy) / det, (xz * yx - xx * yz) / det, (xx * yy - xy * yx) / det
		)
	}

	operator fun div(that: Double): Matrix3D = this * (1.0 / that)

	/**
	 * computes the right division, this * that^-1
	 */
	operator fun div(that: Matrix3D): Matrix3D = this * that.inv()

	/**
	 * computes the inverse transpose of this matrix
	 * @return the inverse transpose matrix
	 */
	fun invTranspose(): Matrix3D {
		val det = det()
		return Matrix3D(
			(yy * zz - yz * zy) / det, (xz * zy - xy * zz) / det, (xy * yz - xz * yy) / det,
			(yz * zx - yx * zz) / det, (xx * zz - xz * zx) / det, (xz * yx - xx * yz) / det,
			(yx * zy - yy * zx) / det, (xy * zx - xx * zy) / det, (xx * yy - xy * yx) / det
		)
	}

	/*
		The following method returns the best guess rotation matrix.
		In general, a square matrix can be represented as an
		orthogonal matrix * symmetric matrix.
			M = O*S
		A symmetric matrix's transpose is itself.
		An orthogonal matrix's transpose is its inverse.
			S^T = S
			O^T = O^-1
		If we perform the following process, we can factor out O.
			M + M^-T
			= O*S + (O*S)^-T
			= O*S + O^-T*S^-T
			= O*S + O*S^-T
			= O*(S + S^-T)
		So we see if we perform M + M^-T, the rotation, O, remains unchanged.
		Iterating M = (M + M^-T)/2, we converge the symmetric part to identity.

		This converges exponentially (one digit per iteration) when it is far from a
		rotation matrix, and quadratically (double the digits each iteration) when it
		is close to a rotation matrix.
	 */
	/**
	 * computes the nearest orthonormal matrix to this matrix
	 * @return the rotation matrix
	 */
	fun orthonormalize(): Matrix3D {
		if (this.det() <= 0.0) { // maybe this doesn't have to be so
			throw Exception("Attempt to convert non-positive determinant matrix to rotation matrix")
		}

		var curMat = this
		var curDet = Double.POSITIVE_INFINITY

		for (i in 1..100) {
			val newMat = (curMat + curMat.invTranspose()) / 2.0
			val newDet = abs(newMat.det())
			// should almost always exit immediately
			if (newDet >= curDet) return curMat
			if (newDet <= 1.0000001f) return newMat
			curMat = newMat
			curDet = newDet
		}

		return curMat
	}

	/**
	 * finds the rotation matrix closest to all given rotation matrices.
	 * multiply input matrices by a weight for weighted averaging.
	 * WARNING: NOT ANGULAR
	 * @param others a variable number of additional boxed matrices to average
	 * @return the average rotation matrix
	 */
	fun average(vararg others: ObjectMatrix3D): Matrix3D {
		var count = 1.0
		var sum = this
		others.forEach {
			count += 1.0
			sum += it.toValue()
		}
		return (sum / count).orthonormalize()
	}

	/**
	 * linearly interpolates this matrix to that matrix by t
	 * @param that the matrix towards which to interpolate
	 * @param t the amount by which to interpolate
	 * @return the interpolated matrix
	 */
	fun lerp(that: Matrix3D, t: Double): Matrix3D = (1.0 - t) * this + t * that

	// assumes this matrix is orthonormal and converts this to a quaternion
	/**
	 * creates a quaternion representing the same rotation as this matrix,
	 * assuming the matrix is a rotation matrix
	 * @return the quaternion
	 */
	fun toQuaternionAssumingOrthonormal(): QuaternionD {
		return if (yy > -zz && zz > -xx && xx > -yy) {
			QuaternionD(1.0 + xx + yy + zz, yz - zy, zx - xz, xy - yx).unit()
		} else if (xx > yy && xx > zz) {
			QuaternionD(yz - zy, 1.0 + xx - yy - zz, xy + yx, xz + zx).unit()
		} else if (yy > zz) {
			QuaternionD(zx - xz, xy + yx, 1.0 - xx + yy - zz, yz + zy).unit()
		} else {
			QuaternionD(xy - yx, xz + zx, yz + zy, 1.0 - xx - yy + zz).unit()
		}
	}

	// orthogonalizes the matrix then returns the quaternion
	/**
	 * creates a quaternion representing the same rotation as this matrix
	 * @return the quaternion
	 */
	fun toQuaternion(): QuaternionD = orthonormalize().toQuaternionAssumingOrthonormal()

	/*
		the standard algorithm:

		yAng = asin(clamp(zx, -1, 1))
		if (abs(zx) < 0.9999999f) {
			xAng = atan2(-zy, zz)
			zAng = atan2(-yx, xx)
		} else {
			xAng = atan2(yz, yy)
			zAng = 0
		}



		problems with the standard algorithm:

	1)
			yAng = asin(clamp(zx, -1, 1))

	FIX:
			yAng = atan2(zx, sqrt(zy*zy + zz*zz))

		this loses many bits of accuracy when near the singularity, zx = +-1 and
		can cause the algorithm to return completely inaccurate results with only
		small floating point errors in the matrix. this happens because zx is
		NOT sin(pitch), but rather errorTerm*sin(pitch), and small changes in zx
		when zx is near +-1 make large changes in asin(zx).



	2)
			if (abs(zx) < 0.9999999f) {

	FIX:
			if (zy*zy + zz*zz > 0.0) {

		this clause, meant to reduce the inaccuracy of the code following does
		not actually test for the condition that makes the following atans unstable.
		that is, when (zy, zz) and (yx, xx) are near 0.
		after several matrix multiplications, the error term is expected to be
		larger than 0.0000001. Often times, this clause will not catch the conditions
		it is trying to catch.



	3)
			zAng = atan2(-yx, xx)

	FIX:
			zAng = atan2(xy*zz - xz*zy, yy*zz - yz*zy)

		xAng and zAng are being computed separately. In the case of near singularity
		the angles of xAng and zAng are effectively added together as they represent
		the same operation (a rotation about the global y-axis). When computed
		separately, it is not guaranteed that the xAng + zAng add together to give
		the actual final rotation about the global y-axis.



	4)
		after many matrix operations are performed, without orthonormalization
		the matrix will contain floating point errors that will throw off the
		accuracy of any euler angles algorithm. orthonormalization should be
		built into the prerequisites for this function
	 */

	/**
	 * creates an eulerAngles representing the same rotation as this matrix,
	 * assuming the matrix is a rotation matrix
	 * @return the eulerAngles
	 */
	fun toEulerAnglesAssumingOrthonormal(order: EulerOrder): EulerAnglesD {
		val ETA = 1.5707964
		when (order) {
			EulerOrder.XYZ -> {
				val kc = sqrt(zy * zy + zz * zz)
				if (kc < 1e-7f) {
					return EulerAnglesD(
						EulerOrder.XYZ,
						atan2(yz, yy),
						ETA.withSign(zx),
						0.0
					)
				}

				return EulerAnglesD(
					EulerOrder.XYZ,
					atan2(-zy, zz),
					atan2(zx, kc),
					atan2(xy * zz - xz * zy, yy * zz - yz * zy)
				)
			}
			EulerOrder.YZX -> {
				val kc = sqrt(xx * xx + xz * xz)
				if (kc < 1e-7f) {
					return EulerAnglesD(
						EulerOrder.YZX,
						0.0,
						atan2(zx, zz),
						ETA.withSign(xy)
					)
				}

				return EulerAnglesD(
					EulerOrder.YZX,
					atan2(xx * yz - xz * yx, xx * zz - xz * zx),
					atan2(-xz, xx),
					atan2(xy, kc)
				)
			}
			EulerOrder.ZXY -> {
				val kc = sqrt(yy * yy + yx * yx)
				if (kc < 1e-7f) {
					return EulerAnglesD(
						EulerOrder.ZXY,
						ETA.withSign(yz),
						0.0,
						atan2(xy, xx)
					)
				}

				return EulerAnglesD(
					EulerOrder.ZXY,
					atan2(yz, kc),
					atan2(yy * zx - yx * zy, yy * xx - yx * xy),
					atan2(-yx, yy)
				)
			}
			EulerOrder.ZYX -> {
				val kc = sqrt(xy * xy + xx * xx)
				if (kc < 1e-7f) {
					return EulerAnglesD(
						EulerOrder.ZYX,
						0.0,
						ETA.withSign(-xz),
						atan2(-yx, yy)
					)
				}

				return EulerAnglesD(
					EulerOrder.ZYX,
					atan2(zx * xy - zy * xx, yy * xx - yx * xy),
					atan2(-xz, kc),
					atan2(xy, xx)
				)
			}

			EulerOrder.YXZ -> {
				val kc = sqrt(zx * zx + zz * zz)
				if (kc < 1e-7f) {
					return EulerAnglesD(
						EulerOrder.YXZ,
						ETA.withSign(-zy),
						atan2(-xz, xx),
						0.0
					)
				}

				return EulerAnglesD(
					EulerOrder.YXZ,
					atan2(-zy, kc),
					atan2(zx, zz),
					atan2(yz * zx - yx * zz, xx * zz - xz * zx)
				)
			}
			EulerOrder.XZY -> {
				val kc = sqrt(yz * yz + yy * yy)
				if (kc < 1e-7f) {
					return EulerAnglesD(
						EulerOrder.XZY,
						atan2(-zy, zz),
						0.0,
						ETA.withSign(-yx)
					)
				}

				return EulerAnglesD(
					EulerOrder.XZY,
					atan2(yz, yy),
					atan2(xy * yz - xz * yy, zz * yy - zy * yz),
					atan2(-yx, kc)
				)
			}
		}
	}

	// orthogonalizes the matrix then returns the euler angles
	/**
	 * creates an eulerAngles representing the same rotation as this matrix
	 * @return the eulerAngles
	 */
	fun toEulerAngles(order: EulerOrder): EulerAnglesD =
		orthonormalize().toEulerAnglesAssumingOrthonormal(order)

	fun toObject() = ObjectMatrix3D(xx, yx, zx, xy, yy, zy, xz, yz, zz)
}

data class ObjectMatrix3D(
	val xx: Double, val yx: Double, val zx: Double,
	val xy: Double, val yy: Double, val zy: Double,
	val xz: Double, val yz: Double, val zz: Double
) {
	fun toValue() = Matrix3D(xx, yx, zx, xy, yy, zy, xz, yz, zz)
}

operator fun Double.times(that: Matrix3D): Matrix3D = that * this

operator fun Double.div(that: Matrix3D): Matrix3D = that.inv() * this
