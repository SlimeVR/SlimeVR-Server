package dev.slimevr.stayaligned.todo

import dev.slimevr.math.angle.Angle
import dev.slimevr.math.angle.AngleErrors
import dev.slimevr.stayaligned.todo.TrackerYaw.extraYaw
import dev.slimevr.stayaligned.todo.TrackerYaw.trackerYaw
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerState
import dev.slimevr.util.Side

/**
 * Assumes that the body is centered around an average yaw, and returns the error of the
 * tracker with respect to that average yaw.
 */
class CenterErrorVisitor(
	val centerYaw: Angle,
	val relaxedPose: RelaxedPose,
	val errors: AngleErrors,
) : TrackerGroup.TrackerVisitor {

	override fun visitHeadTracker(
		trackerState: TrackerState,
		belowUpperBody: TrackerState?,
	) {
		errors.add(centerYaw - trackerYaw(trackerState))
	}

	override fun visitUpperBodyTracker(
		trackerState: TrackerState,
		aboveHeadOrUpperBody: TrackerState?,
		belowUpperBody: TrackerState?,
	) {
		errors.add(centerYaw - trackerYaw(trackerState))
	}

	override fun visitUpperBodyTracker(
		trackerState: TrackerState,
		aboveHeadOrUpperBody: TrackerState?,
		belowLeftUpperLeg: TrackerState?,
		belowRightUpperLeg: TrackerState?,
	) {
		errors.add(centerYaw - trackerYaw(trackerState))
	}

	override fun visitArmTracker(
		side: Side,
		tracker: TrackerState,
		aboveUpperBodyOrArm: TrackerState?,
		belowHandOrArm: TrackerState?,
	) {
		// No error because arms can go anywhere
	}

	override fun visitHandTracker(
		side: Side,
		tracker: TrackerState,
		aboveArm: TrackerState?,
		oppositeHand: TrackerState?,
	) {
		// No error because hands can go anywhere
	}

	override fun visitUpperLegTracker(
		side: Side,
		tracker: TrackerState,
		aboveUpperBody: TrackerState?,
		belowLowerLeg: TrackerState?,
		oppositeUpperLeg: TrackerState?,
	) {
		errors.add(centerYaw + extraYaw(side, relaxedPose.upperLeg) - trackerYaw(tracker))
	}

	override fun visitLowerLegTracker(
		side: Side,
		tracker: TrackerState,
		aboveUpperLeg: TrackerState?,
		belowFoot: TrackerState?,
		oppositeLowerLeg: TrackerState?,
	) {
		errors.add(centerYaw + extraYaw(side, relaxedPose.lowerLeg) - trackerYaw(tracker))
	}

	override fun visitFootTracker(
		side: Side,
		tracker: TrackerState,
		aboveLowerLeg: TrackerState?,
		oppositeFoot: TrackerState?,
	) {
		errors.add(centerYaw + extraYaw(side, relaxedPose.foot) - trackerYaw(tracker))
	}
}
