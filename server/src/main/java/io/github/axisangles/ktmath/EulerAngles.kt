@file:Suppress("unused")

package io.github.axisangles.ktmath

import kotlin.math.cos
import kotlin.math.sin

enum class EulerOrder { XYZ, YZX, ZXY, ZYX, YXZ, XZY }

data class EulerAngles(val order: EulerOrder, val x: Float, val y: Float, val z: Float) {
	/**
	 * creates a quaternion which represents the same rotation as this eulerAngles
	 * @return the quaternion
	 */
	fun toQuaternion(): Quaternion {
		val cX = cos(x / 2f)
		val cY = cos(y / 2f)
		val cZ = cos(z / 2f)
		val sX = sin(x / 2f)
		val sY = sin(y / 2f)
		val sZ = sin(z / 2f)

		return when (order) {
			EulerOrder.XYZ -> Quaternion(
				cX * cY * cZ - sX * sY * sZ,
				cY * cZ * sX + cX * sY * sZ,
				cX * cZ * sY - cY * sX * sZ,
				cZ * sX * sY + cX * cY * sZ
			)
			EulerOrder.YZX -> Quaternion(
				cX * cY * cZ - sX * sY * sZ,
				cY * cZ * sX + cX * sY * sZ,
				cX * cZ * sY + cY * sX * sZ,
				cX * cY * sZ - cZ * sX * sY
			)
			EulerOrder.ZXY -> Quaternion(
				cX * cY * cZ - sX * sY * sZ,
				cY * cZ * sX - cX * sY * sZ,
				cX * cZ * sY + cY * sX * sZ,
				cZ * sX * sY + cX * cY * sZ
			)
			EulerOrder.ZYX -> Quaternion(
				cX * cY * cZ + sX * sY * sZ,
				cY * cZ * sX - cX * sY * sZ,
				cX * cZ * sY + cY * sX * sZ,
				cX * cY * sZ - cZ * sX * sY
			)
			EulerOrder.YXZ -> Quaternion(
				cX * cY * cZ + sX * sY * sZ,
				cY * cZ * sX + cX * sY * sZ,
				cX * cZ * sY - cY * sX * sZ,
				cX * cY * sZ - cZ * sX * sY
			)
			EulerOrder.XZY -> Quaternion(
				cX * cY * cZ + sX * sY * sZ,
				cY * cZ * sX - cX * sY * sZ,
				cX * cZ * sY - cY * sX * sZ,
				cZ * sX * sY + cX * cY * sZ
			)
		}
	}

	// temp, replace with direct conversion later
	// fun toMatrix(): Matrix3 = this.toQuaternion().toMatrix()
	/**
	 * creates a matrix which represents the same rotation as this eulerAngles
	 * @return the matrix
	 */
	fun toMatrix(): Matrix3 {
		val cX = cos(x)
		val cY = cos(y)
		val cZ = cos(z)
		val sX = sin(x)
		val sY = sin(y)
		val sZ = sin(z)

		return when (order) {
			// ktlint ruining spacing
			/* ktlint-disable */
			EulerOrder.XYZ -> Matrix3(
					  cY*cZ      ,      -cY*sZ      ,        sY        ,
				cZ*sX*sY + cX*sZ , cX*cZ - sX*sY*sZ ,      -cY*sX      ,
				sX*sZ - cX*cZ*sY , cZ*sX + cX*sY*sZ ,       cX*cY      )

			EulerOrder.YZX -> Matrix3(
					  cY*cZ      , sX*sY - cX*cY*sZ , cX*sY + cY*sX*sZ ,
					   sZ        ,       cX*cZ      ,      -cZ*sX      ,
					 -cZ*sY      , cY*sX + cX*sY*sZ , cX*cY - sX*sY*sZ )

			EulerOrder.ZXY -> Matrix3(
				cY*cZ - sX*sY*sZ ,      -cX*sZ      , cZ*sY + cY*sX*sZ ,
				cZ*sX*sY + cY*sZ ,       cX*cZ      , sY*sZ - cY*cZ*sX ,
					 -cX*sY      ,        sX        ,       cX*cY      )

			EulerOrder.ZYX -> Matrix3(
					  cY*cZ      , cZ*sX*sY - cX*sZ , cX*cZ*sY + sX*sZ ,
					  cY*sZ      , cX*cZ + sX*sY*sZ , cX*sY*sZ - cZ*sX ,
					  -sY        ,       cY*sX      ,       cX*cY      )

			EulerOrder.YXZ -> Matrix3(
				cY*cZ + sX*sY*sZ , cZ*sX*sY - cY*sZ ,       cX*sY      ,
					  cX*sZ      ,       cX*cZ      ,       -sX        ,
				cY*sX*sZ - cZ*sY , cY*cZ*sX + sY*sZ ,       cX*cY      )

			EulerOrder.XZY -> Matrix3(
					  cY*cZ      ,       -sZ        ,       cZ*sY      ,
				sX*sY + cX*cY*sZ ,       cX*cZ      , cX*sY*sZ - cY*sX ,
				cY*sX*sZ - cX*sY ,       cZ*sX      , cX*cY + sX*sY*sZ )
			/* ktlint-enable */
		}
	}
}
