@file:Suppress("unused")

package io.github.axisangles.ktmath

import kotlinx.serialization.Serializable
import kotlin.math.*

@JvmInline
@Serializable
value class QuaternionD(val w: Double, val x: Double, val y: Double, val z: Double) {
	companion object {
		val NULL = QuaternionD(0.0, 0.0, 0.0, 0.0)
		val IDENTITY = QuaternionD(1.0, 0.0, 0.0, 0.0)
		val I = QuaternionD(0.0, 1.0, 0.0, 0.0)
		val J = QuaternionD(0.0, 0.0, 1.0, 0.0)
		val K = QuaternionD(0.0, 0.0, 0.0, 1.0)

		/**
		 * SlimeVR-specific constants and utils
		 */
		val SLIMEVR: SlimeVR = SlimeVR

		/**
		 * Used to rotate an identity quaternion to face upwards for [twinExtendedBack].
		 */
		private val UP_ADJ = QuaternionD(0.707, -0.707, 0.0, 0.0)

		/**
		 * creates a new quaternion representing the rotation about v's axis
		 * by an angle of v's length
		 * @param v the rotation vector
		 * @return the new quaternion
		 **/
		fun fromRotationVector(v: Vector3D): QuaternionD = QuaternionD(0.0, v / 2.0).exp()

		/**
		 * creates a new quaternion representing the rotation about axis v
		 * by an angle of v's length
		 * @param vx the rotation vector's x component
		 * @param vy the rotation vector's y component
		 * @param vz the rotation vector's z component
		 * @return the new quaternion
		 **/
		fun fromRotationVector(vx: Double, vy: Double, vz: Double): QuaternionD = fromRotationVector(Vector3D(vx, vy, vz))

		/**
		 * finds Q, the smallest-angled quaternion whose local u direction aligns with
		 * the global v direction.
		 * @param u the local direction
		 * @param v the global direction
		 * @return Q
		 **/
		fun fromTo(u: Vector3D, v: Vector3D): QuaternionD {
			val u = QuaternionD(0.0, u)
			val v = QuaternionD(0.0, v)
			val d = v / u

			return (d + d.len()).unit()
		}

		/**
		 * Rotation around X-axis
		 *
		 * Derived from the axis-angle representation in
		 * https://en.wikipedia.org/wiki/Axis%E2%80%93angle_representation#Unit_quaternions
		 */
		fun rotationAroundXAxis(angle: Double): QuaternionD = QuaternionD(cos(angle / 2.0), sin(angle / 2.0), 0.0, 0.0)

		/**
		 * Rotation around Y-axis
		 *
		 * Derived from the axis-angle representation in
		 * https://en.wikipedia.org/wiki/Axis%E2%80%93angle_representation#Unit_quaternions
		 */
		fun rotationAroundYAxis(angle: Double): QuaternionD = QuaternionD(cos(angle / 2.0), 0.0, sin(angle / 2.0), 0.0)

		/**
		 * Rotation around Z-axis
		 *
		 * Derived from the axis-angle representation in
		 * https://en.wikipedia.org/wiki/Axis%E2%80%93angle_representation#Unit_quaternions
		 */
		fun rotationAroundZAxis(angle: Double): QuaternionD = QuaternionD(cos(angle / 2.0), 0.0, 0.0, sin(angle / 2.0))

		/**
		 * SlimeVR-specific constants and utils
		 */
		object SlimeVR {
			val FRONT = QuaternionD(0.0, 0.0, 1.0, 0.0)
			val FRONT_LEFT = QuaternionD(0.383, 0.0, 0.924, 0.0)
			val LEFT = QuaternionD(0.707, 0.0, 0.707, 0.0)
			val BACK_LEFT = QuaternionD(0.924, 0.0, 0.383, 0.0)
			val FRONT_RIGHT = QuaternionD(0.383, 0.0, -0.924, 0.0)
			val RIGHT = QuaternionD(0.707, 0.0, -0.707, 0.0)
			val BACK_RIGHT = QuaternionD(0.924, 0.0, -0.383, 0.0)
			val BACK = QuaternionD(1.0, 0.0, 0.0, 0.0)
		}
	}

	/**
	 * @return the quaternion with w real component and xyz imaginary components
	 */
	constructor(w: Double, xyz: Vector3D) : this(w, xyz.x, xyz.y, xyz.z)

	/**
	 * @return the imaginary components as a vector3
	 **/
	val xyz get(): Vector3D = Vector3D(x, y, z)

	/**
	 * @return the quaternion with only the w component
	 **/
	val re get(): QuaternionD = QuaternionD(w, 0.0, 0.0, 0.0)

	/**
	 * @return the quaternion with only x y z components
	 **/
	val im get(): QuaternionD = QuaternionD(0.0, x, y, z)

	operator fun unaryMinus(): QuaternionD = QuaternionD(-w, -x, -y, -z)

	operator fun plus(that: QuaternionD): QuaternionD = QuaternionD(
		this.w + that.w,
		this.x + that.x,
		this.y + that.y,
		this.z + that.z,
	)

	operator fun plus(that: Double): QuaternionD = QuaternionD(this.w + that, this.x, this.y, this.z)

	operator fun minus(that: QuaternionD): QuaternionD = QuaternionD(
		this.w - that.w,
		this.x - that.x,
		this.y - that.y,
		this.z - that.z,
	)

	operator fun minus(that: Double): QuaternionD = QuaternionD(this.w - that, this.x, this.y, this.z)

	/**
	 * computes the dot product of this quaternion with that quaternion
	 * @param that the quaternion with which to be dotted
	 * @return the dot product between quaternions
	 **/
	fun dot(that: QuaternionD): Double = this.w * that.w + this.x * that.x + this.y * that.y + this.z * that.z

	/**
	 * computes the square of the length of this quaternion
	 * @return the length squared
	 **/
	fun lenSq(): Double = w * w + x * x + y * y + z * z

	/**
	 * computes the length of this quaternion
	 * @return the length
	 **/
	fun len(): Double = sqrt(w * w + x * x + y * y + z * z)

	/**
	 * @return the normalized quaternion
	 **/
	fun unit(): QuaternionD {
		val m = len()
		return if (m == 0.0) NULL else (this / m)
	}

	operator fun times(that: Double): QuaternionD = QuaternionD(
		this.w * that,
		this.x * that,
		this.y * that,
		this.z * that,
	)

	operator fun times(that: QuaternionD): QuaternionD = QuaternionD(
		this.w * that.w - this.x * that.x - this.y * that.y - this.z * that.z,
		this.x * that.w + this.w * that.x - this.z * that.y + this.y * that.z,
		this.y * that.w + this.z * that.x + this.w * that.y - this.x * that.z,
		this.z * that.w - this.y * that.x + this.x * that.y + this.w * that.z,
	)

	/**
	 * computes the inverse of this quaternion
	 * @return the inverse quaternion
	 **/
	fun inv(): QuaternionD {
		val lenSq = lenSq()
		return QuaternionD(
			w / lenSq,
			-x / lenSq,
			-y / lenSq,
			-z / lenSq,
		)
	}

	operator fun div(that: Double): QuaternionD = this * (1f / that)

	/**
	 * computes right division, this * that^-1
	 **/
	operator fun div(that: QuaternionD): QuaternionD = this * that.inv()

	operator fun component1(): Double = w
	operator fun component2(): Double = x
	operator fun component3(): Double = y
	operator fun component4(): Double = z

	/**
	 * @return the conjugate of this quaternion
	 **/
	fun conj(): QuaternionD = QuaternionD(w, -x, -y, -z)

	/**
	 * computes the logarithm of this quaternion
	 * @return the log of this quaternion
	 **/
	fun log(): QuaternionD {
		val co = w
		val si = xyz.len()
		val len = len()

		if (si == 0.0) {
			return QuaternionD(ln(len), xyz / w)
		}

		val ang = atan2(si, co)
		return QuaternionD(ln(len), ang / si * xyz)
	}

	/**
	 * raises e to the power of this quaternion
	 * @return the exponentiated quaternion
	 **/
	fun exp(): QuaternionD {
		val ang = xyz.len()
		val len = exp(w)

		if (ang == 0.0) {
			return QuaternionD(len, len * xyz)
		}

		val co = cos(ang)
		val si = sin(ang)
		return QuaternionD(len * co, len * si / ang * xyz)
	}

	/**
	 * raises this quaternion to the power of t
	 * @param t the power by which to raise this quaternion
	 * @return the powered quaternion
	 **/
	fun pow(t: Double): QuaternionD = (log() * t).exp()

	/**
	 * between this and -this, picks the one nearest to that quaternion
	 * @param that the quaternion to be nearest to
	 * @return nearest quaternion
	 **/
	fun twinNearest(that: QuaternionD): QuaternionD = if (this.dot(that) < 0.0) -this else this

	/**
	 * between this and -this, picks the one furthest from that quaternion
	 * @param that the quaternion to be furthest from
	 * @return furthest quaternion
	 **/
	fun twinFurthest(that: QuaternionD): QuaternionD = if (this.dot(that) < 0.0) this else -this

	/**
	 * Similar to [twinNearest], but offset so the lower back quadrant is the furthest
	 * rotation relative to [that]. This is useful for joints that have limited forward
	 * rotation, but extensive backward rotation.
	 * @param that The reference quaternion to be nearest to or furthest from.
	 * @return The furthest quaternion if in the lower back quadrant, otherwise the
	 * nearest quaternion.
	 **/
	fun twinExtendedBack(that: QuaternionD): QuaternionD {
		/*
		 * This handles the thigh extending behind the torso to face downwards, and the
		 * hip extending behind the chest. The thigh cannot bend to the back away from
		 * the torso and the spine hopefully can't bend back that far, so we can fairly
		 * safely assume the rotation is towards the torso.
		 */
		return this.twinNearest(that * UP_ADJ)
	}

	/**
	 * interpolates from this quaternion to that quaternion by t in quaternion space
	 * @param that the quaternion to interpolate to
	 * @param t the amount to interpolate
	 * @return interpolated quaternion
	 **/
	fun interpQ(that: QuaternionD, t: Double) = if (t == 0.0) {
		this
	} else if (t == 1.0) {
		that
	} else if (t < 0.5f) {
		(that / this).pow(t) * this
	} else {
		(this / that).pow(1f - t) * that
	}

	/**
	 * interpolates from this quaternion to that quaternion by t in rotation space
	 * @param that the quaternion to interpolate to
	 * @param t the amount to interpolate
	 * @return interpolated quaternion
	 **/
	fun interpR(that: QuaternionD, t: Double) = this.interpQ(that.twinNearest(this), t)

	/**
	 * linearly interpolates from this quaternion to that quaternion by t in
	 * quaternion space
	 * @param that the quaternion to interpolate to
	 * @param t the amount to interpolate
	 * @return interpolated quaternion
	 **/
	fun lerpQ(that: QuaternionD, t: Double): QuaternionD = (1f - t) * this + t * that

	/**
	 * linearly interpolates from this quaternion to that quaternion by t in
	 * rotation space
	 * @param that the quaternion to interpolate to
	 * @param t the amount to interpolate
	 * @return interpolated quaternion
	 **/
	fun lerpR(that: QuaternionD, t: Double) = this.lerpQ(that.twinNearest(this), t)

	/**
	 * computes this quaternion's angle to identity in quaternion space
	 * @return angle
	 **/
	fun angleQ(): Double = atan2(xyz.len(), w)

	/**
	 * computes this quaternion's angle to identity in rotation space
	 * @return angle
	 **/
	fun angleR(): Double = 2.0 * atan2(xyz.len(), abs(w))

	/**
	 * computes the angle between this quaternion and that quaternion in quaternion space
	 * @param that the other quaternion
	 * @return angle
	 **/
	fun angleToQ(that: QuaternionD): Double = (this / that).angleQ()

	/**
	 * computes the angle between this quaternion and that quaternion in rotation space
	 * @param that the other quaternion
	 * @return angle
	 **/
	fun angleToR(that: QuaternionD): Double = (this / that).angleR()

	/**
	 * computes the angle this quaternion rotates about the u axis in quaternion space
	 * @param u the axis
	 * @return angle
	 **/
	fun angleAboutQ(u: Vector3D): Double {
		val si = u.dot(xyz)
		val co = u.len() * w
		return atan2(si, co)
	}

	/**
	 * computes the angle this quaternion rotates about the u axis in rotation space
	 * @param u the axis
	 * @return angle
	 **/
	fun angleAboutR(u: Vector3D): Double = 2.0 * twinNearest(IDENTITY).angleAboutQ(u)

	/**
	 * finds Q, the quaternion nearest to this quaternion representing a rotation purely
	 * about the global u axis. Q is NOT unitized
	 * @param v the global axis
	 * @return Q
	 **/
	fun project(v: Vector3D) = QuaternionD(w, xyz.dot(v) / v.lenSq() * v)

	/**
	 * finds Q, the quaternion nearest to this quaternion representing a rotation NOT
	 * on the global u axis. Q is NOT unitized
	 * @param v the global axis
	 * @return Q
	 **/
	fun reject(v: Vector3D) = QuaternionD(w, v.cross(xyz).cross(v) / v.lenSq())

	/**
	 * finds Q, the quaternion nearest to this quaternion whose local u direction aligns
	 * with the global v direction. Q is NOT unitized
	 * @param u the local direction
	 * @param v the global direction
	 * @return Q
	 **/
	fun align(u: Vector3D, v: Vector3D): QuaternionD {
		val u = QuaternionD(0.0, u)
		val v = QuaternionD(0.0, v)

		return (v * this / u + (v / u).len() * this) / 2.0
	}

	/**
	 * Produces angles such that
	 * QuaternionD.fromRotationVector(angles[0]*axisA.unit()) * QuaternionD.fromRotationVector(angles[1]*axisB.unit())
	 * is as close to rot as possible
	 */
	fun biAlign(rot: QuaternionD, axisA: Vector3D, axisB: Vector3D): DoubleArray {
		val a = axisA.unit()
		val b = axisB.unit()

		val aQ = a.dot(rot.xyz)
		val bQ = b.dot(rot.xyz)
		val abQ = a.cross(b).dot(rot.xyz) - a.dot(b) * rot.w

		val angleA = atan2(2.0 * (abQ * bQ + aQ * rot.w), rot.w * rot.w - aQ * aQ + bQ * bQ - abQ * abQ)
		val angleB = atan2(2.0 * (abQ * aQ + bQ * rot.w), rot.w * rot.w + aQ * aQ - bQ * bQ - abQ * abQ)

		return doubleArrayOf(angleA, angleB)
	}

	/**
	 * applies this quaternion's rotation to that vector
	 * @param that the vector to be transformed
	 * @return that vector transformed by this quaternion
	 **/
	fun sandwich(that: Vector3D): Vector3D = (this * QuaternionD(0.0, that) / this).xyz

	/**
	 * Sandwiches the unit X vector
	 *
	 * First column of rotation matrix in
	 * https://en.wikipedia.org/wiki/Quaternions_and_spatial_rotation#Conversion_to_and_from_the_matrix_representation
	 */
	fun sandwichUnitX(): Vector3D = Vector3D(
		w * w + x * x - y * y - z * z,
		2.0 * (x * y + w * z),
		2.0 * (x * z - w * y),
	)

	/**
	 * Sandwiches the unit Y vector
	 *
	 * Second column of rotation matrix in
	 * https://en.wikipedia.org/wiki/Quaternions_and_spatial_rotation#Conversion_to_and_from_the_matrix_representation
	 */
	fun sandwichUnitY(): Vector3D = Vector3D(
		2.0 * (x * y - w * z),
		w * w - x * x + y * y - z * z,
		2.0 * (y * z + w * x),
	)

	/**
	 * Sandwiches the unit Z vector
	 *
	 * Third column of rotation matrix in
	 * https://en.wikipedia.org/wiki/Quaternions_and_spatial_rotation#Conversion_to_and_from_the_matrix_representation
	 */
	fun sandwichUnitZ(): Vector3D = Vector3D(
		2.0 * (x * z + w * y),
		2.0 * (y * z - w * x),
		w * w - x * x - y * y + z * z,
	)

	/**
	 * computes this quaternion's unit length rotation axis
	 * @return rotation axis
	 **/
	fun axis(): Vector3D = xyz.unit()

	/**
	 * computes the rotation vector representing this quaternion's rotation
	 * @return rotation vector
	 **/
	fun toRotationVector(): Vector3D = 2.0 * twinNearest(IDENTITY).log().xyz

	@Suppress("ktlint")
	/**
	 * computes the matrix representing this quaternion's rotation
	 * @return rotation matrix
	 **/
	fun toMatrix(): Matrix3D {
		val d = lenSq()
		return Matrix3D(
			(w*w + x*x - y*y - z*z)/d ,      2.0*(x*y - w*z)/d     ,      2.0*(w*y + x*z)/d     ,
			     2.0*(x*y + w*z)/d     , (w*w - x*x + y*y - z*z)/d ,      2.0*(y*z - w*x)/d     ,
			     2.0*(x*z - w*y)/d     ,      2.0*(w*x + y*z)/d     , (w*w - x*x - y*y + z*z)/d )
	}

	/**
	 * computes the euler angles representing this quaternion's rotation
	 * @param order the order in which to decompose this quaternion into euler angles
	 * @return euler angles
	 **/
	fun toEulerAngles(order: EulerOrder): EulerAnglesD = this.toMatrix().toEulerAnglesAssumingOrthonormal(order)

	fun toObject() = ObjectQuaternionD(w, x, y, z)

	fun toFloat() = Quaternion(w.toFloat(), x.toFloat(), y.toFloat(), z.toFloat())
}

data class ObjectQuaternionD(val w: Double, val x: Double, val y: Double, val z: Double) {
	fun toValue() = QuaternionD(w, x, y, z)
}

operator fun Double.plus(that: QuaternionD): QuaternionD = that + this
operator fun Double.minus(that: QuaternionD): QuaternionD = -that + this
operator fun Double.times(that: QuaternionD): QuaternionD = that * this
operator fun Double.div(that: QuaternionD): QuaternionD = that.inv() * this
