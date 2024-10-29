@file:Suppress("ktlint", "unused")

package io.github.axisangles.ktmath

import kotlinx.serialization.Serializable
import kotlin.math.*

@JvmInline
@Serializable
value class Matrix3
@Suppress("ktlint") constructor(
	val xx: Float, val yx: Float, val zx: Float,
	val xy: Float, val yy: Float, val zy: Float,
	val xz: Float, val yz: Float, val zz: Float
) {
	companion object {
		val NULL = Matrix3(
			0f, 0f, 0f,
			0f, 0f, 0f,
			0f, 0f, 0f
		)
		val IDENTITY = Matrix3(
			1f, 0f, 0f,
			0f, 1f, 0f,
			0f, 0f, 1f
		)
	}

	/**
	 * creates a new matrix from x y and z column vectors
	 */
	constructor(x: Vector3, y: Vector3, z: Vector3) : this(
		x.x, y.x, z.x,
		x.y, y.y, z.y,
		x.z, y.z, z.z
	)

	// column getters
	val x get() = Vector3(xx, xy, xz)
	val y get() = Vector3(yx, yy, yz)
	val z get() = Vector3(zx, zy, zz)

	// row getters
	val xRow get() = Vector3(xx, yx, zx)
	val yRow get() = Vector3(xy, yy, zy)
	val zRow get() = Vector3(xz, yz, zz)

	operator fun component1(): Float = xx
	operator fun component2(): Float = yx
	operator fun component3(): Float = zx
	operator fun component4(): Float = xy
	operator fun component5(): Float = yy
	operator fun component6(): Float = zy
	operator fun component7(): Float = xz
	operator fun component8(): Float = yz
	operator fun component9(): Float = zz

	operator fun unaryMinus(): Matrix3 = Matrix3(
		-xx, -yx, -zx,
		-xy, -yy, -zy,
		-xz, -yz, -zz
	)

	operator fun plus(that: Matrix3): Matrix3 = Matrix3(
		this.xx + that.xx, this.yx + that.yx, this.zx + that.zx,
		this.xy + that.xy, this.yy + that.yy, this.zy + that.zy,
		this.xz + that.xz, this.yz + that.yz, this.zz + that.zz
	)

	operator fun minus(that: Matrix3): Matrix3 = Matrix3(
		this.xx - that.xx, this.yx - that.yx, this.zx - that.zx,
		this.xy - that.xy, this.yy - that.yy, this.zy - that.zy,
		this.xz - that.xz, this.yz - that.yz, this.zz - that.zz
	)

	operator fun times(that: Float): Matrix3 = Matrix3(
		this.xx * that, this.yx * that, this.zx * that,
		this.xy * that, this.yy * that, this.zy * that,
		this.xz * that, this.yz * that, this.zz * that
	)

	operator fun times(that: Vector3): Vector3 = Vector3(
		this.xx * that.x + this.yx * that.y + this.zx * that.z,
		this.xy * that.x + this.yy * that.y + this.zy * that.z,
		this.xz * that.x + this.yz * that.y + this.zz * that.z
	)

	operator fun times(that: Matrix3): Matrix3 = Matrix3(
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
	fun normSq(): Float =
		xx * xx + yx * yx + zx * zx +
			xy * xy + yy * yy + zy * zy +
			xz * xz + yz * yz + zz * zz

	/**
	 * computes the frobenius norm of this matrix
	 * @return the frobenius norm
	 */
	fun norm(): Float = sqrt(normSq())

	/**
	 * computes the determinant of this matrix
	 * @return the determinant
	 */
	fun det(): Float =
		(xz * yx - xx * yz) * zy +
			(xx * yy - xy * yx) * zz +
			(xy * yz - xz * yy) * zx

	/**
	 * computes the trace of this matrix
	 * @return the trace
	 */
	fun trace(): Float = xx + yy + zz

	/**
	 * computes the transpose of this matrix
	 * @return the transpose matrix
	 */
	fun transpose(): Matrix3 = Matrix3(
		xx, xy, xz,
		yx, yy, yz,
		zx, zy, zz
	)

	/**
	 * computes the inverse of this matrix
	 * @return the inverse matrix
	 */
	fun inv(): Matrix3 {
		val det = det()
		return Matrix3(
			(yy * zz - yz * zy) / det, (yz * zx - yx * zz) / det, (yx * zy - yy * zx) / det,
			(xz * zy - xy * zz) / det, (xx * zz - xz * zx) / det, (xy * zx - xx * zy) / det,
			(xy * yz - xz * yy) / det, (xz * yx - xx * yz) / det, (xx * yy - xy * yx) / det
		)
	}

	operator fun div(that: Float): Matrix3 = this * (1f / that)

	/**
	 * computes the right division, this * that^-1
	 */
	operator fun div(that: Matrix3): Matrix3 = this * that.inv()

	/**
	 * computes the inverse transpose of this matrix
	 * @return the inverse transpose matrix
	 */
	fun invTranspose(): Matrix3 {
		val det = det()
		return Matrix3(
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
	fun orthonormalize(): Matrix3 {
		if (this.det() <= 0f) { // maybe this doesn't have to be so
			throw Exception("Attempt to convert non-positive determinant matrix to rotation matrix")
		}

		var curMat = this
		var curDet = Float.POSITIVE_INFINITY

		for (i in 1..100) {
			val newMat = (curMat + curMat.invTranspose()) / 2f
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
	fun average(vararg others: ObjectMatrix3): Matrix3 {
		var count = 1f
		var sum = this
		others.forEach {
			count += 1f
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
	fun lerp(that: Matrix3, t: Float): Matrix3 = (1f - t) * this + t * that

	// assumes this matrix is orthonormal and converts this to a quaternion
	/**
	 * creates a quaternion representing the same rotation as this matrix,
	 * assuming the matrix is a rotation matrix
	 * @return the quaternion
	 */
	fun toQuaternionAssumingOrthonormal(): Quaternion {
		return if (yy > -zz && zz > -xx && xx > -yy) {
			Quaternion(1f + xx + yy + zz, yz - zy, zx - xz, xy - yx).unit()
		} else if (xx > yy && xx > zz) {
			Quaternion(yz - zy, 1f + xx - yy - zz, xy + yx, xz + zx).unit()
		} else if (yy > zz) {
			Quaternion(zx - xz, xy + yx, 1f - xx + yy - zz, yz + zy).unit()
		} else {
			Quaternion(xy - yx, xz + zx, yz + zy, 1f - xx - yy + zz).unit()
		}
	}

	// orthogonalizes the matrix then returns the quaternion
	/**
	 * creates a quaternion representing the same rotation as this matrix
	 * @return the quaternion
	 */
	fun toQuaternion(): Quaternion = orthonormalize().toQuaternionAssumingOrthonormal()

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
			if (zy*zy + zz*zz > 0f) {

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
	fun toEulerAnglesAssumingOrthonormal(order: EulerOrder): EulerAngles {
		val ETA = 1.5707964f
		when (order) {
			EulerOrder.XYZ -> {
				val kc = sqrt(zy * zy + zz * zz)
				if (kc < 1e-7f) {
					return EulerAngles(
						EulerOrder.XYZ,
						atan2(yz, yy),
						ETA.withSign(zx),
						0f
					)
				}

				return EulerAngles(
					EulerOrder.XYZ,
					atan2(-zy, zz),
					atan2(zx, kc),
					atan2(xy * zz - xz * zy, yy * zz - yz * zy)
				)
			}
			EulerOrder.YZX -> {
				val kc = sqrt(xx * xx + xz * xz)
				if (kc < 1e-7f) {
					return EulerAngles(
						EulerOrder.YZX,
						0f,
						atan2(zx, zz),
						ETA.withSign(xy)
					)
				}

				return EulerAngles(
					EulerOrder.YZX,
					atan2(xx * yz - xz * yx, xx * zz - xz * zx),
					atan2(-xz, xx),
					atan2(xy, kc)
				)
			}
			EulerOrder.ZXY -> {
				val kc = sqrt(yy * yy + yx * yx)
				if (kc < 1e-7f) {
					return EulerAngles(
						EulerOrder.ZXY,
						ETA.withSign(yz),
						0f,
						atan2(xy, xx)
					)
				}

				return EulerAngles(
					EulerOrder.ZXY,
					atan2(yz, kc),
					atan2(yy * zx - yx * zy, yy * xx - yx * xy),
					atan2(-yx, yy)
				)
			}
			EulerOrder.ZYX -> {
				val kc = sqrt(xy * xy + xx * xx)
				if (kc < 1e-7f) {
					return EulerAngles(
						EulerOrder.ZYX,
						0f,
						ETA.withSign(-xz),
						atan2(-yx, yy)
					)
				}

				return EulerAngles(
					EulerOrder.ZYX,
					atan2(zx * xy - zy * xx, yy * xx - yx * xy),
					atan2(-xz, kc),
					atan2(xy, xx)
				)
			}

			EulerOrder.YXZ -> {
				val kc = sqrt(zx * zx + zz * zz)
				if (kc < 1e-7f) {
					return EulerAngles(
						EulerOrder.YXZ,
						ETA.withSign(-zy),
						atan2(-xz, xx),
						0f
					)
				}

				return EulerAngles(
					EulerOrder.YXZ,
					atan2(-zy, kc),
					atan2(zx, zz),
					atan2(yz * zx - yx * zz, xx * zz - xz * zx)
				)
			}
			EulerOrder.XZY -> {
				val kc = sqrt(yz * yz + yy * yy)
				if (kc < 1e-7f) {
					return EulerAngles(
						EulerOrder.XZY,
						atan2(-zy, zz),
						0f,
						ETA.withSign(-yx)
					)
				}

				return EulerAngles(
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
	fun toEulerAngles(order: EulerOrder): EulerAngles =
		orthonormalize().toEulerAnglesAssumingOrthonormal(order)

	fun toObject() = ObjectMatrix3(xx, yx, zx, xy, yy, zy, xz, yz, zz)
}

data class ObjectMatrix3(
	val xx: Float, val yx: Float, val zx: Float,
	val xy: Float, val yy: Float, val zy: Float,
	val xz: Float, val yz: Float, val zz: Float
) {
	fun toValue() = Matrix3(xx, yx, zx, xy, yy, zy, xz, yz, zz)
}

operator fun Float.times(that: Matrix3): Matrix3 = that * this

operator fun Float.div(that: Matrix3): Matrix3 = that.inv() * this