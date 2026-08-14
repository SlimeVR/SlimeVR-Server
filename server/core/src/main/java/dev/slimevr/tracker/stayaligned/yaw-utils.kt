package dev.slimevr.tracker.stayaligned

import dev.slimevr.math.angle.Angle
import dev.slimevr.tracker.TrackerState
import dev.slimevr.tracker.applyCalibration
import dev.slimevr.util.Side
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.abs
import kotlin.math.atan2

/**
 * Utilities for trackers' yaw.
 *
 * The SlimeVR coordinate system is x-right, y-up, z-back, which is a right-handed
 * coordinate system.
 *
 * Rotations follow the right-hand rule, for example, a positive rotation around the
 * y-axis is a counter-clockwise rotation from z to x. From the perspective of a player,
 * left is positive yaw, right is negative yaw.
 */
object YawUtils {

	/**
	 * Gets the yaw between two rotations, for small rotations.
	 *
	 * A locked tracker can be in any rotation, so we cannot use
	 * YawUtils::trackerYaw, which doesn't work for a tracker that is on its
	 * side.
	 *
	 * WARNING: DO NOT USE for large rotations because the chosen axis might have
	 * a very small projection on the yaw plane, which yields a low confidence yaw.
	 *
	 * TODO: It might be possible to pick a different EulerOrder when we encounter
	 * 		singularities, but I wasn't able to get this working correctly.
	 */
    fun yawDifference(
		rotation: Quaternion,
		targetRotation: Quaternion,
	): Angle {
		val targetX = targetRotation.sandwichUnitX()
		val targetY = targetRotation.sandwichUnitY()
		val targetZ = targetRotation.sandwichUnitZ()

		// Find the axis that is closest to the yaw plane
		val axis: Vector3
		val targetAxis: Vector3

		val targetXScore = abs(targetX.dot(Vector3.POS_Y))
		val targetYScore = abs(targetY.dot(Vector3.POS_Y))
		val targetZScore = abs(targetZ.dot(Vector3.POS_Y))

		// The axis that is closest to the yaw plane has the smallest absolute dot
		// product with the Y axis
		if ((targetXScore <= targetYScore) && (targetXScore <= targetZScore)) {
			axis = rotation.sandwichUnitX()
			targetAxis = targetX
		} else if ((targetYScore <= targetXScore) && (targetYScore <= targetZScore)) {
			axis = rotation.sandwichUnitY()
			targetAxis = targetY
		} else {
			axis = rotation.sandwichUnitZ()
			targetAxis = targetZ
		}

		val yaw = Angle.ofRad(atan2(axis.z, axis.x))
		val targetYaw = Angle.ofRad(atan2(targetAxis.z, targetAxis.x))

		return targetYaw - yaw
	}

	/**
	 * Whether we can reliably get the yaw of a tracker.
	 */
	fun hasTrackerYaw(trackerState: TrackerState) = Angle.absBetween(
		getStayAlignedRotation(trackerState).sandwichUnitX(),
		Vector3.POS_Y,
	) > MIN_ON_SIDE_ANGLE

	/**
	 * Gets the yaw of the tracker, for trackers that are not on its side.
	 *
	 * WARNING: DO NOT USE for a tracker that is on its side. Euler YZX angles have a
	 * singularity for a tracker that is on its side, and can yield arbitrary yaws.
	 * For example, the Euler YZX angles (Y=0°, Z=90°, X=30°) and (Y=30°, Z=90°, X=0°)
	 * are equivalent but yield completely different yaws.
	 *
	 * WARNING: It is possible to use another EulerOrder which does not have a
	 * singularity for this rotation to get "some" yaw, but this yaw will be very
	 * different from the from YZX. DO NOT ATTEMPT!
	 */
	fun trackerYaw(trackerState: TrackerState) = Angle.ofRad(
		getStayAlignedRotation(trackerState)
			.toEulerAngles(EulerOrder.YZX)
			.y,
	)

	/**
	 * Applies an extra yaw in the specified direction.
	 */
	fun extraYaw(direction: Side, angle: Angle) = when (direction) {
		Side.LEFT -> angle
		Side.RIGHT -> -angle
	}

	private val MIN_ON_SIDE_ANGLE = Angle.ofDeg(30.0f)
}
