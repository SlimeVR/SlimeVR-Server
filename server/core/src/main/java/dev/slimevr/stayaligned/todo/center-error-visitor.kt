package dev.slimevr.stayaligned.todo

import dev.slimevr.math.angle.Angle
import dev.slimevr.math.angle.AngleErrors
import dev.slimevr.stayaligned.todo.TrackerYaw.extraYaw
import dev.slimevr.stayaligned.todo.TrackerYaw.trackerYaw
import dev.slimevr.tracker.Tracker
import dev.slimevr.util.Side

/**
 * Assumes that the body is centered around an average yaw, and returns the error of the
 * tracker with respect to that average yaw.
 */
class CenterErrorVisitor(
	val centerYaw: Angle,
	val relaxedPose: RelaxedPose,
	val errors: AngleErrors,
) : TrackerSkeleton.TrackerVisitor {

	override fun visitHeadTracker(
		tracker: Tracker,
		belowUpperBody: Tracker?,
	) {
		errors.add(centerYaw - trackerYaw(tracker))
	}

	override fun visitUpperBodyTracker(
		tracker: Tracker,
		aboveHeadOrUpperBody: Tracker?,
		belowUpperBody: Tracker?,
	) {
		errors.add(centerYaw - trackerYaw(tracker))
	}

	override fun visitUpperBodyTracker(
		tracker: Tracker,
		aboveHeadOrUpperBody: Tracker?,
		belowLeftUpperLeg: Tracker?,
		belowRightUpperLeg: Tracker?,
	) {
		errors.add(centerYaw - trackerYaw(tracker))
	}

	override fun visitArmTracker(
		side: Side,
		tracker: Tracker,
		aboveUpperBodyOrArm: Tracker?,
		belowHandOrArm: Tracker?,
	) {
		// No error because arms can go anywhere
	}

	override fun visitHandTracker(
		side: Side,
		tracker: Tracker,
		aboveArm: Tracker?,
		oppositeHand: Tracker?,
	) {
		// No error because hands can go anywhere
	}

	override fun visitUpperLegTracker(
		side: Side,
		tracker: Tracker,
		aboveUpperBody: Tracker?,
		belowLowerLeg: Tracker?,
		oppositeUpperLeg: Tracker?,
	) {
		errors.add(centerYaw + extraYaw(side, relaxedPose.upperLeg) - trackerYaw(tracker))
	}

	override fun visitLowerLegTracker(
		side: Side,
		tracker: Tracker,
		aboveUpperLeg: Tracker?,
		belowFoot: Tracker?,
		oppositeLowerLeg: Tracker?,
	) {
		errors.add(centerYaw + extraYaw(side, relaxedPose.lowerLeg) - trackerYaw(tracker))
	}

	override fun visitFootTracker(
		side: Side,
		tracker: Tracker,
		aboveLowerLeg: Tracker?,
		oppositeFoot: Tracker?,
	) {
		errors.add(centerYaw + extraYaw(side, relaxedPose.foot) - trackerYaw(tracker))
	}
}
