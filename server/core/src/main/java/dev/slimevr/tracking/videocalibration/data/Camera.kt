package dev.slimevr.tracking.videocalibration.data

import io.github.axisangles.ktmath.Vector2D
import io.github.axisangles.ktmath.Vector3D
import java.awt.Dimension

data class Camera(
	val extrinsic: CameraExtrinsic,
	val intrinsic: CameraIntrinsic,
	val imageSize: Dimension,
) {
	/**
	 * Projects a point in the world into the camera's image space.
	 */
	fun project(pointInWorld: Vector3D) = intrinsic.project(extrinsic.toCamera(pointInWorld))

	/**
	 * Finds the image vector that corresponds to projecting a vector in world space.
	 */
	fun project(vectorInWorld: Vector3D, originInImage: Vector2D, depth: Double): Vector2D? {
		val originInWorld = extrinsic.toWorld(intrinsic.ray(originInImage) * depth)
		val tipInWorld = originInWorld + vectorInWorld
		val tipInImage = project(tipInWorld) ?: return null
		return tipInImage - originInImage
	}

	override fun toString() = "Camera($extrinsic $intrinsic $imageSize)"
}
