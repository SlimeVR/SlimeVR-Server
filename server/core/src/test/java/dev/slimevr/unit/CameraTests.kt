package dev.slimevr.unit

import dev.slimevr.tracking.videocalibration.data.Camera
import dev.slimevr.tracking.videocalibration.data.CameraExtrinsic
import dev.slimevr.tracking.videocalibration.data.CameraIntrinsic
import io.github.axisangles.ktmath.QuaternionD
import io.github.axisangles.ktmath.Vector2D
import io.github.axisangles.ktmath.Vector3D
import java.awt.Dimension
import kotlin.test.Test
import kotlin.test.assertTrue

class CameraTests {

	@Test
	fun testProject() {
		val camera =
			Camera(
				CameraExtrinsic.fromCameraPose(QuaternionD.IDENTITY, Vector3D(0.0, 0.0, -3.0)),
				CameraIntrinsic(1000.0, 1000.0, 500.0, 500.0),
				Dimension(1000, 1000),
			)

		val p0 = Vector3D(0.0, 0.0, 0.0)
		assertTrue(camera.project(p0)!!.isNear(Vector2D(500.0, 500.0)))

		val p1 = Vector3D(1.0, 0.0, 0.0)
		assertTrue(camera.project(p1)!!.isNear(Vector2D(500.0 + 1000.0 / 3.0, 500.0)))

		val p2 = Vector3D(1.0, 1.0, 1.0)
		assertTrue(camera.project(p2)!!.isNear(Vector2D(500.0 + 1000.0 / 4.0, 500.0 + 1000.0 / 4.0)))
	}
}
