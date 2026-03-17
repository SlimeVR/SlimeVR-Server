package dev.slimevr.tracking.videocalibration.data

import dev.slimevr.tracking.videocalibration.util.toEulerYZXString
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.QuaternionD

/**
 * Converts a tracker's rotation into its bone's rotation.
 */
data class TrackerResetOverride(
	val globalYaw: Double,
	val localRotation: QuaternionD,
) {
	private val globalRotation = QuaternionD.rotationAroundYAxis(globalYaw)

	fun toBoneRotation(trackerRotation: QuaternionD): QuaternionD {
		val rotation = (globalRotation * trackerRotation * localRotation).twinNearest(QuaternionD.IDENTITY)
		return rotation
	}

	fun toBoneRotation(trackerRotation: Quaternion): Quaternion {
		val rotation = toBoneRotation(trackerRotation.toDouble())
		return rotation.toFloat()
	}

	override fun toString() = "TrackerReset(global_yaw=${globalRotation.toEulerYZXString()} local=${localRotation.toEulerYZXString()})"
}
