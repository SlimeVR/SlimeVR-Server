package dev.slimevr.stayaligned.todo

import dev.slimevr.math.angle.Angle
import dev.slimevr.math.angle.AngleErrors
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerState
import dev.slimevr.util.Side
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.abs
import kotlin.math.atan2

/**
 * Error between a locked tracker's yaw and its yaw when it was initially locked.
 */
class LockedErrorVisitor(
	val lockedRotation: Quaternion,
	val errors: AngleErrors,
) : TrackerGroup.TrackerVisitor {

	override fun visitHeadTracker(
		trackerState: TrackerState,
		belowUpperBody: TrackerState?,
	) {
		errors.add(error(trackerState))
	}

	override fun visitUpperBodyTracker(
		trackerState: TrackerState,
		aboveHeadOrUpperBody: TrackerState?,
		belowUpperBody: TrackerState?,
	) {
		errors.add(error(trackerState))
	}

	override fun visitUpperBodyTracker(
		trackerState: TrackerState,
		aboveHeadOrUpperBody: TrackerState?,
		belowLeftUpperLeg: TrackerState?,
		belowRightUpperLeg: TrackerState?,
	) {
		errors.add(error(trackerState))
	}

	override fun visitArmTracker(
		side: Side,
		tracker: TrackerState,
		aboveUpperBodyOrArm: TrackerState?,
		belowHandOrArm: TrackerState?,
	) {
		errors.add(error(tracker))
	}

	override fun visitHandTracker(
		side: Side,
		tracker: TrackerState,
		aboveArm: TrackerState?,
		oppositeHand: TrackerState?,
	) {
		errors.add(error(tracker))
	}

	override fun visitUpperLegTracker(
		side: Side,
		tracker: TrackerState,
		aboveUpperBody: TrackerState?,
		belowLowerLeg: TrackerState?,
		oppositeUpperLeg: TrackerState?,
	) {
		errors.add(error(tracker))
	}

	override fun visitLowerLegTracker(
		side: Side,
		tracker: TrackerState,
		aboveUpperLeg: TrackerState?,
		belowFoot: TrackerState?,
		oppositeLowerLeg: TrackerState?,
	) {
		errors.add(error(tracker))
	}

	override fun visitFootTracker(
		side: Side,
		tracker: TrackerState,
		aboveLowerLeg: TrackerState?,
		oppositeFoot: TrackerState?,
	) {
		errors.add(error(tracker))
	}

	private fun error(tracker: Tracker): Angle = yawDifference(tracker.context.state.value.rotation, lockedRotation) // TODO tracker.getAdjustedRotationForceStayAligned()

	companion object {

		/**
		 * Gets the yaw between two rotations, for small rotations.
		 *
		 * A locked tracker can be in any rotation, so we cannot use
		 * TrackerYaw::trackerYaw, which doesn't work for a tracker that is on its
		 * side.
		 *
		 * WARNING: DO NOT USE for large rotations because the chosen axis might have
		 * a very small projection on the yaw plane, which yields a low confidence yaw.
		 *
		 * TODO: It might be possible to pick a different EulerOrder when we encounter
		 * 		singularities, but I wasn't able to get this working correctly.
		 */
		private fun yawDifference(
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
	}
}
