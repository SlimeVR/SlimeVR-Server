package dev.slimevr.tracking.videocalibration.data

import dev.slimevr.tracking.videocalibration.util.toEulerYZXString
import io.github.axisangles.ktmath.QuaternionD
import io.github.axisangles.ktmath.Vector3D

class CameraExtrinsic(
	val worldToCamera: QuaternionD,
	val worldOriginInCamera: Vector3D,
) {
	val cameraToWorld = worldToCamera.inv()
	val cameraOriginInWorld = -cameraToWorld.sandwich(worldOriginInCamera)

	/**
	 * Transforms a point in world space to camera space.
	 */
	fun toCamera(pointInWorld: Vector3D) = worldToCamera.sandwich(pointInWorld) + worldOriginInCamera

	/**
	 * Transforms a point in camera space to world space.
	 */
	fun toWorld(pointInCamera: Vector3D) = cameraToWorld.sandwich(pointInCamera) + cameraOriginInWorld

	override fun toString(): String = "Extrinsic(cameraToWorld=${cameraToWorld.toEulerYZXString()} cameraOriginInWorld=$cameraOriginInWorld)"

	companion object {

		fun fromCameraPose(cameraToWorld: QuaternionD, cameraOriginInWorld: Vector3D): CameraExtrinsic {
			val worldToCamera = cameraToWorld.inv()
			return CameraExtrinsic(worldToCamera, -worldToCamera.sandwich(cameraOriginInWorld))
		}
	}
}
