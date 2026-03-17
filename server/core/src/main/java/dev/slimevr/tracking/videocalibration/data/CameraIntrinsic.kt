package dev.slimevr.tracking.videocalibration.data

import io.github.axisangles.ktmath.Vector2D
import io.github.axisangles.ktmath.Vector3D

class CameraIntrinsic(
	val fx: Double,
	val fy: Double,
	val tx: Double,
	val ty: Double,
) {
	/**
	 * Projects a point in camera space into the camera's image space.
	 */
	fun project(pointInCamera: Vector3D): Vector2D? {
		if (pointInCamera.z < 0.1) {
			return null
		}

		return Vector2D(
			pointInCamera.x / pointInCamera.z * fx + tx,
			pointInCamera.y / pointInCamera.z * fy + ty,
		)
	}

	/**
	 * A ray from the camera center to the point on the image plane.
	 */
	fun ray(imagePoint: Vector2D): Vector3D = Vector3D(
		(imagePoint.x - tx) / fx,
		(imagePoint.y - ty) / fy,
		1.0,
	)

	override fun toString() = "Intrinsic(fx=${"%.1f".format(fx)} fy=${"%.1f".format(fy)} tx=${"%.1f".format(tx)} ty=${"%.1f".format(ty)})"
}
